package annak.hc.repository;

import annak.hc.entity.GiftSetItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftSetItemRepository extends JpaRepository<GiftSetItem, Long> {
    List<GiftSetItem> findAllByGiftSetId(long giftSetId);
    List<GiftSetItem> findAllByProductId(long productId);
    List<GiftSetItem> findAllByProductIdAndProductCostIsNull(long productId);

    void deleteAllByGiftSetId(long giftSetId);
    void deleteAllByProductId(long productId);
}
