package annak.hc.service;

import annak.hc.dto.*;
import annak.hc.entity.GiftSet;
import annak.hc.entity.User;
import annak.hc.mapper.GiftSetMapper;
import annak.hc.repository.GiftSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GiftSetServiceImpl implements GiftSetService {

    private final GiftSetRepository giftSetRepository;
    private final GiftSetMapper giftSetMapper;

    private final GiftSetItemService giftSetItemService;

    @Override
    public Optional<GiftSetDto> getById(Long id) {
        return giftSetRepository.findById(id)
                .map(giftSetMapper::toDto);
    }

    @Override
    public Optional<GiftSet> getEntityById(Long id) {
        return giftSetRepository.findById(id);
    }

    @Override
    @Transactional
    public GiftSet save(User user, NewGiftSetDto newGiftSetDto) {
        var giftSet = new GiftSet(
                null,
                user,
                LocalDateTime.now(),
                newGiftSetDto.getPackagingWishes(),
                null,
                null
        );
        giftSet.setId(giftSetRepository.save(giftSet).getId());

        for (var newGiftSetItem : newGiftSetDto.getItems()) {
            var giftSetItem = giftSetItemService.save(giftSet, newGiftSetItem);
            if (giftSetItem == null) {
                giftSetRepository.delete(giftSet);
                return null;
            }
        }
        return giftSetRepository.findById(giftSet.getId()).orElseThrow();
    }

    @Override
    @Transactional
    public void deleteById(Long giftSetId) {
        giftSetItemService.deleteAllByGiftSetId(giftSetId);
        giftSetRepository.deleteById(giftSetId);
    }
}
