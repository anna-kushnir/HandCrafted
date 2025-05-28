package annak.hc.mapper;

import annak.hc.dto.CategoryDto;
import annak.hc.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDTO(Category category);
    Category toEntity(CategoryDto categoryDto);
}
