package co.com.nequi.api;

import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.usecase.franchise.FranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "cors.allowed-origins=http://localhost:8080"
})
class FranchiseIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @MockitoBean
    private FranchiseUseCase franchiseUseCase;

    private WebTestClient getWebTestClient() {
        return WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void testCreateFranchiseSuccess() {
        Franchise franchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        when(franchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(franchise));

        getWebTestClient().post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(franchise)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Franchise.class)
                .isEqualTo(franchise);
    }

    @Test
    void testCreateFranchiseWithEmptyNameShouldReturnBadRequest() {
        Franchise franchise = Franchise.builder()
                .id(1L)
                .name("")
                .build();

        when(franchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.error(new BusinessException("Franchise name is empty")));

        getWebTestClient().post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(franchise)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Franchise name is empty")
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request");
    }

    @Test
    void testCreateFranchiseWithNullNameShouldReturnBadRequest() {
        Franchise franchise = Franchise.builder()
                .id(1L)
                .build();

        when(franchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.error(new BusinessException("Franchise name is empty")));

        getWebTestClient().post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(franchise)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Franchise name is empty")
                .jsonPath("$.status").isEqualTo(400);
    }
}
