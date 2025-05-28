package annak.hc.mapper;

import annak.hc.dto.FavoriteProductDto;
import annak.hc.entity.FavoriteProduct;
import annak.hc.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class FavoriteProductMapper {

    public FavoriteProductDto toDTO(FavoriteProduct favoriteProduct) {
        Product product = favoriteProduct.getProduct();
        return new FavoriteProductDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.isWithDiscount(),
                product.getDiscountedPrice(),
                // TODO: перевірити пізніше коректність витягування фото
                product.getPhotos().get(0).getPhoto(),
                product.isInStock());
    }
}
