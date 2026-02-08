package co.com.nequi.usecase.product;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.branch.gateways.BranchRepository;
import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.exceptions.ResourceNotFoundException;
import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.gateways.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
                        error.getMessage().equals("El nombre del producto es obligatorio")
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
                        error.getMessage().equals("El nombre del producto es obligatorio")
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
                        error.getMessage().equals("El stock debe ser un número mayor o igual a cero")
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
                        error.getMessage().equals("El stock debe ser un número mayor o igual a cero")
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
                        error.getMessage().equals("No se encontró la sucursal con id: " + branchId)
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
                        error.getMessage().equals("El producto 'Producto Test' ya existe en esta sucursal")
                )
                .verify();

        verify(branchRepository).findById(branchId);
        verify(productRepository).findByNameAndBranchId(product.getName(), branchId);
        verify(productRepository, never()).save(any(Product.class));
    }
}
