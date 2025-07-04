package annak.hc.service;

import annak.hc.dto.NewGiftSetItemDto;
import annak.hc.entity.GiftSet;
import annak.hc.entity.GiftSetItem;

import java.util.List;

public interface GiftSetItemService {

    List<GiftSetItem> getAllByGiftSetId(Long gifSetId);
    List<GiftSetItem> getAllByProductId(Long productId);
    List<GiftSetItem> getAllNotOrderedByProductId(Long productId);

    GiftSetItem save(GiftSet giftSet, NewGiftSetItemDto newGiftSetItemDto);
    GiftSetItem update(GiftSetItem giftSetItem);
    void deleteAllByGiftSetId(Long giftSetId);
    void deleteAllByProductId(Long productId);
}
