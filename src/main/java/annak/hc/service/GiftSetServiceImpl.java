package annak.hc.service;

import annak.hc.dto.*;
import annak.hc.entity.GiftSet;
import annak.hc.mapper.GiftSetMapper;
import annak.hc.repository.GiftSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    public String save(NewGiftSetDto newGiftSetDto, List<GiftSetItemDto> giftSetItemDtoList) {
        GiftSet giftSet = giftSetMapper.toEntity(newGiftSetDto);
        giftSet.setFormationDate(LocalDateTime.now());
        giftSet.setId(giftSetRepository.save(giftSet).getId());

        BigDecimal totalCost = BigDecimal.ZERO;

        for (GiftSetItemDto giftSetItemDto : giftSetItemDtoList) {
            var giftSetItem = giftSetItemService.save(giftSet, giftSetItemDto);
            if (giftSetItem == null) {
                return "Не вдалося створити подарунковий набір, оскільки не всі товари було знайдено";
            }
            totalCost = totalCost.add(giftSetItem.getProductCost());
        }

        giftSet.setPrice(totalCost);
        giftSetRepository.save(giftSet);

        return "Подарунковий набір успішно збережено та додано до кошика";
    }

    @Override
    @Transactional
    public void deleteById(Long giftSetId) {
        giftSetItemService.deleteAllByGiftSetId(giftSetId);
        giftSetRepository.deleteById(giftSetId);
    }
}
