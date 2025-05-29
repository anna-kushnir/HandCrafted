package annak.hc.repository;

import annak.hc.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("""
            SELECT c FROM Category c
            ORDER BY
                CASE WHEN c.id = 1 THEN 1 ELSE 0 END,
                c.name ASC
        """)
    List<Category> findAllOrderByNameAscAndOthersLast();

    @Query("SELECT c.name AS categoryName, COUNT(oi.id) AS countInOrders " +
            "FROM OrderItem oi JOIN oi.product p JOIN p.category c " +
            "GROUP BY c.name ORDER BY countInOrders DESC")
    List<Object[]> getCategoryStatistics();
}
