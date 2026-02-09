package co.com.nequi.api;

import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.usecase.franchise.FranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import co.com.nequi.api.constants.*;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final FranchiseUseCase franchiseUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(Franchise.class)
                .flatMap(franchiseUseCase::createFranchise)
                .flatMap(franchise-> ServerResponse.status(HttpStatus.CREATED)
                        .header(HttpHeaders.LOCATION, ApiConstants.LOCATION_HEADER_PREFIX + 
                                ApiConstants.FRANCHISES_PATH + "/" + franchise.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(franchise));
    }

    public Mono<ServerResponse> updateFranchise(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable(ApiConstants.ID_PARAM_NAME));

        return request.bodyToMono(Franchise.class)
                .flatMap(franchise -> franchiseUseCase.updateFranchise(id, franchise))
                .flatMap(updatedFranchise -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedFranchise));
    }
}
