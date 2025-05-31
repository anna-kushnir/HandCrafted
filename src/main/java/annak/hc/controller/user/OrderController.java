package annak.hc.controller.user;

import annak.hc.dto.CartItemDto;
import annak.hc.dto.NewOrderDto;
import annak.hc.dto.OrderDto;
import annak.hc.entity.User;
import annak.hc.entity.embedded.TypeOfReceipt;
import annak.hc.service.CartItemService;
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

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final UserDetailsService userDetailsService;
    private final OrderService orderService;
    private final CartItemService cartItemService;
    private final OrderItemService orderItemService;

    @GetMapping
    public String getAllOrders(Principal principal, Model model) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        List<OrderDto> orderDtoList = orderService.getAllByUser(user);
        model.addAttribute("orders", orderDtoList);
        return "user/list_of_orders";
    }

    @GetMapping("/{id}")
    public String getOrderById(@PathVariable Long id, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Optional<OrderDto> orderDtoOptional = orderService.getById(id);
        if (orderDtoOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Замовлення №%s не було знайдено!".formatted(id));
            return "redirect:/orders";
        }
        OrderDto orderDto = orderDtoOptional.get();
        if (!user.equals(orderDto.getUser())) {
            redirectAttributes.addFlashAttribute("message", "Ви можете переглянути лише свої власні замовлення!");
            return "redirect:/orders";
        }
        model.addAttribute("order", orderDto);
        model.addAttribute("orderItems", orderItemService.getAllDtosByOrderId(orderDto.getId()));
        return "user/order";
    }

    @GetMapping("/new")
    public String viewProductsToOrder(Principal principal, Model model) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        List<CartItemDto> cartItemDtoList = cartItemService.getAllByUser(user);
        model.addAttribute("cartItems", cartItemDtoList);
        model.addAttribute("totalPrice", cartItemService.getTotalPriceOfItemsInCart(cartItemDtoList));
        return "user/create_order";
    }

    @GetMapping("/new/fillInTheOrderData")
    public String fillInTheOrderData(Principal principal, Model model) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        NewOrderDto orderDto = new NewOrderDto();
        orderDto.setUserPhone(user.getUserPhone());
        model.addAttribute("order", orderDto);
        model.addAttribute("typesOfReceipt", TypeOfReceipt.values());
        return "user/fill_order_data";
    }

    @PostMapping("/new")
    public String createOrder(Principal principal, @ModelAttribute("order") NewOrderDto newOrderDto, RedirectAttributes redirectAttributes) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        newOrderDto.setUser(user);
        String result = orderService.save(newOrderDto, cartItemService.getAllByUser(user));
        redirectAttributes.addFlashAttribute("message", result);
        return "redirect:/orders";
    }

//    TODO: додати повернення товарів з giftSet в наявність
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> cancelOrderById(@PathVariable Long id, Principal principal) {
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
