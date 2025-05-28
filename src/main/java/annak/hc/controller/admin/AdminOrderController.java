package annak.hc.controller.admin;

import annak.hc.dto.OrderDto;
import annak.hc.entity.embedded.Status;
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

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @GetMapping
    public String getMenuWithOrderStatuses(Model model) {
        model.addAttribute("statuses", Status.values());
        return "admin/orders_menu";
    }

    @GetMapping("/{statusName}")
    public String getAllOrdersByStatus(@PathVariable String statusName, Model model) {
        model.addAttribute("status_name", statusName);
        model.addAttribute("orders", orderService.getAllByStatusName(statusName));
        return "admin/list_of_orders";
    }

    @GetMapping("/{statusName}/{id}")
    public String getOrderById(@PathVariable String statusName, @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<OrderDto> orderDtoOptional = orderService.getById(id);
        if (orderDtoOptional.isPresent()) {
            if (orderDtoOptional.get().getStatus().equals(Status.valueOf(statusName.toUpperCase()))) {
                model.addAttribute("order", orderDtoOptional.get());
                model.addAttribute("orderItems", orderItemService.getAllDtosByOrderId(orderDtoOptional.get().getId()));
                return "admin/order";
            }
            redirectAttributes.addFlashAttribute("message", "Замовлення №%s має інший статус!".formatted(id));
            return "redirect:/admin/orders/{statusName}";
        }
        redirectAttributes.addFlashAttribute("message", "Замовлення №%s не було знайдено!".formatted(id));
        return "redirect:/admin/orders/{statusName}";
    }

    @PutMapping("/{id}/acceptOrderWithPickup")
    public ResponseEntity<?> acceptOrderWithPickup(@PathVariable Long id, @RequestBody LocalDateTime receiptDate) {
        Optional<OrderDto> orderDtoOptional = orderService.getById(id);
        if (orderDtoOptional.isEmpty())
            return ResponseEntity.notFound().build();
        OrderDto orderDto = orderDtoOptional.get();
        if (orderDto.getStatus().getId() != 1)
            return ResponseEntity.badRequest().build();

        orderDto.setReceiptDate(receiptDate);
        orderDto.setStatus(Status.ACCEPTED);
        orderService.update(orderDto, orderItemService.getAllDtosByOrderId(orderDto.getId()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/acceptOrderWithDelivery")
    public ResponseEntity<?> acceptOrderWithDelivery(@PathVariable Long id) {
        Optional<OrderDto> orderDtoOptional = orderService.getById(id);
        if (orderDtoOptional.isEmpty())
            return ResponseEntity.notFound().build();
        OrderDto orderDto = orderDtoOptional.get();
        if (orderDto.getStatus().getId() != 1)
            return ResponseEntity.badRequest().build();

        orderDto.setStatus(Status.ACCEPTED);
        orderService.update(orderDto, orderItemService.getAllDtosByOrderId(orderDto.getId()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/declineOrder")
    public ResponseEntity<?> declineOrder(@PathVariable Long id) {
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
    public ResponseEntity<?> markOrderAsForwardedForDelivery(@PathVariable Long id, @RequestBody Long invoiceNumber) {
        Optional<OrderDto> orderDtoOptional = orderService.getById(id);
        if (orderDtoOptional.isEmpty())
            return ResponseEntity.notFound().build();
        OrderDto orderDto = orderDtoOptional.get();
        if (orderDto.getTypeOfReceipt().getId() != 2 || orderDto.getStatus().getId() != 2)
            return ResponseEntity.badRequest().build();

        orderDto.setInvoiceNumber(invoiceNumber);
        orderDto.setStatus(Status.FORWARDED_FOR_DELIVERY);
        orderService.update(orderDto, orderItemService.getAllDtosByOrderId(orderDto.getId()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/markOrderAsReceived")
    public ResponseEntity<?> markOrderAsReceived(@PathVariable Long id) {
        Optional<OrderDto> orderDtoOptional = orderService.getById(id);
        if (orderDtoOptional.isEmpty())
            return ResponseEntity.notFound().build();
        OrderDto orderDto = orderDtoOptional.get();
        if ((orderDto.getTypeOfReceipt().getId() == 1 && orderDto.getStatus().getId() != 2) ||
                (orderDto.getTypeOfReceipt().getId() == 2 && orderDto.getStatus().getId() != 3))
            return ResponseEntity.badRequest().build();

        orderDto.setStatus(Status.RECEIVED);
        orderService.update(orderDto, orderItemService.getAllDtosByOrderId(orderDto.getId()));
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
            model.addAttribute("orders", orderService.getAllByUserPhone(userPhone));
        } else {
            model.addAttribute("orders", orderService.getById(orderId).stream().toList());
        }
        return "admin/find_orders";
    }
}
