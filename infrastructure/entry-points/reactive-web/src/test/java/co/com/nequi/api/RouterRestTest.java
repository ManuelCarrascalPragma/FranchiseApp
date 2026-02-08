package co.com.nequi.api;

import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.usecase.branch.BranchUseCase;
import co.com.nequi.usecase.franchise.FranchiseUseCase;
import co.com.nequi.usecase.product.ProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    private WebTestClient webTestClient;

    @Mock
    private FranchiseUseCase franchiseUseCase;

    @Mock
    private BranchUseCase branchUseCase;

    @Mock
    private ProductUseCase productUseCase;

    @BeforeEach
    void setUp() {
        FranchiseHandler franchiseHandler = new FranchiseHandler(franchiseUseCase);
        BranchHandler branchHandler = new BranchHandler(branchUseCase);
        ProductHandler productHandler = new ProductHandler(productUseCase);
        RouterRest routerRest = new RouterRest();
        webTestClient = WebTestClient.bindToRouterFunction(routerRest.routerFunction(franchiseHandler, branchHandler, productHandler)).build();
    }

    @Test
    void testCreateFranchise() {
        Franchise franchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        when(franchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(franchise));

        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(franchise)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Franchise.class)
                .isEqualTo(franchise);
    }

    @Test
    void testUpdateFranchise() {
        Franchise updatedFranchise = new Franchise(1L, "Nombre Nuevo");

        Mockito.when(franchiseUseCase.updateFranchise(Mockito.eq(1L), Mockito.any(Franchise.class)))
                .thenReturn(Mono.just(updatedFranchise));

        webTestClient.patch()
                .uri("/api/franchises/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedFranchise)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Franchise.class)
                .consumeWith(response -> {
                    Franchise f = response.getResponseBody();
                    assert f != null;
                    assert f.getName().equals("Nombre Nuevo");
                });
    }
}
