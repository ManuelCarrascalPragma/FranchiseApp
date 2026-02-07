package co.com.nequi.usecase.franchise;

import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> createFranchise(Franchise franchise) {
        if (franchise.getName() == null || franchise.getName().isEmpty()){
            return Mono.error(new BusinessException("Franchise name is empty"));
        }
        return franchiseRepository.findByName(franchise.getName())
                .flatMap(existing -> Mono.<Franchise>error(new BusinessException("Ya existe una franquicia con el nombre: " + franchise.getName())))
                .switchIfEmpty(franchiseRepository.save(franchise));
    }
}
