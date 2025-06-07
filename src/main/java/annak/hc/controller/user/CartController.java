package annak.hc.controller.user;

import annak.hc.dto.CartItemDto;
import annak.hc.dto.GiftSetDto;
import annak.hc.dto.GuestCartItemDto;
import annak.hc.dto.ProductDto;
import annak.hc.entity.User;
import annak.hc.service.*;
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
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private static final String REDIRECT_CART = "redirect:/cart";

    private final UserDetailsService userDetailsService;
    private final CartItemService cartItemService;
    private final FavoriteProductService favoriteProductService;
    private final ProductService productService;
    private final GiftSetService giftSetService;

    @GetMapping
    public String getAllCartItems(Principal principal, Model model) {
        if (principal != null) {
            var user = (User) userDetailsService.loadUserByUsername(principal.getName());
            List<CartItemDto> cartItemDtoList = cartItemService.getAllByUser(user);
            model.addAttribute("cartItems", cartItemDtoList);
            model.addAttribute(TOTAL_PRICE, cartItemService.getTotalPriceOfItemsInCart(cartItemDtoList));
            model.addAttribute(IS_AUTHENTICATED, true);
        } else {
            model.addAttribute(IS_AUTHENTICATED, false);
        }
        return "guest/cart";
    }

    @GetMapping("/giftSets/{id}")
    public String getGiftSetInCart(Principal principal, @PathVariable Long id,
                                   Model model, RedirectAttributes redirectAttributes) {
        var giftSetOptional = giftSetService.getEntityById(id);
        if (giftSetOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Подарунковий набір з id <%s> не знайдено!".formatted(id));
            return REDIRECT_CART;
        }
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        if (cartItemService.getByUserAndGiftSetId(user, id).isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Ви можете переглянути лише власні подарункові набори!");
            return REDIRECT_CART;
        }
        var giftSet = giftSetOptional.get();
        for (var giftSetItem : giftSet.getItems()) {
            giftSetItem.setProductCost(giftSetItem.getProduct().isWithDiscount() ?
                    giftSetItem.getProduct().getDiscountedPrice() :
                    giftSetItem.getProduct().getPrice());
        }
        model.addAttribute("giftSet", giftSet);
        model.addAttribute("items", giftSet.getItems());
        model.addAttribute("itemsPrice", giftSetService.countTotalPriceForItems(giftSet.getItems()));
        return "gift_set";
    }

    @PostMapping("/guest")
    @ResponseBody
    public List<CartItemDto> getGuestCartItems(@RequestBody List<GuestCartItemDto> items) {
        return cartItemService.convertGuestItemsToDto(items);
    }

    @PostMapping("/merge")
    public ResponseEntity<Void> mergeCart(Principal principal, @RequestBody List<GuestCartItemDto> guestCartItems) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        cartItemService.mergeCartItems(user, guestCartItems);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping ("/products/{id}/deleteFromCart")
    public ResponseEntity<Void> deleteProductWithIdFromCart(Principal principal, @PathVariable Long id) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Optional<ProductDto> productDtoOptional = productService.getNotDeletedById(id);
        if (productDtoOptional.isPresent()) {
            var productDto = productDtoOptional.get();
            cartItemService.delete(user, productDto);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping ("/giftSets/{id}/deleteFromCart")
    public ResponseEntity<Void> deleteGiftSetWithIdFromCart(Principal principal, @PathVariable Long id) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Optional<GiftSetDto> giftSetDtoOptional = giftSetService.getById(id);
        if (giftSetDtoOptional.isPresent()) {
            var giftSetDto = giftSetDtoOptional.get();
            cartItemService.delete(user, giftSetDto);
            giftSetService.deleteById(giftSetDto.getId());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping ("/products/{id}/transferToFavorite")
    public ResponseEntity<Void> transferProductWithIdToFavorite(Principal principal, @PathVariable Long id) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Optional<ProductDto> productDtoOptional = productService.getById(id);
        if (productDtoOptional.isPresent()) {
            var productDto = productDtoOptional.get();
            cartItemService.delete(user, productDto);
            favoriteProductService.save(user, productDto);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping ("/products/{id}/updateQuantity/{quantity}")
    public ResponseEntity<Void> updateQuantity(Principal principal, @PathVariable Long id,
                                               @PathVariable Long quantity) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Optional<ProductDto> productDtoOptional = productService.getById(id);
        if (productDtoOptional.isPresent()) {
            var productDto = productDtoOptional.get();
            cartItemService.updateQuantityByUserAndProduct(user, productDto, quantity);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
