package annak.hc.repository;

import annak.hc.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrderId(Long orderId);
    Optional<OrderItem> findByOrderIdAndGiftSetId(Long orderId, Long giftSetId);
}
