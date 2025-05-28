package annak.hc.repository;

import annak.hc.entity.FavoriteProduct;
import annak.hc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {
    List<FavoriteProduct> findAllByUser(User user);
    boolean existsByUserUserNameAndProductId(String userName, Long productId);
    void deleteByUserUserNameAndProductId(String userName, Long productId);
    void deleteAllByProductId(Long productId);
}
