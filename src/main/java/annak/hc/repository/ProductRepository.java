package annak.hc.repository;

import annak.hc.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findAllByDeletedIsFalse();

    List<Product> findAllByCategoryIdAndDeletedIsFalse(Long categoryId);

    // TODO: налаштувати сортування за кольорами
//    List<Product> findAllByColorsContainsAndDeletedIsFalse(Set<Color> colors);

    Optional<Product> findByIdAndDeletedIsFalse(Long id);

    @Query("""
            SELECT DISTINCT p
            FROM Product p
            LEFT JOIN p.category c
            LEFT JOIN p.colors col
            WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.keyWords) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(col.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
               AND NOT p.deleted
            """)
    List<Product> searchProductsBySearchLineAndDeletedIsFalse(@Param("keyword") String searchLine);

    @Query("""
            SELECT DISTINCT p
            FROM Product p
            LEFT JOIN p.category c
            LEFT JOIN p.colors col
            WHERE c.id = :categoryId AND
               (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.keyWords) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(col.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
               AND NOT p.deleted
            """)
    List<Product> searchProductsBySearchLineAndCategoryIdAndDeletedIsFalse(@Param("keyword") String searchLine, @Param("categoryId") long categoryId);

    @Query("""
            SELECT DISTINCT p
            FROM Product p
            WHERE p.id <> :productId
                AND p.deleted = false
                AND (
                    p IN (
                        SELECT oi.product FROM OrderItem oi
                        WHERE oi.order.user.id <> :userId
                            AND oi.product.id <> :productId
                            AND oi.order IN (
                                SELECT oi2.order FROM OrderItem oi2
                                LEFT JOIN oi2.giftSet.items gsi
                                WHERE oi2.product.id = :productId
                                    OR gsi.product.id = :productId
                            )
                    )
                    OR p IN (
                        SELECT gsi.product FROM OrderItem oi
                        JOIN oi.giftSet.items gsi
                        WHERE oi.order.user.id <> :userId
                            AND gsi.product.id <> :productId
                            AND oi.order IN (
                                SELECT oi2.order FROM OrderItem oi2
                                LEFT JOIN oi2.giftSet.items gsi2
                                WHERE oi2.product.id = :productId
                                    OR gsi2.product.id = :productId
                            )
                    )
                )
            """)
    List<Product> getFrequentlyBoughtTogetherWithUserId(@Param("productId") Long productId, @Param("userId") Long userId, Pageable pageable);

    @Query("""
            SELECT DISTINCT p
            FROM Product p
            WHERE p.id <> :productId
                AND p.deleted = false
                AND (
                    p IN (
                        SELECT oi.product FROM OrderItem oi
                        WHERE oi.product.id <> :productId
                            AND oi.order IN (
                                SELECT oi2.order FROM OrderItem oi2
                                LEFT JOIN oi2.giftSet.items gsi
                                WHERE oi2.product.id = :productId
                                    OR gsi.product.id = :productId
                            )
                    )
                    OR p IN (
                        SELECT gsi.product FROM OrderItem oi
                        JOIN oi.giftSet.items gsi
                        WHERE gsi.product.id <> :productId
                            AND oi.order IN (
                                SELECT oi2.order FROM OrderItem oi2
                                LEFT JOIN oi2.giftSet.items gsi2
                                WHERE oi2.product.id = :productId
                                    OR gsi2.product.id = :productId
                            )
                    )
                )
            """)
    List<Product> getFrequentlyBoughtTogether(@Param("productId") Long productId, Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            WHERE p.id <> :productId
                AND NOT p.deleted
                AND (
                    LOWER(p.name) LIKE LOWER(CONCAT('%', :productName, '%')) OR
                    LOWER(p.name) LIKE LOWER(CONCAT('%', :text, '%')) OR
                    LOWER(p.description) LIKE LOWER(CONCAT('%', :text, '%')) OR
                    LOWER(p.keyWords) LIKE LOWER(CONCAT('%', :productKeyWords, '%')) OR
                    LOWER(p.keyWords) LIKE LOWER(CONCAT('%', :text, '%')) OR
                    p.category.id = :categoryId
                )
            ORDER BY
                CASE
                    WHEN LOWER(p.name) LIKE LOWER(CONCAT('%', :productName, '%')) THEN 1
                    WHEN LOWER(p.name) LIKE LOWER(CONCAT('%', :text, '%')) THEN 2
                    WHEN LOWER(p.description) LIKE LOWER(CONCAT('%', :text, '%')) THEN 3
                    WHEN LOWER(p.keyWords) LIKE LOWER(CONCAT('%', :productKeyWords, '%')) THEN 4
                    WHEN LOWER(p.keyWords) LIKE LOWER(CONCAT('%', :text, '%')) THEN 5
                    WHEN p.category.id = :categoryId THEN 6
                    ELSE 7
                END
          """)
    List<Product> getByContentSimilarity(@Param("productId") Long productId,
                                         @Param("categoryId") Long categoryId,
                                         @Param("productName") String productName,
                                         @Param("productKeyWords") String productKeyWords,
                                         @Param("text") String text,
                                         Pageable pageable);

    @Query("""
            SELECT p
            FROM Product p
            WHERE p.id <> :productId
                AND NOT p.deleted
            """)
    List<Product> getTopByNotDeletedAndNotId(@Param("productId") Long productId, Pageable pageable);
}
