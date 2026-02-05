package co.com.nequi.model.branch;
import co.com.nequi.model.product.Product;
import lombok.*;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class Branch {
    Long id;
    String name;
    List<Product> products;
}
