package co.com.nequi.r2dbc.franchise;

import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.r2dbc.entities.FranchiseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseRepositoryAdapterTest {

    @Mock
    private FranchiseReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private FranchiseRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FranchiseRepositoryAdapter(repository, mapper);
    }

    @Test
    void save_ShouldSaveFranchise() {
        Franchise franchise = Franchise.builder()
                .name("Test Franchise")
                .build();

        FranchiseEntity entity = new FranchiseEntity();
        entity.setName("Test Franchise");

        FranchiseEntity savedEntity = new FranchiseEntity();
        savedEntity.setId(1L);
        savedEntity.setName("Test Franchise");

        Franchise savedFranchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        when(mapper.map(franchise, FranchiseEntity.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(savedEntity));
        when(mapper.map(savedEntity, Franchise.class)).thenReturn(savedFranchise);

        StepVerifier.create(adapter.save(franchise))
                .expectNext(savedFranchise)
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnFranchise() {
        FranchiseEntity entity = new FranchiseEntity();
        entity.setId(1L);
        entity.setName("Test Franchise");

        Franchise franchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        when(repository.findById(1L)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Franchise.class)).thenReturn(franchise);

        StepVerifier.create(adapter.findById(1L))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void findById_NotFound_ShouldReturnEmpty() {
        when(repository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(99L))
                .verifyComplete();
    }

    @Test
    void findByName_ShouldReturnFranchise() {
        FranchiseEntity entity = new FranchiseEntity();
        entity.setId(1L);
        entity.setName("Test Franchise");

        Franchise franchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        when(repository.findByName("Test Franchise")).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Franchise.class)).thenReturn(franchise);

        StepVerifier.create(adapter.findByName("Test Franchise"))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void findByName_NotFound_ShouldReturnEmpty() {
        when(repository.findByName("NonExistent")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByName("NonExistent"))
                .verifyComplete();
    }
}
