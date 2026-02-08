package co.com.nequi.r2dbc.product;

import co.com.nequi.r2dbc.entities.ProductEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ProductReactiveRepository extends ReactiveCrudRepository<ProductEntity, Long>,
        ReactiveQueryByExampleExecutor<ProductEntity> {

    Mono<ProductEntity> findByNameAndBranchId(String name, Long branchId);
}
