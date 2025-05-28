package annak.hc.service;

import annak.hc.dto.*;
import annak.hc.entity.CartItem;
import annak.hc.entity.Order;
import annak.hc.entity.User;
import annak.hc.entity.embedded.DeliveryAddress;
import annak.hc.entity.embedded.Status;
import annak.hc.entity.embedded.TypeOfReceipt;
import annak.hc.mapper.OrderMapper;
import annak.hc.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    private final ProductService productService;
    private final CartItemService cartItemService;
    private final OrderItemService orderItemService;

    @Override
    public List<OrderDto> getAllByUser(User user) {
        return orderRepository.findAllByUserOrderByFormationDate(user)
                .stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> getAllByStatusName(String statusName) {
        return orderRepository.findAllByStatusOrderByFormationDate(Status.valueOf(statusName.toUpperCase()))
                .stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> getAllByUserPhone(Long userPhone) {
        return orderRepository.findAllByUserPhoneOrderByFormationDate(userPhone)
                .stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OrderDto> getById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toDTO);
    }

    @Override
    public String save(NewOrderDto newOrderDto, List<CartItemDto> cartItemDtoList) {
        Order order = orderMapper.toEntity(newOrderDto);
        order.setPrice(cartItemService.getTotalPriceOfItemsInCart(cartItemDtoList));
        order.setFormationDate(LocalDateTime.now());
        order.setStatus(Status.IN_PROCESSING);
        if (order.getTypeOfReceipt() == TypeOfReceipt.DELIVERY_TO_THE_POST_OFFICE) {
            order.setDeliveryAddress(new DeliveryAddress(
                    newOrderDto.getDeliveryAddressRegion(),
                    newOrderDto.getDeliveryAddressCity(),
                    newOrderDto.getDeliveryAddressPostAddress()
            ));
        }
        order.setId(orderRepository.save(order).getId());

        for (CartItemDto cartItemDto : cartItemDtoList) {
            Optional<ProductDto> productDtoOptional = productService.getNotDeletedById(cartItemDto.getProductId());
            if (productDtoOptional.isPresent()) {
                ProductDto productDto = productDtoOptional.get();
                if (!(cartItemDto.getQuantityInCart() <= productDto.getQuantity())) {
                    return "Недостатньо товарів з id <%s>!".formatted(productDto.getId());
                }
                Optional<CartItem> cartItemOptional = cartItemService.getById(cartItemDto.getId());
                if (cartItemOptional.isEmpty()) {
                    return "Товар з id <%s> не було знайдено в кошику!".formatted(cartItemDto.getId());
                }
                orderItemService.save(order, cartItemOptional.get());
                cartItemService.deleteById(cartItemDto.getId());
                productDto.setQuantity(productDto.getQuantity() - cartItemDto.getQuantityInCart());
                productDto.setInStock(productDto.getQuantity() != 0);
                productService.update(productDto);
            } else {
                return "Товар з id <%s> не було знайдено!".formatted(cartItemDto.getProductId());
            }
        }
        return "Замовлення було успішно створено під номером <%s>".formatted(order.getId());
    }

    @Override
    public String update(OrderDto orderDto, List<OrderItemDto> orderItemDtoList) {
        Order order = orderMapper.toEntity(orderDto);
        orderRepository.save(order);
        orderItemService.saveAll(order, orderItemDtoList);
        return "Замовлення №%s було успішно оновлено".formatted(orderDto.getId());
    }

    @Override
    public String cancel(OrderDto orderDto) {
        if (orderDto.getStatus().getId() == 1) {
            orderItemService.returnProductsFromOrderToStockByOrderId(orderDto.getId());
            orderRepository.deleteById(orderDto.getId());
            return "Замовлення №%s було успішно скасовано".formatted(orderDto.getId());
        }
        return "Замовлення №%s вже було підтверджено, тому не може бути скасовано".formatted(orderDto.getId());
    }
}
