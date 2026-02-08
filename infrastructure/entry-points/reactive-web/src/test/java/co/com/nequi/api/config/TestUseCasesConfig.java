package co.com.nequi.api.config;

import co.com.nequi.model.branch.gateways.BranchRepository;
import co.com.nequi.model.franchise.gateways.FranchiseRepository;
import co.com.nequi.usecase.branch.BranchUseCase;
import co.com.nequi.usecase.franchise.FranchiseUseCase;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestUseCasesConfig {

    @Bean
    public BranchRepository branchRepository() {
        return Mockito.mock(BranchRepository.class);
    }

    @Bean
    public FranchiseRepository franchiseRepository() {
        return Mockito.mock(FranchiseRepository.class);
    }

    @Bean
    public FranchiseUseCase franchiseUseCase(FranchiseRepository franchiseRepository) {
        return new FranchiseUseCase(franchiseRepository);
    }

    @Bean
    public BranchUseCase branchUseCase(BranchRepository branchRepository, FranchiseRepository franchiseRepository) {
        return new BranchUseCase(branchRepository, franchiseRepository);
    }
}
