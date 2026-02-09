package co.com.nequi.api;

import co.com.nequi.api.constants.*;
import co.com.nequi.model.branch.Branch;
import co.com.nequi.usecase.branch.BranchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchHandler {
    private final BranchUseCase branchUseCase;

    public Mono<ServerResponse> addBranchToFranchise(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable(ApiConstants.ID_PARAM_NAME));
        return request.bodyToMono(Branch.class)
                .flatMap(branch -> branchUseCase.addBranchToFranchise(id, branch))
                .flatMap(savedBranch -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedBranch));
    }

    public Mono<ServerResponse> updateBranch(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable(ApiConstants.ID_PARAM_NAME));
        return request.bodyToMono(Branch.class)
                .flatMap(branch -> branchUseCase.updateBranchName(id, branch))
                .flatMap(updatedBranch -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedBranch));
    }
}
