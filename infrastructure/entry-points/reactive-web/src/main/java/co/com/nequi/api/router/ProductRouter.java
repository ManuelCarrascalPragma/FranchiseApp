package co.com.nequi.api.router;

import co.com.nequi.api.ProductHandler;
import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.ProductMaxStock;
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
public class ProductRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/branches/{id}/products",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = ProductHandler.class,
                    beanMethod = "addProduct",
                    operation = @Operation(
                            operationId = "addProduct",
                            summary = "Add a product to a branch",
                            description = "Creates a new product and adds it to the specified branch",
                            parameters = @Parameter(
                                    name = "id",
                                    description = "Branch ID",
                                    in = ParameterIn.PATH,
                                    required = true
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Product created successfully",
                                            content = @Content(schema = @Schema(implementation = Product.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Branch not found"
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid product data"
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Product object to create",
                                    content = @Content(schema = @Schema(implementation = Product.class))
                            )
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{id}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateProduct",
                    operation = @Operation(
                            operationId = "updateProduct",
                            summary = "Update product name",
                            description = "Updates the name of an existing product",
                            parameters = @Parameter(
                                    name = "id",
                                    description = "Product ID",
                                    in = ParameterIn.PATH,
                                    required = true
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Product name updated successfully",
                                            content = @Content(schema = @Schema(implementation = Product.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Product not found"
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid data"
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Product object with updated name",
                                    content = @Content(schema = @Schema(implementation = Product.class))
                            )
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/products/{productId}",
                    method = RequestMethod.DELETE,
                    beanClass = ProductHandler.class,
                    beanMethod = "deleteProduct",
                    operation = @Operation(
                            operationId = "deleteProduct",
                            summary = "Delete a product from a branch",
                            description = "Removes a product from a specific branch",
                            parameters = {
                                    @Parameter(
                                            name = "branchId",
                                            description = "Branch ID",
                                            in = ParameterIn.PATH,
                                            required = true
                                    ),
                                    @Parameter(
                                            name = "productId",
                                            description = "Product ID",
                                            in = ParameterIn.PATH,
                                            required = true
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "204",
                                            description = "Product deleted successfully"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Branch or product not found"
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Product does not belong to the specified branch"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/products/{productId}/stock",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateStock",
                    operation = @Operation(
                            operationId = "updateStock",
                            summary = "Update product stock",
                            description = "Modifies the stock quantity of a product in a specific branch",
                            parameters = {
                                    @Parameter(
                                            name = "branchId",
                                            description = "Branch ID",
                                            in = ParameterIn.PATH,
                                            required = true
                                    ),
                                    @Parameter(
                                            name = "productId",
                                            description = "Product ID",
                                            in = ParameterIn.PATH,
                                            required = true
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Stock updated successfully",
                                            content = @Content(schema = @Schema(implementation = Product.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid stock value or product does not belong to branch"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Branch or product not found"
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Product object with new stock value",
                                    content = @Content(schema = @Schema(implementation = Product.class))
                            )
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/max-stock",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = ProductHandler.class,
                    beanMethod = "getMaxStockReport",
                    operation = @Operation(
                            operationId = "getMaxStockReport",
                            summary = "Get products with maximum stock per branch",
                            description = "Returns a list with the product that has the highest stock in each branch of a specific franchise",
                            parameters = @Parameter(
                                    name = "franchiseId",
                                    description = "Franchise ID",
                                    in = ParameterIn.PATH,
                                    required = true
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Report generated successfully",
                                            content = @Content(schema = @Schema(implementation = ProductMaxStock.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Franchise not found"
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> productRoutes(ProductHandler handler) {
        return route(POST("/api/branches/{id}/products"), handler::addProduct)
                .andRoute(PATCH("/api/products/{id}"), handler::updateProduct)
                .andRoute(DELETE("/api/branches/{branchId}/products/{productId}"), handler::deleteProduct)
                .andRoute(PATCH("/api/branches/{branchId}/products/{productId}/stock"), handler::updateStock)
                .andRoute(GET("/api/franchises/{franchiseId}/max-stock"), handler::getMaxStockReport);
    }
}
