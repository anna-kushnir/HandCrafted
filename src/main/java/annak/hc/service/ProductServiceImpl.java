package annak.hc.service;

import annak.hc.dto.ProductDto;
import annak.hc.entity.Color;
import annak.hc.entity.Product;
import annak.hc.mapper.ProductMapper;
import annak.hc.repository.ProductRepository;
import annak.hc.repository.spec.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final CategoryService categoryService;
    private final FavoriteProductService favoriteProductService;
    private final CartItemService cartItemService;
    private final ColorService colorService;

    @Override
    public Optional<Product> getEntityById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Optional<ProductDto> getById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDto);
    }

    @Override
    public Optional<Product> getEntityNotDeletedById(Long id) {
        return productRepository.findByIdAndDeletedIsFalse(id);
    }

    @Override
    public Optional<ProductDto> getNotDeletedById(Long id) {
        return productRepository.findByIdAndDeletedIsFalse(id)
                .map(productMapper::toDto);
    }

    @Override
    public List<ProductDto> getAllNotDeleted() {
        return productRepository.findAllByDeletedIsFalse()
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getAllNotDeletedByFilter(
            Long categoryId,
            boolean sortByCost, boolean sortByCostAsc,
            boolean sortByNewness, boolean sortByNewnessAsc,
            BigDecimal priceFrom, BigDecimal priceTo,
            List<Long> colorIds) {
        Specification<Product> spec = Specification
                .where(ProductSpecifications.notDeleted())
                .and(ProductSpecifications.priceBetween(priceFrom, priceTo));

        if (colorIds != null && !colorIds.isEmpty()) {
            Set<Color> colors = new HashSet<>(colorService.getAllByIds(colorIds));
            spec = spec.and(ProductSpecifications.hasColorIn(colors));
        }
        if (categoryId != 0) {
            var category = categoryService.getEntityById(categoryId);
            spec = spec.and(ProductSpecifications.hasCategory(category));
        }

        List<Product> productList = productRepository.findAll(spec);

        Comparator<Product> comparator = null;

        if (sortByCost) {
            Comparator<Product> priceComparator = Comparator.comparing(product ->
                    product.isWithDiscount() ? product.getDiscountedPrice() : product.getPrice());
            if (!sortByCostAsc) {
                priceComparator = priceComparator.reversed();
            }
            comparator = priceComparator;
        }

        if (sortByNewness) {
            Comparator<Product> dateComparator = Comparator.comparing(Product::getCreationDate);
            if (!sortByNewnessAsc) {
                dateComparator = dateComparator.reversed();
            }
            comparator = (comparator == null ? dateComparator : comparator.thenComparing(dateComparator));
        }

        if (comparator != null) {
            productList.sort(comparator);
        }

        return productList.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getAllNotDeletedByCategoryId(Long categoryId) {
        return productRepository.findAllByCategoryIdAndDeletedIsFalse(categoryId)
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getAllNotDeletedBySearchLine(Long categoryId, String searchLine) {
        return productRepository.searchProductsBySearchLineAndDeletedIsFalse(searchLine, categoryId)
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getRecommendedProducts(Long productId, Long userId) {
        var productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return List.of();
        }
        var product = productOptional.get();

        List<Product> collaborative;
        if (userId != null) {
            collaborative = productRepository.getFrequentlyBoughtTogetherWithUserId(productId, userId, PageRequest.of(0, 10));
        } else {
            collaborative = productRepository.getFrequentlyBoughtTogether(productId, PageRequest.of(0, 10));
        }
        Set<Product> recommendations = new LinkedHashSet<>(collaborative);

        if (recommendations.size() < 10) {
            int remaining = 10 - recommendations.size();

            String baseText = product.getName() + " " + product.getDescription() + " " + product.getKeyWords();

            List<Product> contentBased = productRepository.getByContentSimilarity(
                    productId,
                    product.getCategory().getId(),
                    product.getName(),
                    product.getKeyWords(),
                    baseText,
                    PageRequest.of(0, remaining)
            );
            recommendations.addAll(contentBased);
        }

        if (recommendations.size() < 10) {
            int remaining = 10 - recommendations.size();
            List<Product> fallback = productRepository.getTopByNotDeletedAndNotId(productId, PageRequest.of(0, remaining));
            recommendations.addAll(fallback);
        }

        return recommendations
                .stream()
                .limit(10)
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductDto save(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        product.setCreationDate(LocalDateTime.now());
        product.setWithDiscount(false);
        product.setDeleted(false);
        if (!product.isInStock()) {
            product.setQuantity(0L);
        } else if (product.getQuantity() == 0L) {
            product.setInStock(false);
        }
        if (!product.isCanAddToGiftSet()) {
            product.setMaxQuantityInGiftSet(0L);
        } else if (product.getMaxQuantityInGiftSet() == 0L) {
            product.setCanAddToGiftSet(false);
        }
        product.setId(productRepository.save(product).getId());
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public String update(ProductDto productDto) {
        Optional<Product> productOptional = productRepository.findByIdAndDeletedIsFalse(productDto.getId());
        if (productOptional.isEmpty())
            return "Товар з id <%s> не знайдено!".formatted(productDto.getId());
        Product oldProduct = productOptional.get();
        Product product = productMapper.toEntity(productDto);
        product.setCategory(categoryService.getEntityById(productDto.getCategoryId()));
        product.setCreationDate(oldProduct.getCreationDate());
        product.setDeleted(false);
        if (!product.isInStock()) {
            cartItemService.deleteAllByProductId(product.getId());
            product.setQuantity(0L);
        }
        else if (product.getQuantity() == 0L) {
            cartItemService.deleteAllByProductId(product.getId());
            product.setInStock(false);
        } else if (oldProduct.getQuantity() > product.getQuantity()) {
            cartItemService.updateQuantityByProductId(product.getId(), product.getQuantity());
        }
        product.setCanAddToGiftSet(oldProduct.isCanAddToGiftSet());
        product.setMaxQuantityInGiftSet(oldProduct.getMaxQuantityInGiftSet());
        return "Товар з id <%s> було успішно оновлено".formatted(
                productRepository.save(product).getId()
        );
    }

    @Override
    @Transactional
    public void deleteById(Long productId) {
        Optional<Product> productOptional = productRepository.findByIdAndDeletedIsFalse(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setDeleted(true);
            cartItemService.deleteAllByProductId(productId);
            favoriteProductService.deleteAllByProductId(productId);
            productRepository.save(product);
        }
    }
}
