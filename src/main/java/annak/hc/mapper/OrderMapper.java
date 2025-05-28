package annak.hc.mapper;

import annak.hc.dto.NewOrderDto;
import annak.hc.dto.OrderDto;
import annak.hc.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDTO(Order order);

    Order toEntity(OrderDto orderDto);
    Order toEntity(NewOrderDto newOrderDto);
}
