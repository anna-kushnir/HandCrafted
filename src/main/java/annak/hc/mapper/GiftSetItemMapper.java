package annak.hc.mapper;

import annak.hc.dto.GiftSetItemDto;
import annak.hc.entity.GiftSetItem;
import annak.hc.entity.Product;
import annak.hc.exception.ResourceNotFoundException;
import annak.hc.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GiftSetItemMapper {

//    private final GiftSetRepository giftSetRepository;
    private final ProductService productService;

    public GiftSetItemDto toDto(GiftSetItem giftSetItem) {
        GiftSetItemDto giftSetItemDto = new GiftSetItemDto();

        giftSetItemDto.setId(giftSetItem.getId());
        giftSetItemDto.setProductId(giftSetItem.getProduct().getId());
        giftSetItemDto.setProductName(giftSetItem.getProduct().getName());
        giftSetItemDto.setProductCost(giftSetItem.getProductCost());
        giftSetItemDto.setQuantity(giftSetItem.getQuantity());

        return giftSetItemDto;
    }

    // TODO: перевірити коректність і необхідність цього методу
    public GiftSetItem toEntity(GiftSetItemDto giftSetItemDto) {
        GiftSetItem giftSetItem = new GiftSetItem();

        giftSetItem.setId(giftSetItemDto.getId());
//        giftSetItem.setGiftSet(giftSetRepository.findById());
        Optional<Product> productOptional = productService.getEntityById(giftSetItemDto.getProductId());
        if (productOptional.isEmpty()) {
            throw new ResourceNotFoundException("Товар з id <%s> не було знайдено".formatted(giftSetItemDto.getProductId()));
        }
        giftSetItem.setProduct(productOptional.get());
        giftSetItem.setQuantity(giftSetItemDto.getQuantity());
        giftSetItem.setProductCost(giftSetItemDto.getProductCost());

        return giftSetItem;
    }
}
