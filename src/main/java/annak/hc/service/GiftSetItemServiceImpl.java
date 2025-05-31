package annak.hc.service;

import annak.hc.dto.NewGiftSetItemDto;
import annak.hc.dto.ProductDto;
import annak.hc.entity.GiftSet;
import annak.hc.entity.GiftSetItem;
import annak.hc.mapper.ProductMapper;
import annak.hc.repository.GiftSetItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GiftSetItemServiceImpl implements GiftSetItemService {

    private final GiftSetItemRepository giftSetItemRepository;

    private final ProductService productService;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public GiftSetItem save(GiftSet giftSet, NewGiftSetItemDto newGiftSetItemDto) {
        Optional<ProductDto> productDtoOptional = productService.getNotDeletedById(newGiftSetItemDto.getProductId());
        if (productDtoOptional.isPresent()) {
            ProductDto productDto = productDtoOptional.get();
            var giftSetItem = new GiftSetItem();
            giftSetItem.setGiftSet(giftSet);
            giftSetItem.setProduct(productMapper.toEntity(productDto));
            giftSetItem.setQuantity(newGiftSetItemDto.getQuantity());
            giftSetItem.setId(giftSetItemRepository.save(giftSetItem).getId());
            return giftSetItem;
        }
        return null;
    }

    @Override
    public void deleteAllByGiftSetId(Long giftSetId) {
        giftSetItemRepository.deleteAllByGiftSetId(giftSetId);
    }

    @Override
    public void deleteAllByProductId(Long productId) {
        giftSetItemRepository.deleteAllByProductId(productId);
    }
}
