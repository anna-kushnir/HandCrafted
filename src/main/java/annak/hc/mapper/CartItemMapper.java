package annak.hc.mapper;

import annak.hc.config.GlobalVariables;
import annak.hc.dto.CartItemDto;
import annak.hc.entity.CartItem;
import annak.hc.entity.ProductPhoto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CartItemMapper {

    public CartItemDto toDto(CartItem cartItem) {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(cartItem.getId());
        cartItemDto.setQuantityInCart(cartItem.getQuantityInCart());

        if (cartItem.getProduct() != null) {
            cartItemDto.setGiftSet(false);
            cartItemDto.setProductId(cartItem.getProduct().getId());
            cartItemDto.setProductQuantity(cartItem.getProduct().getQuantity());
            cartItemDto.setName(cartItem.getProduct().getName());
            cartItemDto.setPhotos(List.of(cartItem.getProduct().getPhotos().get(0).getPhoto()));
            cartItemDto.setCost(cartItem.getProduct().isWithDiscount() ?
                    cartItem.getProduct().getDiscountedPrice() :
                    cartItem.getProduct().getPrice());
        } else if (cartItem.getGiftSet() != null) {
            cartItemDto.setGiftSet(true);
            cartItemDto.setGiftSetId(cartItem.getGiftSet().getId());
            cartItemDto.setName("Подарунковий набір (" + cartItem.getGiftSet().getItems().size() + ")");
            List<String> photos = cartItem.getGiftSet().getItems().stream()
                    .flatMap(item -> {
                        List<ProductPhoto> images = item.getProduct().getPhotos();
                        return images.isEmpty() ? Stream.empty() : Stream.of(images.get(0).getPhoto());
                    })
                    .limit(4)
                    .collect(Collectors.toList());
            cartItemDto.setPhotos(photos);

            BigDecimal wrapPrice = GlobalVariables.WRAP_PRICE;
            BigDecimal total = cartItem.getGiftSet().getItems().stream()
                    .map(item ->
                            (item.getProduct().isWithDiscount() ?
                                    item.getProduct().getDiscountedPrice() :
                                    item.getProduct().getPrice())
                                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    )
                    .reduce(BigDecimal.ZERO, BigDecimal::add).add(wrapPrice);
            cartItemDto.setCost(total);
        }

        return cartItemDto;
    }
}
