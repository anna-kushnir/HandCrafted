package annak.hc.service;

import annak.hc.dto.OrderItemDto;
import annak.hc.entity.CartItem;
import annak.hc.entity.Order;
import annak.hc.entity.OrderItem;
import annak.hc.entity.Product;
import annak.hc.mapper.OrderItemMapper;
import annak.hc.mapper.ProductMapper;
import annak.hc.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    private final ProductService productService;
    private final ProductMapper productMapper;

    @Override
    public List<OrderItem> getAllByOrderId(Long orderId) {
        return orderItemRepository.findAllByOrderId(orderId);
    }

    @Override
    public List<OrderItemDto> getAllDtosByOrderId(Long orderId) {
        return orderItemRepository.findAllByOrderId(orderId)
                .stream()
                .map(orderItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void save(Order order, CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        if (cartItem.getProduct() != null) {
            Product product = cartItem.getProduct();
            orderItem.setProduct(product);
            orderItem.setItemCost(product.isWithDiscount() ? product.getDiscountedPrice() : product.getPrice());
        } else if (cartItem.getGiftSet() != null) {
            orderItem.setGiftSet(cartItem.getGiftSet());
            //        TODO: налаштувати зміну цієї вартості або десь записати як глобальну змінну
            BigDecimal wrapPrice = BigDecimal.valueOf(50);
            BigDecimal giftSetPrice = cartItem.getGiftSet().getItems().stream()
                    .map(i -> (
                            i.getProduct().isWithDiscount() ?
                                    i.getProduct().getDiscountedPrice() :
                                    i.getProduct().getPrice())
                            .multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add).add(wrapPrice);
            orderItem.setItemCost(giftSetPrice);
        }
        orderItem.setQuantityInOrder(cartItem.getQuantityInCart());
        orderItemRepository.save(orderItem);
    }

    @Override
    public void save(Order order, OrderItemDto orderItemDto) {
        orderItemDto.setId(null);
        orderItemDto.setOrder(order);
        orderItemRepository.save(orderItemMapper.toEntity(orderItemDto));
    }

    @Override
    public void saveAll(Order order, List<OrderItemDto> orderItemDtoList) {
        orderItemDtoList.forEach(orderItemDto -> save(order, orderItemDto));
    }

    @Override
    public void returnProductsFromOrderToStockByOrderId(Long orderId) {
        List<OrderItem> orderItemList = getAllByOrderId(orderId);
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getProduct() != null) {
                Product product = orderItem.getProduct();
                product.setQuantity(product.getQuantity() + orderItem.getQuantityInOrder());
                product.setInStock(true);
                productService.update(productMapper.toDto(product));
            }
//            TODO: врахувати повернення товарів з подарункових наборів
            orderItemRepository.delete(orderItem);
        }
    }
}
