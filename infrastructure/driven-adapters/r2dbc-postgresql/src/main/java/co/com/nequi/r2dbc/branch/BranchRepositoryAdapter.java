package co.com.nequi.r2dbc.branch;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.branch.gateways.BranchRepository;
import co.com.nequi.r2dbc.entities.BranchEntity;
import co.com.nequi.r2dbc.helper.ReactiveAdapterOperations;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class BranchRepositoryAdapter extends ReactiveAdapterOperations<Branch, BranchEntity,Long,BranchReactiveRepository> implements BranchRepository {
    
    public BranchRepositoryAdapter(BranchReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Branch.class));
    }

    @Override
    @CircuitBreaker(name = "database")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Mono<Branch> findByNameAndFranchiseId(String name, Long franchiseId) {
        return repository.findByNameAndFranchiseId(name, franchiseId)
                .map(this::toEntity);
    }

    @Override
    @CircuitBreaker(name = "database")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Flux<Branch> findByFranchiseId(Long franchiseId) {
        return repository.findByFranchiseId(franchiseId)
                .map(this::toEntity);
    }

    @Override
    @CircuitBreaker(name = "database")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Mono<Branch> save(Branch branch) {
        return super.save(branch);
    }

    @Override
    @CircuitBreaker(name = "database")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Mono<Branch> findById(Long id) {
        return super.findById(id);
    }

    @Override
    @CircuitBreaker(name = "database")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Flux<Branch> findAll() {
        return super.findAll();
    }
}
