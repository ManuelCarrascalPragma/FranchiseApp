package co.com.nequi.r2dbc.product;

import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.gateways.ProductRepository;
import co.com.nequi.r2dbc.entities.ProductEntity;
import co.com.nequi.r2dbc.helper.ReactiveAdapterOperations;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ProductRepositoryAdapter extends ReactiveAdapterOperations<Product, ProductEntity, Long, ProductReactiveRepository> implements ProductRepository {
    
    public ProductRepositoryAdapter(ProductReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Product.class));
    }

    @Override
    @CircuitBreaker(name = "database")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Mono<Product> findByNameAndBranchId(String name, Long branchId) {
        return repository.findByNameAndBranchId(name, branchId)
                .map(this::toEntity);
    }

    @Override
    @CircuitBreaker(name = "database")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Mono<Void> deleteByIdAndBranchId(Long id, Long branchId) {
        return repository.deleteByIdAndBranchId(id, branchId);
    }

    @Override
    @CircuitBreaker(name = "database")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Flux<Product> findByFranchiseId(Long franchiseId) {
        return repository.findByFranchiseId(franchiseId)
                .map(this::toEntity);
    }

    @Override
    @CircuitBreaker(name = "database")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Mono<Product> save(Product product) {
        return super.save(product);
    }

    @Override
    @CircuitBreaker(name = "database")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Mono<Product> findById(Long id) {
        return super.findById(id);
    }

    @Override
    @CircuitBreaker(name = "database")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Flux<Product> findAll() {
        return super.findAll();
    }
}
