package annak.hc.service;

import annak.hc.dto.ProductDto;
import annak.hc.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Optional<Product> getEntityById(Long id);
    Optional<ProductDto> getById(Long id);
    Optional<Product> getEntityNotDeletedById(Long id);
    Optional<ProductDto> getNotDeletedById(Long id);
    List<ProductDto> getAllNotDeleted();
    List<ProductDto> getAllNotDeletedByFilter(boolean sortByCost, boolean sortByCostAsc, boolean sortByNewness, boolean sortByNewnessAsc, BigDecimal priceFrom, BigDecimal priceTo);
    List<ProductDto> getAllNotDeletedByCategoryId(Long categoryId);
    List<ProductDto> getAllNotDeletedBySearchLine(String searchLine);

    List<ProductDto> getRecommendedProducts(Long productId, Long userId);

    ProductDto save(ProductDto productDto);
    String update(ProductDto productDto);
    void deleteById(Long productId);
}
