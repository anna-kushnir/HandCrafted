package annak.hc.service;

import annak.hc.dto.CategoryDto;
import annak.hc.entity.Category;
import annak.hc.exception.ResourceNotFoundException;
import annak.hc.mapper.CategoryMapper;
import annak.hc.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getAll() {
        return categoryRepository.findAllOrderByNameAscAndOthersLast()
                .stream()
                .map(categoryMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<CategoryDto> getById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDTO);
    }

    @Override
    public Category getEntityById(Long id) {
        var categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty())
            throw new ResourceNotFoundException("Category with id <%s> does not exist".formatted(id));
        return categoryOptional.get();
    }

    @Override
    public Map<String, Long> getCategoryStatistics() {
        List<Object[]> statisticsList = categoryRepository.getCategoryStatistics();
        Map<String, Long> statisticsMap = new LinkedHashMap<>();

        for (Object[] row : statisticsList) {
            statisticsMap.put((String)row[0], (Long)row[1]);
        }
        return statisticsMap;
    }

    @Override
    @Transactional
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        category.setId(categoryRepository.save(category).getId());
        return categoryMapper.toDTO(category);
    }

    @Override
    @Transactional
    public String update(CategoryDto categoryDto) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryDto.getId());
        if (categoryOptional.isEmpty()) {
            return "Категорію з id <%s> не знайдено!".formatted(categoryDto.getId());
        }
        return "Категорію з id <%s> було успішно оновлено".formatted(
                categoryRepository.save(categoryMapper.toEntity(categoryDto)).getId()
        );
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
