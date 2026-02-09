package co.com.nequi.api;

import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.exceptions.ResourceNotFoundException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.usecase.constants.ErrorMessages;
import co.com.nequi.usecase.franchise.FranchiseUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseHandlerTest {

    @Mock
    private FranchiseUseCase franchiseUseCase;

    private FranchiseHandler franchiseHandler;

    @BeforeEach
    void setUp() {
        franchiseHandler = new FranchiseHandler(franchiseUseCase);
    }

    @Test
    void createFranchise_Success() {
        Franchise franchise = Franchise.builder()
                .name("Test Franchise")
                .build();

        Franchise savedFranchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        when(franchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(franchise));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.CREATED)
                .verifyComplete();
    }

    @Test
    void createFranchise_WithBusinessException_ShouldPropagateError() {
        Franchise franchise = Franchise.builder()
                .name("Test Franchise")
                .build();

        when(franchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.error(new BusinessException(ErrorMessages.FRANCHISE_NAME_REQUIRED)));

        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(franchise));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(request);

        StepVerifier.create(response)
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void updateFranchise_Success() {
        Franchise updateData = Franchise.builder()
                .name("Updated Name")
                .build();

        Franchise updatedFranchise = Franchise.builder()
                .id(1L)
                .name("Updated Name")
                .build();

        when(franchiseUseCase.updateFranchise(eq(1L), any(Franchise.class)))
                .thenReturn(Mono.just(updatedFranchise));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .body(Mono.just(updateData));

        Mono<ServerResponse> response = franchiseHandler.updateFranchise(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void updateFranchise_NotFound_ShouldPropagateError() {
        Franchise updateData = Franchise.builder()
                .name("Updated Name")
                .build();

        when(franchiseUseCase.updateFranchise(eq(99L), any(Franchise.class)))
                .thenReturn(Mono.error(new ResourceNotFoundException(
                        String.format(ErrorMessages.FRANCHISE_NOT_FOUND, 99L))));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "99")
                .body(Mono.just(updateData));

        Mono<ServerResponse> response = franchiseHandler.updateFranchise(request);

        StepVerifier.create(response)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}
