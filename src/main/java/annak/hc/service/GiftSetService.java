package annak.hc.service;

import annak.hc.dto.GiftSetDto;
import annak.hc.dto.GiftSetItemDto;
import annak.hc.dto.NewGiftSetDto;
import annak.hc.entity.GiftSet;

import java.util.List;
import java.util.Optional;

public interface GiftSetService {
    Optional<GiftSetDto> getById(Long id);
    Optional<GiftSet> getEntityById(Long id);

    String save(NewGiftSetDto newGiftSetDto, List<GiftSetItemDto> giftSetItemDtoList);
//    String update(GiftSetDto giftSetDto, List<GiftSetItemDto> giftSetItemDtoList);
    void deleteById(Long giftSetId);
}
