package annak.hc.service;

import annak.hc.dto.FavoriteProductDto;
import annak.hc.dto.ProductDto;
import annak.hc.entity.User;

import java.util.List;

public interface FavoriteProductService {
    List<FavoriteProductDto> getAllByUser(User user);

    void save(User user, ProductDto productDto);
    String saveOrDeleteIfExists(User user, ProductDto productDto);
    void deleteAllByProductId(Long productId);
}
