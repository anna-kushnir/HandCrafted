package annak.hc.controller.user;

import annak.hc.dto.NewGiftSetDto;
import annak.hc.entity.User;
import annak.hc.service.CartItemService;
import annak.hc.service.FavoriteProductService;
import annak.hc.service.GiftSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static annak.hc.config.GlobalVariables.PRODUCTS;
import static annak.hc.config.GlobalVariables.TOTAL_PRICE;

@Controller
@RequestMapping("/giftSets")
@RequiredArgsConstructor
public class GiftSetController {

    private final GiftSetService giftSetService;

    private final UserDetailsService userDetailsService;
    private final FavoriteProductService favoriteProductService;
    private final CartItemService cartItemService;

    @GetMapping("/new")
    public String getCreateGiftSetView(Principal principal, Model model) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        var productToGiftSetDtoList = favoriteProductService.getAllForGiftSetByUser(user);
        model.addAttribute(PRODUCTS, productToGiftSetDtoList);
        model.addAttribute(TOTAL_PRICE, 0);
        return "user/create_gift_set";
    }

    @PostMapping("/new")
    public ResponseEntity<Void> createGiftSet(Principal principal, @RequestBody NewGiftSetDto giftSetDto) {
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        var giftSet = giftSetService.save(user, giftSetDto);
        if (giftSet == null) {
            return ResponseEntity.badRequest().build();
        }
        cartItemService.saveGiftSet(giftSet);
        return ResponseEntity.ok().build();
    }
}
