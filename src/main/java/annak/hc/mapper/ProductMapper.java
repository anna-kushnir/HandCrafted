package annak.hc.mapper;

import annak.hc.dto.ProductDto;
import annak.hc.entity.Product;
import annak.hc.entity.ProductPhoto;
import annak.hc.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ColorService colorService;

    public ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getKeyWords(),
                product.getPrice(),
                product.isWithDiscount(),
                product.getDiscountedPrice(),
                product.isInStock(),
                product.getQuantity(),
                product.isCanAddToGiftSet(),
                product.getMaxQuantityInGiftSet(),
                product.getCreationDate(),
                product.getCategory(),
                product.getCategory().getId(),
                product.isDeleted(),
                product.getPhotos().stream().map(ProductPhoto::getPhoto).collect(Collectors.toList()),
                colorService.convertColorsToString(product.getColors())
        );
    }

    public Product toEntity(ProductDto productDto) {
        var product = new Product(
                productDto.getId(),
                productDto.getName(),
                productDto.getDescription(),
                productDto.getKeyWords(),
                productDto.getPrice(),
                productDto.isWithDiscount(),
                productDto.getDiscountedPrice(),
                productDto.isInStock(),
                productDto.getQuantity(),
                productDto.isCanAddToGiftSet(),
                productDto.getMaxQuantityInGiftSet(),
                productDto.getCreationDate(),
                productDto.getCategory(),
                productDto.isDeleted(),
                new ArrayList<>(),
                colorService.convertColorsToList(productDto.getColors())
        );
        List<ProductPhoto> photoEntities = new ArrayList<>();
        for (int i = 0; i < productDto.getPhotos().size(); i++) {
            String photoString = productDto.getPhotos().get(i);
            ProductPhoto photo = new ProductPhoto();
            photo.setPhoto(photoString);
            photo.setIndex(i);
            photo.setProduct(product);
            photoEntities.add(photo);
        }
        product.setPhotos(photoEntities);
        return product;
    }
}
