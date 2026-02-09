package co.com.nequi.api;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.exceptions.ResourceNotFoundException;
import co.com.nequi.usecase.branch.BranchUseCase;
import co.com.nequi.usecase.constants.ErrorMessages;
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
class BranchHandlerTest {

    @Mock
    private BranchUseCase branchUseCase;

    private BranchHandler branchHandler;

    @BeforeEach
    void setUp() {
        branchHandler = new BranchHandler(branchUseCase);
    }

    @Test
    void addBranchToFranchise_Success() {
        Branch branch = Branch.builder()
                .name("Test Branch")
                .build();

        Branch savedBranch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .build();

        when(branchUseCase.addBranchToFranchise(eq(1L), any(Branch.class)))
                .thenReturn(Mono.just(savedBranch));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .body(Mono.just(branch));

        Mono<ServerResponse> response = branchHandler.addBranchToFranchise(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.CREATED)
                .verifyComplete();
    }

    @Test
    void addBranchToFranchise_FranchiseNotFound_ShouldPropagateError() {
        Branch branch = Branch.builder()
                .name("Test Branch")
                .build();

        when(branchUseCase.addBranchToFranchise(eq(99L), any(Branch.class)))
                .thenReturn(Mono.error(new ResourceNotFoundException(
                        String.format(ErrorMessages.FRANCHISE_NOT_FOUND, 99L))));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "99")
                .body(Mono.just(branch));

        Mono<ServerResponse> response = branchHandler.addBranchToFranchise(request);

        StepVerifier.create(response)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void updateBranch_Success() {
        Branch updateData = Branch.builder()
                .name("Updated Branch")
                .build();

        Branch updatedBranch = Branch.builder()
                .id(1L)
                .name("Updated Branch")
                .franchiseId(1L)
                .build();

        when(branchUseCase.updateBranchName(eq(1L), any(Branch.class)))
                .thenReturn(Mono.just(updatedBranch));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .body(Mono.just(updateData));

        Mono<ServerResponse> response = branchHandler.updateBranch(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void updateBranch_NotFound_ShouldPropagateError() {
        Branch updateData = Branch.builder()
                .name("Updated Branch")
                .build();

        when(branchUseCase.updateBranchName(eq(99L), any(Branch.class)))
                .thenReturn(Mono.error(new ResourceNotFoundException(
                        String.format(ErrorMessages.BRANCH_NOT_FOUND, 99L))));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "99")
                .body(Mono.just(updateData));

        Mono<ServerResponse> response = branchHandler.updateBranch(request);

        StepVerifier.create(response)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}
