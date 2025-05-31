package annak.hc.service;

import annak.hc.dto.NewGiftSetItemDto;
import annak.hc.entity.GiftSet;
import annak.hc.entity.GiftSetItem;

public interface GiftSetItemService {

    GiftSetItem save(GiftSet giftSet, NewGiftSetItemDto newGiftSetItemDto);
    void deleteAllByGiftSetId(Long giftSetId);
    void deleteAllByProductId(Long productId);
}
