package co.com.nequi.usecase.franchise;

import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.gateways.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
}
