package co.com.nequi.r2dbc.branch;

import co.com.nequi.r2dbc.entities.BranchEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BranchReactiveRepository extends
        ReactiveCrudRepository<BranchEntity, Long>,
        ReactiveQueryByExampleExecutor<BranchEntity> {
}