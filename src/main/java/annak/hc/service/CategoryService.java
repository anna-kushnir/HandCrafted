package annak.hc.service;

import annak.hc.dto.CategoryDto;
import annak.hc.entity.Category;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CategoryService {
    List<CategoryDto> getAll();
    Optional<CategoryDto> getById(Long id);
    Category getEntityById(Long id);
    Map<String, Long> getCategoryStatistics();

    CategoryDto save(CategoryDto categoryDto);
    String update(CategoryDto categoryDto);
    void deleteById(Long id);
}
