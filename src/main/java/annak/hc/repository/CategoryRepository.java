package annak.hc.repository;

import annak.hc.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderByNameAsc();

    @Query("SELECT c.name AS categoryName, COUNT(oi.id) AS countInOrders " +
            "FROM OrderItem oi JOIN oi.product p JOIN p.category c " +
            "GROUP BY c.name ORDER BY countInOrders DESC")
    List<Object[]> getCategoryStatistics();
}
