package co.com.nequi.usecase.product;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.branch.gateways.BranchRepository;
import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.exceptions.ResourceNotFoundException;
import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.ProductMaxStock;
import co.com.nequi.model.product.gateways.ProductRepository;
import co.com.nequi.usecase.constants.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private ProductUseCase productUseCase;

    private Product product;
    private Branch branch;
    private Long branchId;

    @BeforeEach
    void setUp() {
        branchId = 1L;
        branch = Branch.builder()
                .id(branchId)
                .name("Sucursal Test")
                .franchiseId(1L)
                .build();

        product = Product.builder()
                .name("Producto Test")
                .stock(10L)
                .build();
    }

    @Test
    void addProductToBranch_Success() {
        Product savedProduct = product.toBuilder()
                .id(1L)
                .branchId(branchId)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findByNameAndBranchId(product.getName(), branchId))
                .thenReturn(Mono.empty());
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(savedProduct));

        StepVerifier.create(productUseCase.addProductToBranch(branchId, product))
                .expectNextMatches(p ->
                        p.getId().equals(1L) &&
                        p.getName().equals("Producto Test") &&
                        p.getStock().equals(10L) &&
                        p.getBranchId().equals(branchId)
                )
                .verifyComplete();

        verify(branchRepository).findById(branchId);
        verify(productRepository).findByNameAndBranchId(product.getName(), branchId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void addProductToBranch_WithNullName_ShouldReturnError() {
        product.setName(null);

        StepVerifier.create(productUseCase.addProductToBranch(branchId, product))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                        error.getMessage().equals(ErrorMessages.PRODUCT_NAME_REQUIRED)
                )
                .verify();

        verify(branchRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void addProductToBranch_WithEmptyName_ShouldReturnError() {
        product.setName("   ");

        StepVerifier.create(productUseCase.addProductToBranch(branchId, product))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                        error.getMessage().equals(ErrorMessages.PRODUCT_NAME_REQUIRED)
                )
                .verify();

        verify(branchRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void addProductToBranch_WithNullStock_ShouldReturnError() {
        product.setStock(null);

        StepVerifier.create(productUseCase.addProductToBranch(branchId, product))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                        error.getMessage().equals(ErrorMessages.PRODUCT_STOCK_INVALID)
                )
                .verify();

        verify(branchRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void addProductToBranch_WithNegativeStock_ShouldReturnError() {
        product.setStock(-5L);

        StepVerifier.create(productUseCase.addProductToBranch(branchId, product))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                        error.getMessage().equals(ErrorMessages.PRODUCT_STOCK_INVALID)
                )
                .verify();

        verify(branchRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void addProductToBranch_WithZeroStock_Success() {
        product.setStock(0L);
        Product savedProduct = product.toBuilder()
                .id(1L)
                .branchId(branchId)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findByNameAndBranchId(product.getName(), branchId))
                .thenReturn(Mono.empty());
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(savedProduct));

        StepVerifier.create(productUseCase.addProductToBranch(branchId, product))
                .expectNextMatches(p -> p.getStock().equals(0L))
                .verifyComplete();

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void addProductToBranch_BranchNotFound_ShouldReturnError() {
        when(branchRepository.findById(branchId)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.addProductToBranch(branchId, product))
                .expectErrorMatches(error ->
                        error instanceof ResourceNotFoundException &&
                        error.getMessage().equals(String.format(ErrorMessages.BRANCH_NOT_FOUND, branchId))
                )
                .verify();

        verify(branchRepository).findById(branchId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void addProductToBranch_ProductAlreadyExists_ShouldReturnError() {
        Product existingProduct = product.toBuilder()
                .id(2L)
                .branchId(branchId)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findByNameAndBranchId(product.getName(), branchId))
                .thenReturn(Mono.just(existingProduct));

        StepVerifier.create(productUseCase.addProductToBranch(branchId, product))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                        error.getMessage().equals(String.format(ErrorMessages.PRODUCT_NAME_ALREADY_EXISTS, "Producto Test"))
                )
                .verify();

        verify(branchRepository).findById(branchId);
        verify(productRepository).findByNameAndBranchId(product.getName(), branchId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_Success() {
        Long productId = 1L;
        Product existingProduct = Product.builder()
                .id(productId)
                .name("Nombre Antiguo")
                .stock(10L)
                .branchId(branchId)
                .build();

        Product updateInfo = Product.builder().name("Nombre Nuevo").build();
        Product savedProduct = existingProduct.toBuilder().name("Nombre Nuevo").build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(savedProduct));

        StepVerifier.create(productUseCase.updateProduct(productId, updateInfo))
                .expectNextMatches(p -> p.getName().equals("Nombre Nuevo") && p.getId().equals(productId))
                .verifyComplete();

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_WithEmptyName_ShouldReturnError() {
        Product updateInfo = Product.builder().name("   ").build();

        StepVerifier.create(productUseCase.updateProduct(1L, updateInfo))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                error.getMessage().equals(ErrorMessages.PRODUCT_NAME_REQUIRED)
                )
                .verify();

        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_NotFound_ShouldReturnError() {
        Long productId = 99L;
        Product updateInfo = Product.builder().name("Nuevo Nombre").build();

        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateProduct(productId, updateInfo))
                .expectErrorMatches(error ->
                        error instanceof ResourceNotFoundException &&
                                error.getMessage().equals(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId))
                )
                .verify();

        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_Success() {
        Long productId = 50L;
        Product existingProduct = Product.builder()
                .id(productId)
                .branchId(branchId)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepository.deleteByIdAndBranchId(productId, branchId)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.deleteProductFromBranch(branchId, productId))
                .verifyComplete();

        verify(branchRepository).findById(branchId);
        verify(productRepository).findById(productId);
        verify(productRepository).deleteByIdAndBranchId(productId, branchId);
    }

    @Test
    void deleteProduct_WrongBranch_ShouldReturnError() {
        Long otherBranchId = 99L;
        Long productId = 50L;

        Product productInOtherBranch = Product.builder()
                .id(productId)
                .branchId(otherBranchId)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findById(productId)).thenReturn(Mono.just(productInOtherBranch));

        StepVerifier.create(productUseCase.deleteProductFromBranch(branchId, productId))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                error.getMessage().equals(ErrorMessages.PRODUCT_DOES_NOT_BELONG_TO_BRANCH)
                )
                .verify();

        verify(productRepository).findById(productId);
        verify(productRepository, never()).deleteByIdAndBranchId(anyLong(), anyLong());
    }

    @Test
    void deleteProduct_NotFound_ShouldReturnError() {
        Long productId = 88L;

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.deleteProductFromBranch(branchId, productId))
                .expectErrorMatches(error ->
                        error instanceof ResourceNotFoundException &&
                                error.getMessage().equals(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId))
                )
                .verify();

        verify(productRepository, never()).deleteByIdAndBranchId(anyLong(), anyLong());
    }

    @Test
    void updateProductStock_Success() {
        Long productId = 50L;
        Long newStock = 20L;
        Product existingProduct = Product.builder()
                .id(productId)
                .name("Producto Existente")
                .stock(10L)
                .branchId(branchId)
                .build();

        Product updateInfo = Product.builder().stock(newStock).build();
        Product savedProduct = existingProduct.toBuilder().stock(newStock).build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(savedProduct));

        StepVerifier.create(productUseCase.updateProductStock(branchId, productId, updateInfo))
                .expectNextMatches(p -> p.getStock().equals(newStock) && p.getId().equals(productId))
                .verifyComplete();

        verify(branchRepository).findById(branchId);
        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProductStock_NegativeStock_ShouldReturnError() {
        Product updateInfo = Product.builder().stock(-10L).build();

        StepVerifier.create(productUseCase.updateProductStock(branchId, 50L, updateInfo))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                error.getMessage().equals(ErrorMessages.PRODUCT_STOCK_INVALID)
                )
                .verify();

        verify(productRepository, never()).findById(anyLong());
    }

    @Test
    void updateProductStock_BranchNotFound_ShouldReturnError() {
        Long productId = 50L;
        Product updateInfo = Product.builder().stock(20L).build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateProductStock(branchId, productId, updateInfo))
                .expectErrorMatches(error ->
                        error instanceof ResourceNotFoundException &&
                                error.getMessage().equals(String.format(ErrorMessages.BRANCH_NOT_FOUND, branchId))
                )
                .verify();

        verify(branchRepository).findById(branchId);
        verify(productRepository, never()).findById(anyLong());
    }

    @Test
    void updateProductStock_ProductNotFound_ShouldReturnError() {
        Long productId = 50L;
        Product updateInfo = Product.builder().stock(20L).build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateProductStock(branchId, productId, updateInfo))
                .expectErrorMatches(error ->
                        error instanceof ResourceNotFoundException &&
                                error.getMessage().equals(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId))
                )
                .verify();

        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProductStock_WrongBranch_ShouldReturnError() {
        Long productId = 50L;
        Long otherBranchId = 99L;
        Product productInOtherBranch = Product.builder()
                .id(productId)
                .branchId(otherBranchId)
                .stock(10L)
                .build();

        Product updateInfo = Product.builder().stock(20L).build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findById(productId)).thenReturn(Mono.just(productInOtherBranch));

        StepVerifier.create(productUseCase.updateProductStock(branchId, productId, updateInfo))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                error.getMessage().equals(ErrorMessages.PRODUCT_DOES_NOT_BELONG_TO_BRANCH)
                )
                .verify();

        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getMaxStockProductsByFranchise_Success() {
        Long franchiseId = 1L;

        Branch b1 = Branch.builder().id(10L).name("Sucursal Norte").build();
        Branch b2 = Branch.builder().id(20L).name("Sucursal Sur").build();

        Product p1 = Product.builder().name("Producto A").stock(5L).branchId(10L).build();
        Product p2 = Product.builder().name("Producto B").stock(100L).branchId(10L).build(); // Máximo Norte

        Product p3 = Product.builder().name("Producto C").stock(50L).branchId(20L).build(); // Máximo Sur
        Product p4 = Product.builder().name("Producto D").stock(10L).branchId(20L).build();

        // Mocks
        when(branchRepository.findByFranchiseId(franchiseId)).thenReturn(Flux.just(b1, b2));
        when(productRepository.findByFranchiseId(franchiseId)).thenReturn(Flux.just(p1, p2, p3, p4));

        StepVerifier.create(productUseCase.getMaxStockProductsByFranchise(franchiseId))
                .recordWith(java.util.ArrayList::new)
                .expectNextCount(2) // Esperamos 2 resultados (uno por sucursal)
                .consumeRecordedWith(results -> {
                    ProductMaxStock resNorte = results.stream()
                            .filter(r -> r.getBranchName().equals("Sucursal Norte"))
                            .findFirst().orElseThrow();
                    assert resNorte.getProductName().equals("Producto B");
                    assert resNorte.getStock().equals(100L);

                    ProductMaxStock resSur = results.stream()
                            .filter(r -> r.getBranchName().equals("Sucursal Sur"))
                            .findFirst().orElseThrow();
                    assert resSur.getProductName().equals("Producto C");
                    assert resSur.getStock().equals(50L);
                })
                .verifyComplete();

        verify(branchRepository).findByFranchiseId(franchiseId);
        verify(productRepository).findByFranchiseId(franchiseId);
    }

    @Test
    void getMaxStockProductsByFranchise_NoBranches_ShouldReturnEmpty() {
        Long franchiseId = 1L;
        when(branchRepository.findByFranchiseId(franchiseId)).thenReturn(Flux.empty());

        StepVerifier.create(productUseCase.getMaxStockProductsByFranchise(franchiseId))
                .expectNextCount(0)
                .verifyComplete();

        verify(branchRepository).findByFranchiseId(franchiseId);
        verify(productRepository, never()).findByFranchiseId(anyLong());
    }

    @Test
    void getMaxStockProductsByFranchise_BranchesWithoutProducts_ShouldReturnEmpty() {
        Long franchiseId = 1L;
        Branch b1 = Branch.builder().id(10L).name("Sucursal Norte").build();

        when(branchRepository.findByFranchiseId(franchiseId)).thenReturn(Flux.just(b1));
        when(productRepository.findByFranchiseId(franchiseId)).thenReturn(Flux.empty());

        StepVerifier.create(productUseCase.getMaxStockProductsByFranchise(franchiseId))
                .expectNextCount(0)
                .verifyComplete();
    }
}
