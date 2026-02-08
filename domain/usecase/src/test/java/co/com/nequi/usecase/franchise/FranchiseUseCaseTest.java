package co.com.nequi.usecase.franchise;

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
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    private FranchiseUseCase franchiseUseCase;

    @BeforeEach
    void setUp() {
        franchiseUseCase = new FranchiseUseCase(franchiseRepository);
    }

    @Test
    void createFranchise_shouldReturnFranchise_whenNameIsValid() {
        Franchise franchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        when(franchiseRepository.findByName("Test Franchise")).thenReturn(Mono.empty());
        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseUseCase.createFranchise(franchise))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void createFranchise_shouldReturnError_whenNameIsNull() {
        Franchise franchise = Franchise.builder()
                .id(1L)
                .build();

        StepVerifier.create(franchiseUseCase.createFranchise(franchise))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        throwable.getMessage().equals("Franchise name is empty"))
                .verify();
    }

    @Test
    void createFranchise_shouldReturnError_whenNameIsEmpty() {
        Franchise franchise = Franchise.builder()
                .id(1L)
                .name("")
                .build();

        StepVerifier.create(franchiseUseCase.createFranchise(franchise))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        throwable.getMessage().equals("Franchise name is empty"))
                .verify();
    }

    @Test
    void updateFranchise_shouldUpdateName_whenIdAndNameAreValid() {
        Long id = 1L;
        Franchise existingFranchise = Franchise.builder().id(id).name("Old Name").build();
        Franchise updateInfo = Franchise.builder().name("New Name").build();
        Franchise savedFranchise = Franchise.builder().id(id).name("New Name").build();

        when(franchiseRepository.findById(id)).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(savedFranchise));

        StepVerifier.create(franchiseUseCase.updateFranchise(id, updateInfo))
                .expectNextMatches(f -> f.getName().equals("New Name"))
                .verifyComplete();
    }

    @Test
    void updateFranchise_shouldReturnError_whenNameIsInvalid() {
        Franchise invalidFranchise = Franchise.builder().name("   ").build();

        StepVerifier.create(franchiseUseCase.updateFranchise(1L, invalidFranchise))
                .expectErrorMatches(t -> t instanceof BusinessException &&
                        t.getMessage().equals("El nuevo nombre es obligatorio"))
                .verify();
    }

    @Test
    void updateFranchise_shouldReturnError_whenIdDoesNotExist() {
        Long id = 99L;
        Franchise updateInfo = Franchise.builder().name("New Name").build();

        when(franchiseRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.updateFranchise(id, updateInfo))
                .expectErrorMatches(t -> t instanceof ResourceNotFoundException &&
                        t.getMessage().contains("No se encontró la franquicia"))
                .verify();
    }
}
