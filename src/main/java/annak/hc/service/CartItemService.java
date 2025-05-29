package annak.hc.service;

import annak.hc.dto.CartItemDto;
import annak.hc.dto.GiftSetDto;
import annak.hc.dto.GuestCartItemDto;
import annak.hc.dto.ProductDto;
import annak.hc.entity.CartItem;
import annak.hc.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CartItemService {
    Optional<CartItem> getById(Long id);
    List<CartItemDto> getAllByUser(User user);

    String saveOrDeleteIfExists(User user, ProductDto productDto);
    void deleteById(Long id);
    void delete(User user, ProductDto productDto);
    void delete(User user, GiftSetDto giftSetDto);
    void deleteAllByProductId(Long productId);

    String updateQuantityByProductId(Long productId, Long newQuantity);
    String updateQuantityByUserAndProduct(User user, ProductDto productDto, Long quantity);
    BigDecimal getTotalPriceOfItemsInCart(List<CartItemDto> cartItemDtoList);

    List<CartItemDto> convertGuestItemsToDto(List<GuestCartItemDto> guestItems);
    void mergeCartItems(User user, List<GuestCartItemDto> guestCartItems);
}
