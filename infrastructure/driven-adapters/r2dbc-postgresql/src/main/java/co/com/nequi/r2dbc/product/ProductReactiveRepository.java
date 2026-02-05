package co.com.nequi.r2dbc.product;

import co.com.nequi.r2dbc.entities.ProductEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductReactiveRepository extends ReactiveCrudRepository<ProductEntity, Long>,
        ReactiveQueryByExampleExecutor<ProductEntity> {

}
