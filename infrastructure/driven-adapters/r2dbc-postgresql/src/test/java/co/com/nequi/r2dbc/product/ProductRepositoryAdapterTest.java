package co.com.nequi.r2dbc.product;

import co.com.nequi.model.product.Product;
import co.com.nequi.r2dbc.entities.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    @Mock
    private ProductReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private ProductRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProductRepositoryAdapter(repository, mapper);
    }

    @Test
    void save_ShouldSaveProduct() {
        Product product = Product.builder()
                .name("Test Product")
                .stock(10L)
                .branchId(1L)
                .build();

        ProductEntity entity = new ProductEntity();
        entity.setName("Test Product");
        entity.setStock(10L);
        entity.setBranchId(1L);

        ProductEntity savedEntity = new ProductEntity();
        savedEntity.setId(1L);
        savedEntity.setName("Test Product");
        savedEntity.setStock(10L);
        savedEntity.setBranchId(1L);

        Product savedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(10L)
                .branchId(1L)
                .build();

        when(mapper.map(product, ProductEntity.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(savedEntity));
        when(mapper.map(savedEntity, Product.class)).thenReturn(savedProduct);

        StepVerifier.create(adapter.save(product))
                .expectNext(savedProduct)
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnProduct() {
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Test Product");
        entity.setStock(10L);
        entity.setBranchId(1L);

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(10L)
                .branchId(1L)
                .build();

        when(repository.findById(1L)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Product.class)).thenReturn(product);

        StepVerifier.create(adapter.findById(1L))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void findByNameAndBranchId_ShouldReturnProduct() {
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Test Product");
        entity.setStock(10L);
        entity.setBranchId(1L);

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(10L)
                .branchId(1L)
                .build();

        when(repository.findByNameAndBranchId("Test Product", 1L)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Product.class)).thenReturn(product);

        StepVerifier.create(adapter.findByNameAndBranchId("Test Product", 1L))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void findByNameAndBranchId_NotFound_ShouldReturnEmpty() {
        when(repository.findByNameAndBranchId("NonExistent", 1L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByNameAndBranchId("NonExistent", 1L))
                .verifyComplete();
    }

    @Test
    void deleteByIdAndBranchId_ShouldDeleteProduct() {
        when(repository.deleteByIdAndBranchId(1L, 1L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteByIdAndBranchId(1L, 1L))
                .verifyComplete();
    }

    @Test
    void findByFranchiseId_ShouldReturnProducts() {
        ProductEntity entity1 = new ProductEntity();
        entity1.setId(1L);
        entity1.setName("Product 1");
        entity1.setStock(10L);
        entity1.setBranchId(1L);

        ProductEntity entity2 = new ProductEntity();
        entity2.setId(2L);
        entity2.setName("Product 2");
        entity2.setStock(20L);
        entity2.setBranchId(2L);

        Product product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .stock(10L)
                .branchId(1L)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .stock(20L)
                .branchId(2L)
                .build();

        when(repository.findByFranchiseId(1L)).thenReturn(Flux.just(entity1, entity2));
        when(mapper.map(entity1, Product.class)).thenReturn(product1);
        when(mapper.map(entity2, Product.class)).thenReturn(product2);

        StepVerifier.create(adapter.findByFranchiseId(1L))
                .expectNext(product1)
                .expectNext(product2)
                .verifyComplete();
    }

    @Test
    void findByFranchiseId_NoResults_ShouldReturnEmpty() {
        when(repository.findByFranchiseId(99L)).thenReturn(Flux.empty());

        StepVerifier.create(adapter.findByFranchiseId(99L))
                .verifyComplete();
    }
}
