package co.com.nequi.usecase.product;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.branch.gateways.BranchRepository;
import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.exceptions.ResourceNotFoundException;
import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.ProductMaxStock;
import co.com.nequi.model.product.gateways.ProductRepository;
import co.com.nequi.usecase.constants.ErrorMessages;
import co.com.nequi.usecase.constants.ValidationConstants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ProductUseCase {
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    public Mono<Product> addProductToBranch(Long branchId, Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return Mono.error(new BusinessException(ErrorMessages.PRODUCT_NAME_REQUIRED));
        }

        if (product.getStock() == null || product.getStock() < ValidationConstants.MIN_STOCK_VALUE) {
            return Mono.error(new BusinessException(ErrorMessages.PRODUCT_STOCK_INVALID));
        }
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        String.format(ErrorMessages.BRANCH_NOT_FOUND, branchId))))
                .flatMap(branch ->
                        productRepository.findByNameAndBranchId(product.getName(), branchId)
                                .flatMap(exists -> Mono.<Product>error(
                                        new BusinessException(String.format(ErrorMessages.PRODUCT_NAME_ALREADY_EXISTS, product.getName()))))
                                .switchIfEmpty(Mono.defer(() -> {
                                    product.setBranchId(branchId);
                                    return productRepository.save(product);
                                }))
                );
    }

    public Mono<Product> updateProduct(Long id, Product product) {
        if(product.getName() == null || product.getName().trim().isEmpty()) {
            return Mono.error(new BusinessException(ErrorMessages.PRODUCT_NAME_REQUIRED));
        }

        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        String.format(ErrorMessages.PRODUCT_NOT_FOUND, id))))
                .flatMap(foundProduct -> {
                    foundProduct.setName(product.getName());
                    return productRepository.save(foundProduct);
                });
    }

    public Mono<Void> deleteProductFromBranch(Long branchId, Long productId) {
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        String.format(ErrorMessages.BRANCH_NOT_FOUND, branchId))))
                .flatMap(branch -> productRepository.findById(productId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                                String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId))))
                        .flatMap(product -> {
                            if (!product.getBranchId().equals(branchId)) {
                                return Mono.error(new BusinessException(ErrorMessages.PRODUCT_DOES_NOT_BELONG_TO_BRANCH));
                            }
                            return productRepository.deleteByIdAndBranchId(productId, branchId);
                        })
                );
    }

    public Mono<Product> updateProductStock(Long branchId, Long productId, Product productWithNewStock) {
        if (productWithNewStock.getStock() == null || productWithNewStock.getStock() < ValidationConstants.MIN_STOCK_VALUE) {
            return Mono.error(new BusinessException(ErrorMessages.PRODUCT_STOCK_INVALID));
        }

        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        String.format(ErrorMessages.BRANCH_NOT_FOUND, branchId))))
                .flatMap(branch -> productRepository.findById(productId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                                String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId))))
                        .flatMap(foundProduct -> {
                            if (!foundProduct.getBranchId().equals(branchId)) {
                                return Mono.error(new BusinessException(ErrorMessages.PRODUCT_DOES_NOT_BELONG_TO_BRANCH));
                            }

                            foundProduct.setStock(productWithNewStock.getStock());
                            return productRepository.save(foundProduct);
                        })
                );
    }

    public Flux<ProductMaxStock> getMaxStockProductsByFranchise(Long franchiseId) {
        return branchRepository.findByFranchiseId(franchiseId)
                .collectList()
                .flatMapMany(branches -> {
                    if (branches.isEmpty()) {
                        return Flux.empty();
                    }
                    Map<Long, String> branchNames = branches.stream()
                            .collect(Collectors.toMap(Branch::getId, Branch::getName));

                    return productRepository.findByFranchiseId(franchiseId)
                            .collectMultimap(Product::getBranchId)
                            .flatMapMany(map -> Flux.fromIterable(map.entrySet())
                                    .map(entry -> {
                                        Product maxProduct = entry.getValue().stream()
                                                .max(Comparator.comparing(Product::getStock))
                                                .orElseThrow();

                                        return ProductMaxStock.builder()
                                                .branchName(branchNames.get(entry.getKey()))
                                                .productName(maxProduct.getName())
                                                .stock(maxProduct.getStock())
                                                .build();
                                    }));
                });
    }
}
