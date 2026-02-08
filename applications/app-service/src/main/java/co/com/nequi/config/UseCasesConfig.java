package co.com.nequi.config;

import co.com.nequi.model.branch.gateways.BranchRepository;
import co.com.nequi.model.franchise.gateways.FranchiseRepository;
import co.com.nequi.model.product.gateways.ProductRepository;
import co.com.nequi.usecase.branch.BranchUseCase;
import co.com.nequi.usecase.franchise.FranchiseUseCase;
import co.com.nequi.usecase.product.ProductUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.nequi.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public FranchiseUseCase franchiseUseCase(FranchiseRepository franchiseRepository) {
        return new FranchiseUseCase(franchiseRepository);
    }

    @Bean
    public BranchUseCase branchUseCase(BranchRepository branchRepository, FranchiseRepository franchiseRepository) {
        return new BranchUseCase(branchRepository, franchiseRepository);
    }

    @Bean
    public ProductUseCase productUseCase(ProductRepository productRepository, BranchRepository branchRepository) {
        return new ProductUseCase(productRepository, branchRepository);
    }

}
