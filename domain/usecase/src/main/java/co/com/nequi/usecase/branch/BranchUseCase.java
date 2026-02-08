package co.com.nequi.usecase.branch;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.branch.gateways.BranchRepository;
import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.exceptions.ResourceNotFoundException;
import co.com.nequi.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchUseCase {
    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    public Mono<Branch> addBranchToFranchise(Long franchiseId, Branch branch) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada")))
                .flatMap(franchise -> branchRepository.findByNameAndFranchiseId(branch.getName(), franchiseId)
                        .flatMap(exists -> Mono.<Branch>error(
                                new BusinessException(
                                        "La sucursal '" + branch.getName() + "' ya existe en esta franquicia"
                                )))
                        .switchIfEmpty(Mono.defer(() -> {
                            branch.setFranchiseId(franchiseId);
                            return branchRepository.save(branch);
                        }))
                );
    }
}
