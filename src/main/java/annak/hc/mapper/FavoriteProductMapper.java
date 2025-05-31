package annak.hc.mapper;

import annak.hc.dto.FavoriteProductDto;
import annak.hc.dto.FavoriteProductToGiftSetDto;
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
                product.getPhotos().get(0).getPhoto(),
                product.isInStock());
    }

    public FavoriteProductToGiftSetDto toGiftSetDTO(FavoriteProduct favoriteProduct) {
        Product product = favoriteProduct.getProduct();
        return new FavoriteProductToGiftSetDto(
                product.getId(),
                product.getName(),
                product.isWithDiscount() ? product.getDiscountedPrice() : product.getPrice(),
                product.getPhotos().get(0).getPhoto(),
                product.isInStock(),
                product.isCanAddToGiftSet(),
                Math.min(product.getMaxQuantityInGiftSet(), product.getQuantity()));
    }
}
