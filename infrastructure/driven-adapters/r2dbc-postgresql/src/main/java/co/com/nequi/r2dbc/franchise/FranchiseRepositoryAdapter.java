package co.com.nequi.r2dbc.franchise;

import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.gateways.FranchiseRepository;
import co.com.nequi.r2dbc.entities.FranchiseEntity;
import co.com.nequi.r2dbc.helper.ReactiveAdapterOperations;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class FranchiseRepositoryAdapter extends ReactiveAdapterOperations<Franchise, FranchiseEntity, Long, FranchiseReactiveRepository> implements FranchiseRepository {
    
    public FranchiseRepositoryAdapter(FranchiseReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Franchise.class));
    }

    @Override
    @CircuitBreaker(name = "database")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Mono<Franchise> findByName(String name) {
        return repository.findByName(name)
                .map(this::toEntity);
    }

    @Override
    @CircuitBreaker(name = "database")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Mono<Franchise> save(Franchise franchise) {
        return super.save(franchise);
    }

    @Override
    @CircuitBreaker(name = "database")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Mono<Franchise> findById(Long id) {
        return super.findById(id);
    }

    @Override
    @CircuitBreaker(name = "database")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Flux<Franchise> findAll() {
        return super.findAll();
    }
}
