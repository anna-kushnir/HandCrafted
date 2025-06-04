package annak.hc.controller.user;

import annak.hc.dto.ProductDto;
import annak.hc.entity.User;
import annak.hc.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import static annak.hc.config.GlobalVariables.IS_AUTHENTICATED;
import static annak.hc.config.GlobalVariables.MESSAGE;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ColorService colorService;
    private final UserDetailsService userDetailsService;
    private final CartItemService cartItemService;
    private final FavoriteProductService favoriteProductService;

    @GetMapping
    public String getAllProducts(Principal principal, Model model,
                                 @RequestParam(name = "sortByCost", required = false, defaultValue = "false") boolean sortByCost,
                                 @RequestParam(name = "sortByCostAsc", required = false, defaultValue = "true") boolean sortByCostAsc,
                                 @RequestParam(name = "sortByNewness", required = false, defaultValue = "false") boolean sortByNewness,
                                 @RequestParam(name = "sortByNewnessAsc", required = false, defaultValue = "true") boolean sortByNewnessAsc,
                                 @RequestParam(name = "priceFrom", required = false, defaultValue = "0") BigDecimal priceFrom,
                                 @RequestParam(name = "priceTo", required = false, defaultValue = "10000") BigDecimal priceTo,
                                 @RequestParam(name = "categoryId", required = false, defaultValue = "0") Long categoryId,
                                 @RequestParam(name = "search", required = false) String search,
                                 @RequestParam(name = "colorIds", required = false) List<Long> colorIds
    ) {
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("colors", colorService.getAll());
        model.addAttribute("categoryWasChosen", categoryId != 0);
        if (search != null) {
            model.addAttribute("products", productService.getAllNotDeletedBySearchLine(categoryId, search));
        } else {
            model.addAttribute("products",
                    productService.getAllNotDeletedByFilter(categoryId, sortByCost, sortByCostAsc, sortByNewness, sortByNewnessAsc, priceFrom, priceTo, colorIds));
        }
        model.addAttribute(IS_AUTHENTICATED, principal != null);
        return "guest/list_of_products";
    }

    @GetMapping("/{id}")
    public String getProductById(Principal principal, @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        var productDtoOptional = productService.getNotDeletedById(id);
        if (productDtoOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Товар з id <%s> не було знайдено".formatted(id));
            return "redirect:/products";
        }
        var productDto = productDtoOptional.get();
        model.addAttribute("product", productDto);
        if (principal != null) {
            var user = (User) userDetailsService.loadUserByUsername(principal.getName());
            List<ProductDto> recommendedDtoList = productService.getRecommendedProducts(productDto.getId(), user.getId());
            model.addAttribute("recommendedProducts", recommendedDtoList);
            model.addAttribute(IS_AUTHENTICATED, true);
        } else {
            List<ProductDto> recommendedDtoList = productService.getRecommendedProducts(productDto.getId(), null);
            model.addAttribute("recommendedProducts", recommendedDtoList);
            model.addAttribute(IS_AUTHENTICATED, false);
        }
        return "guest/product";
    }

    @PostMapping("/{id}/addToFavorites")
    public String addProductWithIdToFavorites(Principal principal, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("msgAddToFavorites", "Щоб додати товар до вподобаного, вам необхідно зареєструватися");
            return "redirect:/products/{id}";
        }
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        var productDtoOptional = productService.getNotDeletedById(id);
        if (productDtoOptional.isPresent()) {
            var productDto = productDtoOptional.get();
            var result = favoriteProductService.saveOrDeleteIfExists(user, productDto);
            redirectAttributes.addFlashAttribute("msgAddToFavorites", result);
            return "redirect:/products/{id}";
        } else {
            return "redirect:/products";
        }
    }

    @PostMapping("/{id}/addToCart")
    public String addProductWithIdToCart(Principal principal, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/products/{id}";
        }
        var user = (User) userDetailsService.loadUserByUsername(principal.getName());
        var productDtoOptional = productService.getNotDeletedById(id);
        if (productDtoOptional.isPresent()) {
            var productDto = productDtoOptional.get();
            var result = cartItemService.saveOrDeleteIfExists(user, productDto);
            redirectAttributes.addFlashAttribute("msgAddToCart", result);
            return "redirect:/products/{id}";
        } else {
            return "redirect:/products";
        }
    }
}
