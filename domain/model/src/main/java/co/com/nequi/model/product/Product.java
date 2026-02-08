package co.com.nequi.model.product;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Product {
    private Long id;
    private String name;
    private Long stock;
    private Long branchId;

}
