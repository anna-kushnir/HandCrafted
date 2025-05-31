package annak.hc.service;

import annak.hc.dto.GiftSetDto;
import annak.hc.dto.NewGiftSetDto;
import annak.hc.entity.GiftSet;
import annak.hc.entity.User;

import java.math.BigDecimal;
import java.util.Optional;

public interface GiftSetService {
    Optional<GiftSetDto> getById(Long id);
    Optional<GiftSet> getEntityById(Long id);

    BigDecimal setPriceForGiftSetAndItsItems(Long giftSetId);

    GiftSet save(User user, NewGiftSetDto newGiftSetDto);
    GiftSet update(GiftSet giftSet);
    void deleteById(Long giftSetId);
}
