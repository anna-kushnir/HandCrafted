package annak.hc.controller.user;

import annak.hc.dto.CartItemDto;
import annak.hc.dto.NewOrderDto;
import annak.hc.dto.OrderDto;
import annak.hc.entity.User;
import annak.hc.entity.embedded.TypeOfReceipt;
import annak.hc.exception.ResourceNotFoundException;
import annak.hc.service.CartItemService;
import annak.hc.service.GiftSetService;
import annak.hc.service.OrderItemService;
import annak.hc.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static annak.hc.config.GlobalVariables.*;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final String REDIRECT_ORDERS = "redirect:/orders";
    private static final String REDIRECT_CART = "redirect:/cart";

    private final UserDetailsService userDetailsService;
    private final OrderService orderService;
    private final CartItemService cartItemService;
    private final OrderItemService orderItemService;
    private final GiftSetService giftSetService;

    @GetMapping
    public String getAllOrders(Principal principal, Model model) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        List<OrderDto> orderDtoList = orderService.getAllByUser(user);
        model.addAttribute(ORDERS, orderDtoList);
        return "user/list_of_orders";
    }

    @GetMapping("/{id}")
    public String getOrderById(@PathVariable Long id, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Optional<OrderDto> orderDtoOptional = orderService.getById(id);
        if (orderDtoOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Замовлення №%s не було знайдено!".formatted(id));
            return REDIRECT_ORDERS;
        }
        OrderDto orderDto = orderDtoOptional.get();
        if (!user.equals(orderDto.getUser())) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Ви можете переглянути лише свої власні замовлення!");
            return REDIRECT_ORDERS;
        }
        model.addAttribute(ORDER, orderDto);
        model.addAttribute("orderItems", orderItemService.getAllDtosByOrderId(orderDto.getId()));
        return "user/order";
    }

    @GetMapping("/{orderId}/giftSets/{giftSetId}")
    public String getGiftSetInOrder(Principal principal, @PathVariable Long orderId, @PathVariable Long giftSetId,
                                    Model model, RedirectAttributes redirectAttributes) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Optional<OrderDto> orderDtoOptional = orderService.getById(orderId);
        if (orderDtoOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Замовлення №%s не було знайдено!".formatted(orderId));
            return REDIRECT_ORDERS;
        }
        OrderDto orderDto = orderDtoOptional.get();
        if (!user.equals(orderDto.getUser())) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Ви можете переглянути лише свої власні замовлення!");
            return REDIRECT_ORDERS;
        }
        var giftSetOptional = giftSetService.getEntityById(giftSetId);
        if (giftSetOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Подарунковий набір з id <%s> не знайдено!".formatted(giftSetId));
            return REDIRECT_ORDERS;
        }
        if (orderItemService.getByOrderIdAndGiftSetId(orderId, giftSetId).isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE,
                    "Подарунковий набір з id <%s> не належить до замовлення з id <%s>!".formatted(giftSetId, orderId));
            return REDIRECT_ORDERS;
        }
        var giftSet = giftSetOptional.get();
        model.addAttribute("giftSet", giftSet);
        model.addAttribute("items", giftSet.getItems());
        model.addAttribute(TOTAL_PRICE, giftSet.getPrice());
        return "gift_set";
    }

    @GetMapping("/new")
    public String viewProductsToOrder(Principal principal, Model model) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        List<CartItemDto> cartItemDtoList = cartItemService.getAllByUser(user);
        model.addAttribute("cartItems", cartItemDtoList);
        model.addAttribute(TOTAL_PRICE, cartItemService.getTotalPriceOfItemsInCart(cartItemDtoList));
        return "user/create_order";
    }

    @GetMapping("/new/fillInTheOrderData")
    public String fillInTheOrderData(Principal principal, Model model) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        NewOrderDto orderDto = new NewOrderDto();
        orderDto.setUserPhone(user.getUserPhone());
        model.addAttribute(ORDER, orderDto);
        model.addAttribute("typesOfReceipt", TypeOfReceipt.values());
        return "user/fill_order_data";
    }

    @PostMapping("/new")
    public String createOrder(Principal principal, @ModelAttribute("order") NewOrderDto newOrderDto, RedirectAttributes redirectAttributes) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        newOrderDto.setUser(user);
        try {
            String result = orderService.save(newOrderDto, cartItemService.getAllByUser(user));
            redirectAttributes.addFlashAttribute(MESSAGE, result);
            return REDIRECT_ORDERS;
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute(MESSAGE, e.getMessage());
            return REDIRECT_CART;
        }
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> cancelOrderById(@PathVariable Long id, Principal principal) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Optional<OrderDto> orderDtoOptional = orderService.getById(id);
        if (orderDtoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        OrderDto orderDto = orderDtoOptional.get();
        if (!user.equals(orderDto.getUser())) {
            return ResponseEntity.badRequest().build();
        }
        orderService.cancel(orderDto);
        return ResponseEntity.ok().build();
    }
}
