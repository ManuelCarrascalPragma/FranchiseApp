package co.com.nequi.api.config;

import co.com.nequi.api.FranchiseHandler;
import co.com.nequi.api.RouterRest;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.usecase.franchise.FranchiseUseCase;
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

    @BeforeEach
    void setUp() {
        FranchiseHandler handler = new FranchiseHandler(franchiseUseCase);
        RouterRest routerRest = new RouterRest();
        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(handler);
        
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
