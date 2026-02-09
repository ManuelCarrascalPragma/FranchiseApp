package co.com.nequi.api.config;

import co.com.nequi.api.BranchHandler;
import co.com.nequi.api.FranchiseHandler;
import co.com.nequi.api.ProductHandler;
import co.com.nequi.api.router.BranchRouter;
import co.com.nequi.api.router.FranchiseRouter;
import co.com.nequi.api.router.ProductRouter;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.usecase.branch.BranchUseCase;
import co.com.nequi.usecase.franchise.FranchiseUseCase;
import co.com.nequi.usecase.product.ProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigTest {

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
        
        FranchiseRouter franchiseRouter = new FranchiseRouter();
        BranchRouter branchRouter = new BranchRouter();
        ProductRouter productRouter = new ProductRouter();
        
        RouterFunction<ServerResponse> routerFunction = franchiseRouter.franchiseRoutes(franchiseHandler)
                .and(branchRouter.branchRoutes(branchHandler))
                .and(productRouter.productRoutes(productHandler));
        
        SecurityHeadersConfig securityHeadersConfig = new SecurityHeadersConfig();
        
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction)
                .webFilter(securityHeadersConfig)
                .build();
    }

    @Test
    void securityHeadersShouldBePresent() {
        Franchise franchise = Franchise.builder()
                .id(1L)
                .name("Test")
                .build();

        when(franchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(franchise));

        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(franchise)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}
