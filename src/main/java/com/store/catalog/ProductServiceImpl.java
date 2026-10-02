package com.store.catalog;

import com.store.catalog.dto.*;
import com.store.common.dto.PageResponse;
import com.store.common.exception.BusinessException;
import com.store.common.exception.DuplicateResourceException;
import com.store.common.exception.ResourceNotFoundException;
import com.store.user.User;
import com.store.user.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductVariantRepository variantRepository,
                              UserRepository userRepository,
                              CategoryRepository categoryRepository,
                              BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
    }

    @Override
    public ProductResponse create(Long vendorId, ProductRequest request) {
        String slug = slugify(request.slug() != null ? request.slug() : request.name());
        if (productRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Product slug already exists: " + slug);
        }

        User vendor = userRepository.findById(vendorId)
            .orElseThrow(() -> new ResourceNotFoundException("Vendor", vendorId));

        Product product = new Product();
        product.setSlug(slug);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setShortDescription(request.shortDescription());
        product.setVendor(vendor);
        product.setBasePrice(request.basePrice());
        product.setCompareAtPrice(request.compareAtPrice());
        product.setCurrency(request.currency() != null ? request.currency() : "USD");
        product.setFeatured(request.featured() != null ? request.featured() : false);
        product.setHasVariants(request.hasVariants() != null ? request.hasVariants() : false);
        product.setStatus(ProductStatusEnum.DRAFT);

        if (request.categoryId() != null) {
            product.setCategory(categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId())));
        }
        if (request.brandId() != null) {
            product.setBrand(brandRepository.findById(request.brandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand", request.brandId())));
        }

        // Images
        if (request.images() != null) {
            for (ProductImageRequest imgReq : request.images()) {
                ProductImage img = new ProductImage();
                img.setProduct(product);
                img.setUrl(imgReq.url());
                img.setAlt(imgReq.alt());
                img.setSortOrder(imgReq.sortOrder() != null ? imgReq.sortOrder() : 0);
                img.setPrimary(imgReq.primary() != null ? imgReq.primary() : false);
                product.getImages().add(img);
            }
        }

        // Variants
        if (request.variants() != null) {
            Set<String> skus = new HashSet<>();
            for (ProductVariantRequest vReq : request.variants()) {
                if (!skus.add(vReq.sku())) {
                    throw new BusinessException("Duplicate SKU in request: " + vReq.sku());
                }
                if (variantRepository.existsBySku(vReq.sku())) {
                    throw new DuplicateResourceException("Variant SKU already exists: " + vReq.sku());
                }
                ProductVariant v = new ProductVariant();
                v.setProduct(product);
                v.setSku(vReq.sku());
                v.setName(vReq.name());
                v.setPrice(vReq.price());
                v.setCompareAtPrice(vReq.compareAtPrice());
                v.setCostPrice(vReq.costPrice());
                v.setWeightGrams(vReq.weightGrams());
                v.setBarcode(vReq.barcode());
                v.setImageUrl(vReq.imageUrl());
                v.setActive(vReq.active() != null ? vReq.active() : true);
                product.getVariants().add(v);
            }
        }

        return ProductResponse.from(productRepository.save(product));
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getEntity(id);

        if (request.name() != null) product.setName(request.name());
        if (request.description() != null) product.setDescription(request.description());
        if (request.shortDescription() != null) product.setShortDescription(request.shortDescription());
        if (request.basePrice() != null) product.setBasePrice(request.basePrice());
        if (request.compareAtPrice() != null) product.setCompareAtPrice(request.compareAtPrice());
        if (request.currency() != null) product.setCurrency(request.currency());
        if (request.featured() != null) product.setFeatured(request.featured());
        if (request.hasVariants() != null) product.setHasVariants(request.hasVariants());

        if (request.slug() != null && !request.slug().equals(product.getSlug())) {
            String slug = slugify(request.slug());
            if (productRepository.existsBySlug(slug)) {
                throw new DuplicateResourceException("Product slug already exists: " + slug);
            }
            product.setSlug(slug);
        }

        if (request.categoryId() != null) {
            product.setCategory(categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId())));
        }
        if (request.brandId() != null) {
            product.setBrand(brandRepository.findById(request.brandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand", request.brandId())));
        }

        return ProductResponse.from(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return ProductResponse.from(
            productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getBySlug(String slug) {
        return ProductResponse.from(
            productRepository.findBySlugWithImages(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + slug))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> list(Pageable pageable) {
        return PageResponse.of(productRepository.findAll(pageable), ProductSummaryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> listByStatus(ProductStatusEnum status, Pageable pageable) {
        return PageResponse.of(productRepository.findAllByStatus(status, pageable), ProductSummaryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> listByVendor(Long vendorId, Pageable pageable) {
        return PageResponse.of(productRepository.findAllByVendorId(vendorId, pageable), ProductSummaryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> listByCategory(Long categoryId, Pageable pageable) {
        return PageResponse.of(
            productRepository.findAllByCategoryIdAndStatus(categoryId, ProductStatusEnum.ACTIVE, pageable),
            ProductSummaryResponse::from
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> listByBrand(Long brandId, Pageable pageable) {
        return PageResponse.of(
            productRepository.findAllByBrandIdAndStatus(brandId, ProductStatusEnum.ACTIVE, pageable),
            ProductSummaryResponse::from
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> search(String keyword, Pageable pageable) {
        return PageResponse.of(productRepository.search(keyword, pageable), ProductSummaryResponse::from);
    }

    @Override
    public ProductResponse changeStatus(Long id, ProductStatusEnum status) {
        Product product = getEntity(id);
        product.setStatus(status);
        return ProductResponse.from(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        productRepository.delete(getEntity(id));
    }

    private Product getEntity(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private static String slugify(String input) {
        if (input == null) return "";
        return input.toLowerCase().trim()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-+|-+$", "");
    }
}