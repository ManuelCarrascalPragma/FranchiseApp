package co.com.nequi.model.branch.gateways;

import co.com.nequi.model.branch.Branch;
import reactor.core.publisher.Mono;

public interface BranchRepository {
    Mono<Branch> save(Branch branch);
    Mono<Branch> findByNameAndFranchiseId(String name, Long franchiseId);
}
