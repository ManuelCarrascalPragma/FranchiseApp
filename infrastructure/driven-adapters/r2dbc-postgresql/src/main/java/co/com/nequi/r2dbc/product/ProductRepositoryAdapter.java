package co.com.nequi.r2dbc.product;

import co.com.nequi.model.product.Product;
import co.com.nequi.model.product.gateways.ProductRepository;
import co.com.nequi.r2dbc.entities.ProductEntity;
import co.com.nequi.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryAdapter extends ReactiveAdapterOperations<Product, ProductEntity, Long, ProductReactiveRepository> implements ProductRepository
{
    public ProductRepositoryAdapter(ProductReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Product.class));
    }

}
