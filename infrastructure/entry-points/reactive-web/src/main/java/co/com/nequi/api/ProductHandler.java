package co.com.nequi.api;

import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.ProductMaxStock;
import co.com.nequi.usecase.product.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import co.com.nequi.api.constants.*;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductHandler {
    private final ProductUseCase productUseCase;

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        Long branchId = Long.valueOf(request.pathVariable(ApiConstants.ID_PARAM_NAME));
        return request.bodyToMono(Product.class)
                .flatMap(product -> productUseCase.addProductToBranch(branchId, product))
                .flatMap(savedProduct -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedProduct));
    }

    public Mono<ServerResponse> updateProduct(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable(ApiConstants.ID_PARAM_NAME));
        return request.bodyToMono(Product.class)
                .flatMap(product -> productUseCase.updateProduct(id, product))
                .flatMap(updateProduct-> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updateProduct));
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        return Mono.defer(() -> {
                    Long branchId = Long.valueOf(request.pathVariable(ApiConstants.BRANCH_ID_PARAM_NAME));
                    Long productId = Long.valueOf(request.pathVariable(ApiConstants.PRODUCT_ID_PARAM_NAME));
                    return productUseCase.deleteProductFromBranch(branchId, productId);
                })
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateStock(ServerRequest request) {
        return Mono.defer(() -> {
            Long branchId = Long.valueOf(request.pathVariable(ApiConstants.BRANCH_ID_PARAM_NAME));
            Long productId = Long.valueOf(request.pathVariable(ApiConstants.PRODUCT_ID_PARAM_NAME));

            return request.bodyToMono(Product.class)
                    .flatMap(product -> productUseCase.updateProductStock(branchId, productId, product))
                    .flatMap(updatedProduct -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(updatedProduct));
        });
    }

    public Mono<ServerResponse> getMaxStockReport(ServerRequest request) {
        Long franchiseId = Long.valueOf(request.pathVariable(ApiConstants.FRANCHISE_ID_PARAM_NAME));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productUseCase.getMaxStockProductsByFranchise(franchiseId), ProductMaxStock.class);
    }
}
