package co.com.nequi.usecase.franchise;

import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.exceptions.ResourceNotFoundException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.gateways.FranchiseRepository;
import co.com.nequi.usecase.constants.ErrorMessages;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> createFranchise(Franchise franchise) {
        if (franchise.getName() == null || franchise.getName().isEmpty()){
            return Mono.error(new BusinessException(ErrorMessages.FRANCHISE_NAME_REQUIRED));
        }
        return franchiseRepository.findByName(franchise.getName())
                .flatMap(existing -> Mono.<Franchise>error(
                        new BusinessException(String.format(ErrorMessages.FRANCHISE_NAME_ALREADY_EXISTS, franchise.getName()))))
                .switchIfEmpty(franchiseRepository.save(franchise));
    }

    public Mono<Franchise> updateFranchise(Long id, Franchise franchise) {
        if (franchise.getName() == null || franchise.getName().trim().isEmpty()) {
            return Mono.error(new BusinessException(ErrorMessages.FRANCHISE_NAME_REQUIRED));
        }

        return franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format(ErrorMessages.FRANCHISE_NOT_FOUND, id))))
                .flatMap(foundFranchise -> {
                    foundFranchise.setName(franchise.getName());
                    return franchiseRepository.save(foundFranchise);
                });
    }
}
