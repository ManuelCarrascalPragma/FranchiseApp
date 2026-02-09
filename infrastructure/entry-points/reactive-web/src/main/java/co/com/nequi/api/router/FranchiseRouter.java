package co.com.nequi.api.router;

import co.com.nequi.api.FranchiseHandler;
import co.com.nequi.model.franchise.Franchise;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FranchiseRouter {

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
                            summary = "Create a new franchise",
                            description = "Creates a new franchise with the provided name",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Franchise created successfully",
                                            content = @Content(schema = @Schema(implementation = Franchise.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid request data"
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Franchise object to create",
                                    content = @Content(schema = @Schema(implementation = Franchise.class))
                            )
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{id}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PATCH,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "updateFranchise",
                    operation = @Operation(
                            operationId = "updateFranchise",
                            summary = "Update franchise name",
                            description = "Updates the name of an existing franchise",
                            parameters = @Parameter(
                                    name = "id",
                                    description = "Franchise ID",
                                    in = ParameterIn.PATH,
                                    required = true
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Franchise updated successfully",
                                            content = @Content(schema = @Schema(implementation = Franchise.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Franchise not found"
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid data"
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Franchise object with updated name",
                                    content = @Content(schema = @Schema(implementation = Franchise.class))
                            )
                    )
            )
    })
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler handler) {
        return route(POST("/api/franchises"), handler::createFranchise)
                .andRoute(PATCH("/api/franchises/{id}"), handler::updateFranchise);
    }
}
