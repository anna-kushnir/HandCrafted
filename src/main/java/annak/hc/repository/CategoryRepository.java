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

    @Query("""
            SELECT stats.categoryName AS categoryName, SUM(stats.countInOrders) AS totalCountInOrders
            FROM (
                SELECT c.name AS categoryName, COUNT(oi.id) AS countInOrders
                FROM OrderItem oi
                JOIN oi.product p JOIN p.category c
                WHERE oi.product IS NOT NULL
                GROUP BY c.name
        
                UNION ALL
        
                SELECT c2.name AS categoryName, COUNT(gsi.id) AS countInOrders
                FROM OrderItem oi2
                JOIN oi2.giftSet gs JOIN gs.items gsi
                JOIN gsi.product p2 JOIN p2.category c2
                WHERE oi2.giftSet IS NOT NULL
                GROUP BY c2.name
            ) stats
            GROUP BY stats.categoryName
            ORDER BY totalCountInOrders DESC
        """)
    List<Object[]> getCategoryStatistics();
}
