package annak.hc.controller.user;

import annak.hc.dto.CartItemDto;
import annak.hc.dto.GiftSetDto;
import annak.hc.dto.GuestCartItemDto;
import annak.hc.dto.ProductDto;
import annak.hc.entity.User;
import annak.hc.service.CartItemService;
import annak.hc.service.FavoriteProductService;
import annak.hc.service.GiftSetService;
import annak.hc.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

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
            model.addAttribute("totalPrice", cartItemService.getTotalPriceOfItemsInCart(cartItemDtoList));
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        return "guest/cart";
    }

    @PostMapping("/guest")
    @ResponseBody
    public List<CartItemDto> getGuestCartItems(@RequestBody List<GuestCartItemDto> items) {
        System.out.println(items.toString());
        return cartItemService.convertGuestItemsToDto(items);
    }

    @DeleteMapping ("/products/{id}/deleteFromCart")
    public ResponseEntity<?> deleteProductWithIdFromCart(Principal principal, @PathVariable Long id) {
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
    public ResponseEntity<?> deleteGiftSetWithIdFromCart(Principal principal, @PathVariable Long id) {
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
    public ResponseEntity<?> transferProductWithIdToFavorite(Principal principal, @PathVariable Long id) {
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
    public ResponseEntity<?> updateQuantity(Principal principal, @PathVariable Long id, @PathVariable Long quantity) {
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
