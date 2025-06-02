package annak.hc.service;

import annak.hc.dto.*;
import annak.hc.entity.CartItem;
import annak.hc.entity.Order;
import annak.hc.entity.User;
import annak.hc.entity.embedded.DeliveryAddress;
import annak.hc.entity.embedded.Status;
import annak.hc.entity.embedded.TypeOfReceipt;
import annak.hc.exception.ResourceNotFoundException;
import annak.hc.mapper.OrderMapper;
import annak.hc.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    private final GiftSetService giftSetService;
    private final GiftSetItemService giftSetItemService;

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

    private ProductDto getProductDtoAndCheckQuantityOrElseThrow(Long productId, Long itemQuantity) {
        Optional<ProductDto> productDtoOptional = productService.getNotDeletedById(productId);
        if (productDtoOptional.isEmpty()) {
            throw new ResourceNotFoundException("Товар з id <%s> не було знайдено!".formatted(productId));
        }
        var productDto = productDtoOptional.get();
        if (!(itemQuantity <= productDto.getQuantity())) {
            throw new ResourceNotFoundException("Недостатньо товарів з id <%s>!".formatted(productDto.getId()));
        }
        return productDto;
    }

    @Override
    @Transactional
    public String save(NewOrderDto newOrderDto, List<CartItemDto> cartItemDtoList) {
        Order order = orderMapper.toEntity(newOrderDto);
        for (CartItemDto cartItemDto : cartItemDtoList) {
            if (cartItemDto.isGiftSet()) {
                BigDecimal totalGiftSetPrice = giftSetService.setPriceForGiftSetAndItsItems(cartItemDto.getGiftSetId());
                cartItemDto.setCost(totalGiftSetPrice);
            }
        }
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
            Optional<CartItem> cartItemOptional = cartItemService.getById(cartItemDto.getId());
            if (cartItemOptional.isEmpty()) {
                throw new ResourceNotFoundException("В кошику під id <%s> нічого не знайдено!".formatted(cartItemDto.getId()));
            }

            if (cartItemDto.isGiftSet()) {
                for (var giftSetItem : giftSetItemService.getAllByGiftSetId(cartItemDto.getGiftSetId())) {
                    ProductDto productDto = getProductDtoAndCheckQuantityOrElseThrow(giftSetItem.getProduct().getId(), giftSetItem.getQuantity());
                    if (!(giftSetItem.getQuantity() <= productDto.getMaxQuantityInGiftSet())) {
                        throw new ResourceNotFoundException("В подарунковий набір не можна додати таку кількість товару з id <%s>!".formatted(productDto.getId()));
                    }
                    productDto.setQuantity(productDto.getQuantity() - giftSetItem.getQuantity());
                    productDto.setInStock(productDto.getQuantity() != 0);
                    productService.update(productDto);
                }
                orderItemService.save(order, cartItemOptional.get());
                cartItemService.deleteById(cartItemDto.getId());
            } else {
                ProductDto productDto = getProductDtoAndCheckQuantityOrElseThrow(cartItemDto.getProductId(), cartItemDto.getQuantityInCart());
                orderItemService.save(order, cartItemOptional.get());
                cartItemService.deleteById(cartItemDto.getId());
                productDto.setQuantity(productDto.getQuantity() - cartItemDto.getQuantityInCart());
                productDto.setInStock(productDto.getQuantity() != 0);
                productService.update(productDto);
            }
        }
        return "Замовлення було успішно створено під номером <%s>".formatted(order.getId());
    }

    @Override
    public String update(OrderDto orderDto) {
        Order order = orderMapper.toEntity(orderDto);
        orderRepository.save(order);
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
