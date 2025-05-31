package annak.hc.service;

import annak.hc.dto.GiftSetDto;
import annak.hc.dto.NewGiftSetDto;
import annak.hc.entity.GiftSet;
import annak.hc.entity.User;

import java.util.Optional;

public interface GiftSetService {
    Optional<GiftSetDto> getById(Long id);
    Optional<GiftSet> getEntityById(Long id);

    GiftSet save(User user, NewGiftSetDto newGiftSetDto);
//    String update(GiftSetDto giftSetDto, List<GiftSetItemDto> giftSetItemDtoList);
    void deleteById(Long giftSetId);
}
