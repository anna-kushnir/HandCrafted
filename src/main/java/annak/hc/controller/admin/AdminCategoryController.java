package annak.hc.controller.admin;

import annak.hc.dto.CategoryDto;
import annak.hc.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static annak.hc.config.GlobalVariables.*;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private static final String REDIRECT_ADMIN_CATEGORIES = "redirect:/admin/categories";

    private final CategoryService categoryService;

    @GetMapping
    public String getAllCategories(Model model) {
        model.addAttribute(CATEGORIES, categoryService.getAll());
        return "admin/list_of_categories";
    }

    @GetMapping("/add")
    public String getAddNewCategoryForm(Model model) {
        model.addAttribute(CATEGORY, new CategoryDto());
        return "admin/add_category";
    }

    @PostMapping
    public String addCategory(@ModelAttribute("category") CategoryDto categoryDto, RedirectAttributes redirectAttributes) {
        if (categoryDto.getId() != null) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Id нової категорії має бути порожнім!");
            return REDIRECT_ADMIN_CATEGORIES;
        }
        categoryService.save(categoryDto);
        redirectAttributes.addFlashAttribute(MESSAGE, "Категорію успішно додано");
        return REDIRECT_ADMIN_CATEGORIES;
    }

    @GetMapping("/{id}/edit")
    public String getCategoryToEditById(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        var categoryDtoOptional = categoryService.getById(id);
        if (categoryDtoOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Категорію з id <%s> не було знайдено".formatted(id));
            return REDIRECT_ADMIN_CATEGORIES;
        }
        var categoryDto = categoryDtoOptional.get();
        model.addAttribute("oldCategory", categoryDto);
        var newCategoryDto = new CategoryDto();
        newCategoryDto.setId(categoryDto.getId());
        model.addAttribute("newCategory", newCategoryDto);
        return "admin/edit_category";
    }

    @PostMapping("/{id}")
    public String editCategory(@PathVariable Long id, @ModelAttribute(CATEGORY) CategoryDto categoryDto, RedirectAttributes redirectAttributes) {
        if (!categoryDto.getId().equals(id)) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Некоректне вказання id категорії для редагування");
            return REDIRECT_ADMIN_CATEGORIES;
        }
        var result = categoryService.update(categoryDto);
        redirectAttributes.addFlashAttribute(MESSAGE, result);
        return REDIRECT_ADMIN_CATEGORIES;
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
