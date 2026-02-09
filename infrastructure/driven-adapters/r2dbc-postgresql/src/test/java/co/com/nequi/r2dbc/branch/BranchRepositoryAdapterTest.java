package co.com.nequi.r2dbc.branch;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.r2dbc.entities.BranchEntity;
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
class BranchRepositoryAdapterTest {

    @Mock
    private BranchReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private BranchRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BranchRepositoryAdapter(repository, mapper);
    }

    @Test
    void save_ShouldSaveBranch() {
        Branch branch = Branch.builder()
                .name("Test Branch")
                .franchiseId(1L)
                .build();

        BranchEntity entity = new BranchEntity();
        entity.setName("Test Branch");
        entity.setFranchiseId(1L);

        BranchEntity savedEntity = new BranchEntity();
        savedEntity.setId(1L);
        savedEntity.setName("Test Branch");
        savedEntity.setFranchiseId(1L);

        Branch savedBranch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .build();

        when(mapper.map(branch, BranchEntity.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(savedEntity));
        when(mapper.map(savedEntity, Branch.class)).thenReturn(savedBranch);

        StepVerifier.create(adapter.save(branch))
                .expectNext(savedBranch)
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnBranch() {
        BranchEntity entity = new BranchEntity();
        entity.setId(1L);
        entity.setName("Test Branch");
        entity.setFranchiseId(1L);

        Branch branch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .build();

        when(repository.findById(1L)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Branch.class)).thenReturn(branch);

        StepVerifier.create(adapter.findById(1L))
                .expectNext(branch)
                .verifyComplete();
    }

    @Test
    void findByNameAndFranchiseId_ShouldReturnBranch() {
        BranchEntity entity = new BranchEntity();
        entity.setId(1L);
        entity.setName("Test Branch");
        entity.setFranchiseId(1L);

        Branch branch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .build();

        when(repository.findByNameAndFranchiseId("Test Branch", 1L)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Branch.class)).thenReturn(branch);

        StepVerifier.create(adapter.findByNameAndFranchiseId("Test Branch", 1L))
                .expectNext(branch)
                .verifyComplete();
    }

    @Test
    void findByNameAndFranchiseId_NotFound_ShouldReturnEmpty() {
        when(repository.findByNameAndFranchiseId("NonExistent", 1L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByNameAndFranchiseId("NonExistent", 1L))
                .verifyComplete();
    }

    @Test
    void findByFranchiseId_ShouldReturnBranches() {
        BranchEntity entity1 = new BranchEntity();
        entity1.setId(1L);
        entity1.setName("Branch 1");
        entity1.setFranchiseId(1L);

        BranchEntity entity2 = new BranchEntity();
        entity2.setId(2L);
        entity2.setName("Branch 2");
        entity2.setFranchiseId(1L);

        Branch branch1 = Branch.builder()
                .id(1L)
                .name("Branch 1")
                .franchiseId(1L)
                .build();

        Branch branch2 = Branch.builder()
                .id(2L)
                .name("Branch 2")
                .franchiseId(1L)
                .build();

        when(repository.findByFranchiseId(1L)).thenReturn(Flux.just(entity1, entity2));
        when(mapper.map(entity1, Branch.class)).thenReturn(branch1);
        when(mapper.map(entity2, Branch.class)).thenReturn(branch2);

        StepVerifier.create(adapter.findByFranchiseId(1L))
                .expectNext(branch1)
                .expectNext(branch2)
                .verifyComplete();
    }

    @Test
    void findByFranchiseId_NoResults_ShouldReturnEmpty() {
        when(repository.findByFranchiseId(99L)).thenReturn(Flux.empty());

        StepVerifier.create(adapter.findByFranchiseId(99L))
                .verifyComplete();
    }
}
