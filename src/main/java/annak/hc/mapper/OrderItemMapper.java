package annak.hc.mapper;

import annak.hc.dto.OrderItemDto;
import annak.hc.entity.GiftSet;
import annak.hc.entity.OrderItem;
import annak.hc.entity.Product;
import annak.hc.entity.ProductPhoto;
import annak.hc.exception.ResourceNotFoundException;
import annak.hc.service.GiftSetService;
import annak.hc.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class OrderItemMapper {

    private final ProductService productService;
    private final GiftSetService giftSetService;

    public OrderItemDto toDTO(OrderItem orderItem) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId());
        orderItemDto.setOrder(orderItem.getOrder());                    // TODO: видалити, якщо не знадобиться
        orderItemDto.setQuantityInOrder(orderItem.getQuantityInOrder());

        if (orderItem.getProduct() != null) {
            orderItemDto.setGiftSet(false);
            orderItemDto.setProductId(orderItem.getProduct().getId());
            orderItemDto.setName(orderItem.getProduct().getName());
            orderItemDto.setPhotos(List.of(orderItem.getProduct().getPhotos().get(0).getPhoto()));
            orderItemDto.setCost(orderItem.getProduct().isWithDiscount() ?
                    orderItem.getProduct().getDiscountedPrice() :
                    orderItem.getProduct().getPrice());
            orderItemDto.setHasDeletedProduct(orderItem.getProduct().isDeleted());
        } else if (orderItem.getGiftSet() != null) {
            orderItemDto.setGiftSet(true);
            orderItemDto.setGiftSetId(orderItem.getGiftSet().getId());
            orderItemDto.setName("Подарунковий набір (" + orderItem.getGiftSet().getItems().size() + ")");
            List<String> photos = orderItem.getGiftSet().getItems().stream()
                    .flatMap(item -> {
                        List<ProductPhoto> images = item.getProduct().getPhotos();
                        return images.isEmpty() ? Stream.empty() : Stream.of(images.get(0).getPhoto());
                    })
                    .limit(4)
                    .collect(Collectors.toList());
            orderItemDto.setPhotos(photos);
            orderItemDto.setCost(orderItem.getItemCost());
        }
        return orderItemDto;
    }

    public OrderItem toEntity(OrderItemDto orderItemDto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(orderItemDto.getId());
        orderItem.setOrder(orderItemDto.getOrder());
        orderItem.setItemCost(orderItemDto.getCost());
        orderItem.setQuantityInOrder(orderItemDto.getQuantityInOrder());
        if (!orderItemDto.isGiftSet()) {
            Optional<Product> productOptional = productService.getEntityById(orderItemDto.getProductId());
            if (productOptional.isEmpty()) {
                throw new ResourceNotFoundException("Товар з id <%s> не було знайдено".formatted(orderItemDto.getProductId()));
            }
            orderItem.setProduct(productOptional.get());
        } else {
            Optional<GiftSet> giftSetOptional = giftSetService.getEntityById(orderItemDto.getGiftSetId());
            if (giftSetOptional.isEmpty()) {
                throw new ResourceNotFoundException("Подарунковий набір з id <%s> не було знайдено".formatted(orderItemDto.getGiftSetId()));
            }
            orderItem.setGiftSet(giftSetOptional.get());
        }
        return orderItem;
    }
}
