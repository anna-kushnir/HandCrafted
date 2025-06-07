package annak.hc.service;

import annak.hc.config.GlobalVariables;
import annak.hc.dto.*;
import annak.hc.entity.GiftSet;
import annak.hc.entity.GiftSetItem;
import annak.hc.entity.User;
import annak.hc.exception.ResourceNotFoundException;
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
    private final ProductService productService;

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
    public BigDecimal setPriceForGiftSetAndItsItems(Long giftSetId) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        var giftSetOptional = giftSetRepository.findById(giftSetId);
        if (giftSetOptional.isEmpty()) {
            throw new ResourceNotFoundException("Подарунковий набір з id <%s> не знайдено".formatted(giftSetId));
        }
        var giftSet = giftSetOptional.get();

        for (var giftSetItem : giftSet.getItems()) {
            var productDtoOptional = productService.getNotDeletedById(giftSetItem.getProduct().getId());
            if (productDtoOptional.isEmpty()) {
                throw new ResourceNotFoundException("Товар з id <%s> не знайдено".formatted(giftSetItem.getProduct().getId()));
            }
            var product = productDtoOptional.get();
            giftSetItem.setProductCost(product.isWithDiscount() ? product.getDiscountedPrice() : product.getPrice());
            giftSetItemService.update(giftSetItem);
            totalPrice = totalPrice.add(giftSetItem.getProductCost().multiply(BigDecimal.valueOf(giftSetItem.getQuantity())));
        }

        BigDecimal wrapPrice = GlobalVariables.WRAP_PRICE;
        giftSet.setPrice(totalPrice.add(wrapPrice));
        giftSetRepository.save(giftSet);
        return giftSet.getPrice();
    }

    @Override
    public BigDecimal countTotalPriceForItems(List<GiftSetItem> items) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (var item : items) {
            totalPrice = totalPrice.add(item.getProductCost().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return totalPrice;
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
