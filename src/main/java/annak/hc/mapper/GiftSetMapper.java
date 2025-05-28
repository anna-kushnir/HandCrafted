package annak.hc.mapper;

import annak.hc.dto.GiftSetDto;
import annak.hc.dto.GiftSetItemDto;
import annak.hc.dto.NewGiftSetDto;
import annak.hc.entity.GiftSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GiftSetMapper {

    public GiftSetDto toDto(GiftSet giftSet) {
        GiftSetDto giftSetDto = new GiftSetDto();
        giftSetDto.setId(giftSet.getId());

        List<GiftSetItemDto> itemDtos = giftSet.getItems().stream().map(item -> {
            GiftSetItemDto itemDto = new GiftSetItemDto();
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setProductCost(item.getProduct().isWithDiscount() ?
                    item.getProduct().getDiscountedPrice() :
                    item.getProduct().getPrice());
            return itemDto;
        }).toList();

        giftSetDto.setItems(itemDtos);

        BigDecimal totalPrice = itemDtos.stream()
                .map(i -> i.getProductCost().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        giftSetDto.setTotalPrice(totalPrice);

        return giftSetDto;
    }

    public GiftSet toEntity(NewGiftSetDto newGiftSetDto) {
        GiftSet giftSet = new GiftSet();
        giftSet.setUser(newGiftSetDto.getUser());
        giftSet.setPackagingWishes(newGiftSetDto.getPackagingWishes());
        return giftSet;
    }
}
