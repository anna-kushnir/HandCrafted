package annak.hc.service;

import annak.hc.dto.CartItemDto;
import annak.hc.dto.NewOrderDto;
import annak.hc.dto.OrderDto;
import annak.hc.dto.OrderItemDto;
import annak.hc.entity.User;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<OrderDto> getAllByUser(User user);
    List<OrderDto> getAllByStatusName(String statusName);
    List<OrderDto> getAllByUserPhone(Long userPhone);
    Optional<OrderDto> getById(Long orderId);

    String save(NewOrderDto newOrderDto, List<CartItemDto> cartItemDtoList);
    String update(OrderDto orderDto, List<OrderItemDto> orderItemDtoList);
    String cancel(OrderDto orderDto);
}
