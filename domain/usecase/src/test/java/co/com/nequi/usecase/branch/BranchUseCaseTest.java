package co.com.nequi.usecase.branch;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.branch.gateways.BranchRepository;
import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.exceptions.ResourceNotFoundException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.gateways.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(MockitoExtension.class)
class BranchUseCaseTest {

    @Mock
    private BranchRepository branchRepository;
    @Mock
    private FranchiseRepository franchiseRepository;

    private BranchUseCase branchUseCase;

    @BeforeEach
    void setUp() {
        branchUseCase = new BranchUseCase(branchRepository, franchiseRepository);
    }

    @Test
    void addBranchToFranchise_shouldSaveBranch_whenDataIsValid() {
        Long franchiseId = 1L;
        Branch branchInput = Branch.builder().name("Sucursal A").build();
        Branch branchSaved = Branch.builder().id(10L).name("Sucursal A").franchiseId(franchiseId).build();
        Franchise franchise = Franchise.builder().id(franchiseId).name("Nequi").build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchRepository.findByNameAndFranchiseId("Sucursal A", franchiseId)).thenReturn(Mono.empty());
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(branchSaved));

        StepVerifier.create(branchUseCase.addBranchToFranchise(franchiseId, branchInput))
                .expectNext(branchSaved)
                .verifyComplete();
    }

    @Test
    void addBranchToFranchise_shouldError_whenFranchiseNotFound() {
        Long id = 1L;
        when(franchiseRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.addBranchToFranchise(id, Branch.builder().build()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void addBranchToFranchise_shouldError_whenBranchAlreadyExists() {
        Long fId = 1L;
        Branch branchInput = Branch.builder().name("Duplicada").build();
        Branch existingBranch = Branch.builder().name("Duplicada").franchiseId(fId).build();

        when(franchiseRepository.findById(fId)).thenReturn(Mono.just(Franchise.builder().id(fId).build()));
        when(branchRepository.findByNameAndFranchiseId("Duplicada", fId)).thenReturn(Mono.just(existingBranch));

        StepVerifier.create(branchUseCase.addBranchToFranchise(fId, branchInput))
                .expectErrorMatches(t -> t instanceof BusinessException &&
                        t.getMessage().contains("ya existe en esta franquicia"))
                .verify();
    }
}