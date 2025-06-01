package annak.hc.repository;

import annak.hc.entity.CartItem;
import annak.hc.entity.Product;
import annak.hc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByUserOrderById(User user);
    List<CartItem> findAllByProduct(Product product);

    Optional<CartItem> findByUserAndProductId(User user, Long productId);
    Optional<CartItem> findByUserAndGiftSetId(User user, Long giftSetId);

    boolean existsByUserUserNameAndGiftSetId(String userName, Long giftSetId);
    void deleteByUserUserNameAndGiftSetId(String userName, Long giftSetId);

    boolean existsByUserUserNameAndProductId(String userName, Long productId);
    void deleteByUserUserNameAndProductId(String userName, Long productId);
    void deleteAllByProductId(Long productId);
}
