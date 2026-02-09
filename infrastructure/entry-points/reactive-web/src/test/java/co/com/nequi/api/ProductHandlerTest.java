package co.com.nequi.api;

import co.com.nequi.usecase.constants.ErrorMessages;
import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.exceptions.ResourceNotFoundException;
import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.ProductMaxStock;
import co.com.nequi.usecase.product.ProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductHandlerTest {

    @Mock
    private ProductUseCase productUseCase;

    private ProductHandler productHandler;

    @BeforeEach
    void setUp() {
        productHandler = new ProductHandler(productUseCase);
    }

    @Test
    void addProduct_Success() {
        Product product = Product.builder()
                .name("Test Product")
                .stock(10L)
                .build();

        Product savedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(10L)
                .branchId(1L)
                .build();

        when(productUseCase.addProductToBranch(eq(1L), any(Product.class)))
                .thenReturn(Mono.just(savedProduct));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .body(Mono.just(product));

        Mono<ServerResponse> response = productHandler.addProduct(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.CREATED)
                .verifyComplete();
    }

    @Test
    void addProduct_BranchNotFound_ShouldPropagateError() {
        Product product = Product.builder()
                .name("Test Product")
                .stock(10L)
                .build();

        when(productUseCase.addProductToBranch(eq(99L), any(Product.class)))
                .thenReturn(Mono.error(new ResourceNotFoundException(
                        String.format(ErrorMessages.BRANCH_NOT_FOUND, 99L))));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "99")
                .body(Mono.just(product));

        Mono<ServerResponse> response = productHandler.addProduct(request);

        StepVerifier.create(response)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void updateProduct_Success() {
        Product updateData = Product.builder()
                .name("Updated Product")
                .build();

        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated Product")
                .stock(10L)
                .branchId(1L)
                .build();

        when(productUseCase.updateProduct(eq(1L), any(Product.class)))
                .thenReturn(Mono.just(updatedProduct));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .body(Mono.just(updateData));

        Mono<ServerResponse> response = productHandler.updateProduct(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void updateProduct_NotFound_ShouldPropagateError() {
        Product updateData = Product.builder()
                .name("Updated Product")
                .build();

        when(productUseCase.updateProduct(eq(99L), any(Product.class)))
                .thenReturn(Mono.error(new ResourceNotFoundException(
                        String.format(ErrorMessages.PRODUCT_NOT_FOUND, 99L))));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "99")
                .body(Mono.just(updateData));

        Mono<ServerResponse> response = productHandler.updateProduct(request);

        StepVerifier.create(response)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void deleteProduct_Success() {
        when(productUseCase.deleteProductFromBranch(eq(1L), eq(2L)))
                .thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("branchId", "1")
                .pathVariable("productId", "2")
                .build();

        Mono<ServerResponse> response = productHandler.deleteProduct(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.NO_CONTENT)
                .verifyComplete();
    }

    @Test
    void deleteProduct_NotFound_ShouldPropagateError() {
        when(productUseCase.deleteProductFromBranch(eq(1L), eq(99L)))
                .thenReturn(Mono.error(new ResourceNotFoundException(
                        String.format(ErrorMessages.PRODUCT_NOT_FOUND, 99L))));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("branchId", "1")
                .pathVariable("productId", "99")
                .build();

        Mono<ServerResponse> response = productHandler.deleteProduct(request);

        StepVerifier.create(response)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void updateStock_Success() {
        Product stockUpdate = Product.builder()
                .stock(50L)
                .build();

        Product updatedProduct = Product.builder()
                .id(2L)
                .name("Test Product")
                .stock(50L)
                .branchId(1L)
                .build();

        when(productUseCase.updateProductStock(eq(1L), eq(2L), any(Product.class)))
                .thenReturn(Mono.just(updatedProduct));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("branchId", "1")
                .pathVariable("productId", "2")
                .body(Mono.just(stockUpdate));

        Mono<ServerResponse> response = productHandler.updateStock(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void updateStock_InvalidStock_ShouldPropagateError() {
        Product stockUpdate = Product.builder()
                .stock(-10L)
                .build();

        when(productUseCase.updateProductStock(eq(1L), eq(2L), any(Product.class)))
                .thenReturn(Mono.error(new BusinessException(ErrorMessages.PRODUCT_STOCK_INVALID)));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("branchId", "1")
                .pathVariable("productId", "2")
                .body(Mono.just(stockUpdate));

        Mono<ServerResponse> response = productHandler.updateStock(request);

        StepVerifier.create(response)
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void getMaxStockReport_Success() {
        ProductMaxStock report1 = ProductMaxStock.builder()
                .branchName("Branch 1")
                .productName("Product A")
                .stock(100L)
                .build();

        ProductMaxStock report2 = ProductMaxStock.builder()
                .branchName("Branch 2")
                .productName("Product B")
                .stock(200L)
                .build();

        when(productUseCase.getMaxStockProductsByFranchise(eq(1L)))
                .thenReturn(Flux.just(report1, report2));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "1")
                .build();

        Mono<ServerResponse> response = productHandler.getMaxStockReport(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void getMaxStockReport_EmptyResult() {
        when(productUseCase.getMaxStockProductsByFranchise(eq(1L)))
                .thenReturn(Flux.empty());

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "1")
                .build();

        Mono<ServerResponse> response = productHandler.getMaxStockReport(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.OK)
                .verifyComplete();
    }
}
