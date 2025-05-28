package annak.hc.service;

import annak.hc.dto.OrderItemDto;
import annak.hc.entity.CartItem;
import annak.hc.entity.Order;
import annak.hc.entity.OrderItem;

import java.util.List;

public interface OrderItemService {
    List<OrderItem> getAllByOrderId(Long orderId);
    List<OrderItemDto> getAllDtosByOrderId(Long orderId);

    void save(Order order, CartItem cartItem);
    void save(Order order, OrderItemDto orderItemDto);
    void saveAll(Order order, List<OrderItemDto> orderItemDtoList);
    void returnProductsFromOrderToStockByOrderId(Long orderId);
}
