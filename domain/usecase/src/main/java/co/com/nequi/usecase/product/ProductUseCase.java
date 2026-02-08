package co.com.nequi.usecase.product;

import co.com.nequi.model.branch.gateways.BranchRepository;
import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.exceptions.ResourceNotFoundException;
import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductUseCase {
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    public Mono<Product> addProductToBranch(Long branchId, Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return Mono.error(new BusinessException("El nombre del producto es obligatorio"));
        }

        if (product.getStock() == null || product.getStock() < 0) {
            return Mono.error(new BusinessException("El stock debe ser un número mayor o igual a cero"));
        }
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró la sucursal con id: " + branchId)))
                .flatMap(branch ->
                        productRepository.findByNameAndBranchId(product.getName(), branchId)
                                .flatMap(exists -> Mono.<Product>error(
                                        new BusinessException("El producto '" + product.getName() + "' ya existe en esta sucursal")))
                                .switchIfEmpty(Mono.defer(() -> {
                                    product.setBranchId(branchId);
                                    return productRepository.save(product);
                                }))
                );
    }
}
