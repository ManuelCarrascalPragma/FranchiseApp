package co.com.nequi.api;

import co.com.nequi.model.franchise.Franchise;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/franchises",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Franquicia creada exitosamente",
                                            content = @Content(schema = @Schema(implementation = Franchise.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
                            },
                            requestBody = @RequestBody(
                                    content = @Content(schema = @Schema(implementation = Franchise.class))
                            )
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(FranchiseHandler handler) {
        return route(POST("/api/franchises"), handler::createFranchise);
    }
}
