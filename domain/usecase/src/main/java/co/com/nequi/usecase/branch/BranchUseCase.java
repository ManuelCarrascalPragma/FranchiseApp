package co.com.nequi.usecase.branch;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.branch.gateways.BranchRepository;
import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.exceptions.ResourceNotFoundException;
import co.com.nequi.model.franchise.gateways.FranchiseRepository;
import co.com.nequi.usecase.constants.ErrorMessages;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchUseCase {
    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    public Mono<Branch> addBranchToFranchise(Long franchiseId, Branch branch) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        String.format(ErrorMessages.FRANCHISE_NOT_FOUND, franchiseId))))
                .flatMap(franchise -> branchRepository.findByNameAndFranchiseId(branch.getName(), franchiseId)
                        .flatMap(exists -> Mono.<Branch>error(
                                new BusinessException(
                                        String.format(ErrorMessages.BRANCH_NAME_ALREADY_EXISTS, branch.getName())
                                )))
                        .switchIfEmpty(Mono.defer(() -> {
                            branch.setFranchiseId(franchiseId);
                            return branchRepository.save(branch);
                        }))
                );
    }

    public Mono<Branch> updateBranchName(Long id, Branch branch) {
        if (branch.getName() == null || branch.getName().trim().isEmpty()) {
            return Mono.error(new BusinessException(ErrorMessages.BRANCH_NAME_REQUIRED));
        }

        return branchRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        String.format(ErrorMessages.BRANCH_NOT_FOUND, id))))
                .flatMap(foundBranch -> {
                    foundBranch.setName(branch.getName());
                    return branchRepository.save(foundBranch);
                });
    }
}
