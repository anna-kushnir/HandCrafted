package annak.hc.service;

import annak.hc.dto.GiftSetItemDto;
import annak.hc.entity.GiftSet;
import annak.hc.entity.GiftSetItem;

public interface GiftSetItemService {

    GiftSetItem save(GiftSet giftSet, GiftSetItemDto giftSetItemDto);
    void deleteAllByGiftSetId(Long giftSetId);
    void deleteAllByProductId(Long productId);
}
