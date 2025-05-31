package annak.hc.service;

import annak.hc.dto.FavoriteProductDto;
import annak.hc.dto.FavoriteProductToGiftSetDto;
import annak.hc.dto.ProductDto;
import annak.hc.entity.FavoriteProduct;
import annak.hc.entity.User;
import annak.hc.mapper.FavoriteProductMapper;
import annak.hc.mapper.ProductMapper;
import annak.hc.repository.FavoriteProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteProductServiceImpl implements FavoriteProductService {

    private final FavoriteProductRepository favoriteProductRepository;
    private final FavoriteProductMapper favoriteProductMapper;

    private final ProductMapper productMapper;

    @Override
    public List<FavoriteProductDto> getAllByUser(User user) {
        return favoriteProductRepository.findAllByUser(user)
                .stream()
                .map(favoriteProductMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FavoriteProductToGiftSetDto> getAllForGiftSetByUser(User user) {
        return favoriteProductRepository.findAllByUser(user)
                .stream()
                .map(favoriteProductMapper::toGiftSetDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void save(User user, ProductDto productDto) {
        if (!favoriteProductRepository.existsByUserUserNameAndProductId(user.getUsername(), productDto.getId())) {
            FavoriteProduct favoriteProduct = new FavoriteProduct();
            favoriteProduct.setProduct(productMapper.toEntity(productDto));
            favoriteProduct.setUser(user);

            favoriteProductRepository.save(favoriteProduct);
        }
    }

    @Override
    @Transactional
    public String saveOrDeleteIfExists(User user, ProductDto productDto) {
        if (favoriteProductRepository.existsByUserUserNameAndProductId(user.getUsername(), productDto.getId())) {
            favoriteProductRepository.deleteByUserUserNameAndProductId(user.getUsername(), productDto.getId());
            return "Товар видалено з вподобаного";
        }
        else {
            FavoriteProduct favoriteProduct = new FavoriteProduct();
            favoriteProduct.setProduct(productMapper.toEntity(productDto));
            favoriteProduct.setUser(user);

            favoriteProductRepository.save(favoriteProduct);
            return "Товар додано до вподобаного";
        }
    }

    @Override
    public void deleteAllByProductId(Long productId) {
        favoriteProductRepository.deleteAllByProductId(productId);
    }
}
