package annak.hc.repository;

import annak.hc.entity.GiftSetItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftSetItemRepository extends JpaRepository<GiftSetItem, Long> {
    void deleteAllByGiftSetId(long giftSetId);
    void deleteAllByProductId(long productId);
}
