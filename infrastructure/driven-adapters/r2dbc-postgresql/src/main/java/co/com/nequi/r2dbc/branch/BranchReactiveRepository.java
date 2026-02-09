package co.com.nequi.r2dbc.branch;

import co.com.nequi.r2dbc.entities.BranchEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchReactiveRepository extends ReactiveCrudRepository<BranchEntity, Long>, ReactiveQueryByExampleExecutor<BranchEntity> {
    Mono<BranchEntity> findByNameAndFranchiseId(String name, Long franchiseId);

    Flux<BranchEntity> findByFranchiseId(Long franchiseId);
}