package annak.hc.repository;

import annak.hc.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ColorRepository extends JpaRepository<Color, Long> {
    List<Color> findAllByOrderByNameAsc();
    List<Color> findAllByIdIn(List<Long> id);

    Color findByName(String name);
    boolean existsByName(String name);

    @Query("""
            SELECT stats.colorName AS colorName, SUM(stats.countInOrders) AS totalCountInOrders
            FROM (
                SELECT c.name AS colorName, COUNT(oi.id) AS countInOrders
                FROM OrderItem oi
                JOIN oi.product p JOIN p.colors c
                WHERE oi.product IS NOT NULL
                GROUP BY c.name
        
                UNION ALL
        
                SELECT c2.name AS colorName, COUNT(gsi.id) AS countInOrders
                FROM OrderItem oi2
                JOIN oi2.giftSet gs JOIN gs.items gsi
                JOIN gsi.product p2 JOIN p2.colors c2
                WHERE oi2.giftSet IS NOT NULL
                GROUP BY c2.name
            ) stats
            GROUP BY stats.colorName
            ORDER BY totalCountInOrders DESC
        """)
    List<Object[]> getColorStatistics();
}
