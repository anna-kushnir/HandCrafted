package annak.hc.repository;

import annak.hc.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ColorRepository extends JpaRepository<Color, Long> {
    List<Color> findAllByOrderByNameAsc();

    Color findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT c.name AS colorName, COUNT(oi.id) AS countInOrders " +
            "FROM OrderItem oi JOIN oi.product p JOIN p.colors c " +
            "GROUP BY c.name ORDER BY countInOrders DESC")
    List<Object[]> getColorStatistics();
}
