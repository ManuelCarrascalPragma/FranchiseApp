package co.com.nequi.api;

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
            ),
            @RouterOperation(
                    path = "/api/franchises/{id}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PATCH,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "updateFranchise",
                    operation = @Operation(
                            operationId = "updateFranchise",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Actualización exitosa"),
                                    @ApiResponse(responseCode = "404", description = "Franquicia no encontrada"),
                                    @ApiResponse(responseCode = "400", description = "Error en datos")
                            },
                            parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = Franchise.class)))
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{id}/branches",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = BranchHandler.class,
                    beanMethod = "addBranchToFranchise",
                    operation = @Operation(
                            operationId = "addBranchToFranchise",
                            summary = "Agregar una sucursal a una franquicia",
                            description = "Crea una sucursal y la vincula a la franquicia indicada en el ID de la ruta",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Sucursal creada y vinculada exitosamente"
                                    ),
                                    @ApiResponse(responseCode = "404", description = "La franquicia padre no existe"),
                                    @ApiResponse(responseCode = "400", description = "Datos de sucursal inválidos o nombre duplicado")
                            },
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            description = "ID de la franquicia existente",
                                            in = ParameterIn.PATH,
                                            required = true
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Objeto sucursal a crear (solo requiere el nombre)",
                                    content = @Content(schema = @Schema(implementation = co.com.nequi.model.branch.Branch.class))
                            )
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{id}",
                    method = RequestMethod.PATCH,
                    beanClass = BranchHandler.class,
                    beanMethod = "updateBranch",
                    operation = @Operation(
                            operationId = "updateBranch",
                            summary = "Actualizar nombre de una sucursal",
                            parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Nombre actualizado"),
                                    @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{id}/products",
                    method = RequestMethod.POST,
                    beanClass = ProductHandler.class,
                    beanMethod = "addProduct",
                    operation = @Operation(
                            operationId = "addProduct",
                            summary = "Agregar producto a una sucursal",
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Producto creado"),
                                    @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{id}",
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateProduct",
                    operation = @Operation(
                            operationId = "updateProduct",
                            summary = "Actualizar nombre de un producto",
                            parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Nombre de producto actualizado"),
                                    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/products/{productId}",
                    method = RequestMethod.DELETE,
                    beanClass = ProductHandler.class,
                    beanMethod = "deleteProduct",
                    operation = @Operation(
                            operationId = "deleteProduct",
                            summary = "Eliminar un producto de una sucursal específica",
                            parameters = {
                                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true),
                                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Producto eliminado"),
                                    @ApiResponse(responseCode = "404", description = "No encontrado")
                            }
                    )
            )
    })

    public RouterFunction<ServerResponse> routerFunction(FranchiseHandler handler, BranchHandler branchHandler, ProductHandler productHandler) {
        return route(POST("/api/franchises"), handler::createFranchise)
                .andRoute(PATCH("/api/franchises/{id}"), handler::updateFranchise)
                .andRoute(POST("/api/franchises/{id}/branches"), branchHandler::addBranchToFranchise)
                .andRoute(PATCH("/api/branches/{id}"), branchHandler::updateBranch)
                .andRoute(POST("/api/branches/{id}/products"), productHandler::addProduct)
                .andRoute(PATCH("/api/products/{id}"), productHandler::updateProduct)
                .andRoute(DELETE("/api/branches/{branchId}/products/{productId}"), productHandler::deleteProduct);
    }

}
