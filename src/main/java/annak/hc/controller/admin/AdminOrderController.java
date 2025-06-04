package annak.hc.controller.admin;

import annak.hc.dto.OrderDto;
import annak.hc.entity.embedded.Status;
import annak.hc.service.GiftSetService;
import annak.hc.service.OrderItemService;
import annak.hc.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

import static annak.hc.config.GlobalVariables.*;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private static final String REDIRECT_ADMIN_ORDERS = "redirect:/admin/orders";
    private static final String REDIRECT_ADMIN_ORDERS_BY_STATUS = "redirect:/admin/orders/{statusName}";

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final GiftSetService giftSetService;

    @GetMapping
    public String getMenuWithOrderStatuses(Model model) {
        model.addAttribute("statuses", Status.values());
        return "admin/orders_menu";
    }

    @GetMapping("/{statusName}")
    public String getAllOrdersByStatus(@PathVariable String statusName, Model model) {
        model.addAttribute("status_name", statusName);
        model.addAttribute(ORDERS, orderService.getAllByStatusName(statusName));
        return "admin/list_of_orders";
    }

    @GetMapping("/{statusName}/{id}")
    public String getOrderById(@PathVariable String statusName, @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<OrderDto> orderDtoOptional = orderService.getById(id);
        if (orderDtoOptional.isPresent()) {
            if (orderDtoOptional.get().getStatus().equals(Status.valueOf(statusName.toUpperCase()))) {
                model.addAttribute(ORDER, orderDtoOptional.get());
                model.addAttribute("orderItems", orderItemService.getAllDtosByOrderId(orderDtoOptional.get().getId()));
                return "admin/order";
            }
            redirectAttributes.addFlashAttribute(MESSAGE, "Замовлення №%s має інший статус!".formatted(id));
            return REDIRECT_ADMIN_ORDERS_BY_STATUS;
        }
        redirectAttributes.addFlashAttribute(MESSAGE, "Замовлення №%s не було знайдено!".formatted(id));
        return REDIRECT_ADMIN_ORDERS_BY_STATUS;
    }

    @GetMapping("/{orderId}/giftSets/{giftSetId}")
    public String getGiftSetInOrder(@PathVariable Long orderId, @PathVariable Long giftSetId,
                                    Model model, RedirectAttributes redirectAttributes) {
        Optional<OrderDto> orderDtoOptional = orderService.getById(orderId);
        if (orderDtoOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Замовлення №%s не було знайдено!".formatted(orderId));
            return REDIRECT_ADMIN_ORDERS;
        }
        var giftSetOptional = giftSetService.getEntityById(giftSetId);
        if (giftSetOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Подарунковий набір з id <%s> не знайдено!".formatted(giftSetId));
            return REDIRECT_ADMIN_ORDERS;
        }
        if (orderItemService.getByOrderIdAndGiftSetId(orderId, giftSetId).isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE,
                    "Подарунковий набір з id <%s> не належить до замовлення з id <%s>!".formatted(giftSetId, orderId));
            return REDIRECT_ADMIN_ORDERS;
        }
        var giftSet = giftSetOptional.get();
        model.addAttribute("giftSet", giftSet);
        model.addAttribute("items", giftSet.getItems());
        model.addAttribute(TOTAL_PRICE, giftSet.getPrice());
        return "gift_set";
    }

    @PutMapping("/{id}/acceptOrderWithPickup")
    public ResponseEntity<Void> acceptOrderWithPickup(@PathVariable Long id, @RequestBody LocalDateTime receiptDate) {
        Optional<OrderDto> orderDtoOptional = orderService.getById(id);
        if (orderDtoOptional.isEmpty())
            return ResponseEntity.notFound().build();
        OrderDto orderDto = orderDtoOptional.get();
        if (orderDto.getStatus().getId() != 1)
            return ResponseEntity.badRequest().build();

        orderDto.setReceiptDate(receiptDate);
        orderDto.setStatus(Status.ACCEPTED);
        orderService.update(orderDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/acceptOrderWithDelivery")
    public ResponseEntity<Void> acceptOrderWithDelivery(@PathVariable Long id) {
        Optional<OrderDto> orderDtoOptional = orderService.getById(id);
        if (orderDtoOptional.isEmpty())
            return ResponseEntity.notFound().build();
        OrderDto orderDto = orderDtoOptional.get();
        if (orderDto.getStatus().getId() != 1)
            return ResponseEntity.badRequest().build();

        orderDto.setStatus(Status.ACCEPTED);
        orderService.update(orderDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/declineOrder")
    public ResponseEntity<Void> declineOrder(@PathVariable Long id) {
        Optional<OrderDto> orderDtoOptional = orderService.getById(id);
        if (orderDtoOptional.isEmpty())
            return ResponseEntity.notFound().build();
        OrderDto orderDto = orderDtoOptional.get();
        if (orderDto.getStatus().getId() != 1)
            return ResponseEntity.badRequest().build();

        orderService.cancel(orderDtoOptional.get());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/markOrderAsForwardedForDelivery")
    public ResponseEntity<Void> markOrderAsForwardedForDelivery(@PathVariable Long id, @RequestBody Long invoiceNumber) {
        Optional<OrderDto> orderDtoOptional = orderService.getById(id);
        if (orderDtoOptional.isEmpty())
            return ResponseEntity.notFound().build();
        OrderDto orderDto = orderDtoOptional.get();
        if (orderDto.getTypeOfReceipt().getId() != 2 || orderDto.getStatus().getId() != 2)
            return ResponseEntity.badRequest().build();

        orderDto.setInvoiceNumber(invoiceNumber);
        orderDto.setStatus(Status.FORWARDED_FOR_DELIVERY);
        orderService.update(orderDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/markOrderAsReceived")
    public ResponseEntity<Void> markOrderAsReceived(@PathVariable Long id) {
        Optional<OrderDto> orderDtoOptional = orderService.getById(id);
        if (orderDtoOptional.isEmpty())
            return ResponseEntity.notFound().build();
        OrderDto orderDto = orderDtoOptional.get();
        if ((orderDto.getTypeOfReceipt().getId() == 1 && orderDto.getStatus().getId() != 2) ||
                (orderDto.getTypeOfReceipt().getId() == 2 && orderDto.getStatus().getId() != 3))
            return ResponseEntity.badRequest().build();

        orderDto.setStatus(Status.RECEIVED);
        orderService.update(orderDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/find")
    public String findOrders(Model model,
                             @RequestParam(name = "userPhone", required = false) Long userPhone,
                             @RequestParam(name = "orderId", required = false) Long orderId) {
        if (userPhone == null && orderId == null) {
            model.addAttribute("searchFlag", false);
            return "admin/find_orders";
        }
        model.addAttribute("searchFlag", true);
        if (userPhone != null) {
            model.addAttribute(ORDERS, orderService.getAllByUserPhone(userPhone));
        } else {
            model.addAttribute(ORDERS, orderService.getById(orderId).stream().toList());
        }
        return "admin/find_orders";
    }
}
