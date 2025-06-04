package annak.hc.controller.admin;

import annak.hc.dto.ProductDto;
import annak.hc.service.CategoryService;
import annak.hc.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static annak.hc.config.GlobalVariables.*;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String getAllProducts(Model model,
                                 @RequestParam(name = "categoryId", required = false, defaultValue = "0") Long categoryId,
                                 @RequestParam(name = "search", required = false) String search) {
        model.addAttribute(CATEGORIES, categoryService.getAll());
        if (categoryId != 0) {
            model.addAttribute(PRODUCTS, productService.getAllNotDeletedByCategoryId(categoryId));
        } else if (search != null) {
            model.addAttribute(PRODUCTS, productService.getAllNotDeletedBySearchLine(0L, search));
        } else {
            model.addAttribute(PRODUCTS, productService.getAllNotDeleted());
        }
        return "admin/list_of_products";
    }

    @GetMapping("/add")
    public String getAddNewProductForm(Model model) {
        model.addAttribute(CATEGORIES, categoryService.getAll());
        return "admin/add_product";
    }

    @PostMapping
    public String addProduct(@RequestBody ProductDto productDto, RedirectAttributes redirectAttributes) {
        if (productDto.getId() != null) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Id нового товару має бути порожнім!");
            return "redirect:/admin/products";
        }
        productDto.setCategory(categoryService.getEntityById(productDto.getCategoryId()));
        productService.save(productDto);
        redirectAttributes.addFlashAttribute(MESSAGE, "Товар успішно додано");
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String getProductToEditById(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        var productDtoOptional = productService.getNotDeletedById(id);
        if (productDtoOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Товар з id <%s> не було знайдено".formatted(id));
            return "redirect:/admin/products";
        }
        model.addAttribute("oldProduct", productDtoOptional.get());
        model.addAttribute(CATEGORIES, categoryService.getAll());
        return "admin/edit_product";
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        if (!productDto.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        productService.update(productDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
