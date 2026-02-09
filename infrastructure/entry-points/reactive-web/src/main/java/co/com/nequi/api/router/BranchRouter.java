package co.com.nequi.api.router;

import co.com.nequi.api.BranchHandler;
import co.com.nequi.model.branch.Branch;
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
public class BranchRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/franchises/{id}/branches",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = BranchHandler.class,
                    beanMethod = "addBranchToFranchise",
                    operation = @Operation(
                            operationId = "addBranchToFranchise",
                            summary = "Add a branch to a franchise",
                            description = "Creates a new branch and links it to the specified franchise",
                            parameters = @Parameter(
                                    name = "id",
                                    description = "Existing franchise ID",
                                    in = ParameterIn.PATH,
                                    required = true
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Branch created and linked successfully",
                                            content = @Content(schema = @Schema(implementation = Branch.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Parent franchise does not exist"
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid branch data or duplicate name"
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Branch object to create (only name is required)",
                                    content = @Content(schema = @Schema(implementation = Branch.class))
                            )
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{id}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PATCH,
                    beanClass = BranchHandler.class,
                    beanMethod = "updateBranch",
                    operation = @Operation(
                            operationId = "updateBranch",
                            summary = "Update branch name",
                            description = "Updates the name of an existing branch",
                            parameters = @Parameter(
                                    name = "id",
                                    description = "Branch ID",
                                    in = ParameterIn.PATH,
                                    required = true
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Branch name updated successfully",
                                            content = @Content(schema = @Schema(implementation = Branch.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Branch not found"
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid data"
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Branch object with updated name",
                                    content = @Content(schema = @Schema(implementation = Branch.class))
                            )
                    )
            )
    })
    public RouterFunction<ServerResponse> branchRoutes(BranchHandler handler) {
        return route(POST("/api/franchises/{id}/branches"), handler::addBranchToFranchise)
                .andRoute(PATCH("/api/branches/{id}"), handler::updateBranch);
    }
}
