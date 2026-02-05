package co.com.nequi.model.product;

import lombok.Builder;
import lombok.Value;


@Value
@Builder(toBuilder = true)
public class Product {
    Long id;
    String name;
    Long stock;
}
