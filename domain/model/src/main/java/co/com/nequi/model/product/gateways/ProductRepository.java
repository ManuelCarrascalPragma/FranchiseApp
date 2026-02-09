package co.com.nequi.model.product.gateways;

import co.com.nequi.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> save(Product product);

    Mono<Product> findByNameAndBranchId(String name, Long branchId);

    Mono<Product> findById(Long id);

    Mono<Void> deleteByIdAndBranchId(Long id, Long branchId);

    Flux<Product> findByFranchiseId(Long franchiseId);
}