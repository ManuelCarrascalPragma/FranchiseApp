package co.com.nequi.r2dbc.product;

import co.com.nequi.r2dbc.entities.ProductEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReactiveRepository extends ReactiveCrudRepository<ProductEntity, Long>,
        ReactiveQueryByExampleExecutor<ProductEntity> {

    Mono<ProductEntity> findByNameAndBranchId(String name, Long branchId);

    Mono<Void> deleteByIdAndBranchId(Long id, Long branchId);

    @Query("SELECT p.* FROM products p JOIN branches b ON p.branch_id = b.id WHERE b.franchise_id = :franchiseId")
    Flux<ProductEntity> findByFranchiseId(Long franchiseId);
}
