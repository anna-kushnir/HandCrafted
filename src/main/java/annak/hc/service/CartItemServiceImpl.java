package annak.hc.service;

import annak.hc.dto.CartItemDto;
import annak.hc.dto.GiftSetDto;
import annak.hc.dto.GuestCartItemDto;
import annak.hc.dto.ProductDto;
import annak.hc.entity.CartItem;
import annak.hc.entity.GiftSet;
import annak.hc.entity.Product;
import annak.hc.entity.User;
import annak.hc.exception.ResourceNotFoundException;
import annak.hc.mapper.CartItemMapper;
import annak.hc.mapper.ProductMapper;
import annak.hc.repository.CartItemRepository;
import annak.hc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Optional<CartItem> getById(Long id) {
        return cartItemRepository.findById(id);
    }

    @Override
    public Optional<CartItem> getByUserAndGiftSetId(User user, Long giftSetId) {
        return cartItemRepository.findByUserAndGiftSetId(user, giftSetId);
    }

    @Override
    public List<CartItemDto> getAllByUser(User user) {
        return cartItemRepository.findAllByUserOrderById(user)
                .stream()
                .map(cartItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String saveOrDeleteIfExists(User user, ProductDto productDto) {
        if (cartItemRepository.existsByUserUserNameAndProductId(user.getUsername(), productDto.getId())) {
            cartItemRepository.deleteByUserUserNameAndProductId(user.getUsername(), productDto.getId());
            return "Товар видалено з кошика";
        }
        if (!productDto.isInStock()) {
            return "Товару немає в наявності";
        }
        CartItem cartItem = new CartItem();
        cartItem.setProduct(productMapper.toEntity(productDto));
        cartItem.setUser(user);
        cartItem.setQuantityInCart(1L);

        cartItemRepository.save(cartItem);
        return "Товар додано до кошика";
    }

    @Override
    @Transactional
    public String saveGiftSet(GiftSet giftSet) {
        if (cartItemRepository.existsByUserUserNameAndGiftSetId(giftSet.getUser().getUsername(), giftSet.getId())) {
            return "Подарунковий набір вже є в кошику";
        }
        CartItem cartItem = new CartItem();
        cartItem.setGiftSet(giftSet);
        cartItem.setUser(giftSet.getUser());
        cartItem.setQuantityInCart(1L);

        cartItemRepository.save(cartItem);
        return "Подарунковий набір додано до кошика";
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        cartItemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void delete(User user, ProductDto productDto) {
        if (cartItemRepository.existsByUserUserNameAndProductId(user.getUsername(), productDto.getId())) {
            cartItemRepository.deleteByUserUserNameAndProductId(user.getUsername(), productDto.getId());
        }
    }

    @Override
    @Transactional
    public void delete(User user, GiftSetDto giftSetDto) {
        if (cartItemRepository.existsByUserUserNameAndGiftSetId(user.getUsername(), giftSetDto.getId())) {
            cartItemRepository.deleteByUserUserNameAndGiftSetId(user.getUsername(), giftSetDto.getId());
        }
    }

    @Override
    public void deleteAllByProductId(Long productId) {
        cartItemRepository.deleteAllByProductId(productId);
    }

    @Override
    public String updateQuantityByProductId(Long productId, Long newQuantity) {
        Optional<Product> productOptional = productRepository.findByIdAndDeletedIsFalse(productId);
        if (productOptional.isEmpty()) {
            throw new ResourceNotFoundException("Товар з id <%s> не було знайдено".formatted(productId));
        }
        List<CartItem> cartItems = cartItemRepository.findAllByProduct(productOptional.get());
        for (var cartItem : cartItems) {
            if (cartItem.getQuantityInCart() > newQuantity) {
                cartItem.setQuantityInCart(newQuantity);
                cartItemRepository.save(cartItem);
            }
        }
        return "Кількість товарів у кошику для товару з id <%s> було успішно змінено".formatted(productId);
    }

    @Override
    public String updateQuantityByUserAndProduct(User user, ProductDto productDto, Long quantity) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findByUserAndProductId(user, productDto.getId());
        if (cartItemOptional.isEmpty()) {
            return "Даного товару немає в кошику!";
        }
        CartItem cartItem = cartItemOptional.get();
        if (cartItem.getProduct().getQuantity() < quantity) {
            return "Недостатньо товару з id <%s>!".formatted(cartItem.getProduct().getId());
        }
        cartItem.setQuantityInCart(quantity);
        cartItemRepository.save(cartItem);
        return "Кількість товару з id <%s> в кошику було успішно змінено".formatted(cartItem.getProduct().getId());
    }

    @Override
    public BigDecimal getTotalPriceOfItemsInCart(List<CartItemDto> cartItemDtoList) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItemDto cartItemDto : cartItemDtoList) {
            totalPrice = totalPrice.add(cartItemDto.getCost().multiply(BigDecimal.valueOf(cartItemDto.getQuantityInCart())));
        }
        return totalPrice;
    }

    @Override
    public List<CartItemDto> convertGuestItemsToDto(List<GuestCartItemDto> guestItems) {
        return guestItems
                .stream()
                .map(item -> {
                    Optional<Product> productOptional = productRepository.findByIdAndDeletedIsFalse(item.getProductId());
                    if (productOptional.isEmpty()) {
                        return null;
                    }
                    Product product = productOptional.get();
                    if (!product.isInStock()) {
                        return null;
                    }
                    CartItemDto cartItemDto = new CartItemDto();
                    cartItemDto.setName(product.getName());
                    cartItemDto.setCost(product.isWithDiscount() ?
                            product.getDiscountedPrice() : product.getPrice()
                    );
                    cartItemDto.setQuantityInCart(Math.min(item.getQuantityInCart(), product.getQuantity()));
                    cartItemDto.setPhotos(List.of(product.getPhotos().get(0).getPhoto()));
                    cartItemDto.setGiftSet(false);

                    cartItemDto.setProductId(product.getId());
                    cartItemDto.setProductQuantity(product.getQuantity());
                    return cartItemDto;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional
    public void mergeCartItems(User user, List<GuestCartItemDto> guestCartItems) {
        for (GuestCartItemDto guestCartItem : guestCartItems) {
            var productOptional = productRepository.findById(guestCartItem.getProductId());
            if (productOptional.isEmpty()) {
                throw new ResourceNotFoundException("Товар з id <%s> не було знайдено".formatted(guestCartItem.getProductId()));
            }

            var product = productOptional.get();
            if (product.isInStock()) {
                var existingProductInCart = cartItemRepository.findByUserAndProductId(user, product.getId());
                if (existingProductInCart.isPresent()) {
                    existingProductInCart.get().setQuantityInCart(Math.min(
                            Math.max(existingProductInCart.get().getQuantityInCart(), guestCartItem.getQuantityInCart()),
                            product.getQuantity()));
                } else {
                    cartItemRepository.save(new CartItem(null, user, product, null, Math.min(
                            guestCartItem.getQuantityInCart(),
                            product.getQuantity())));
                }
            }
        }
    }
}
