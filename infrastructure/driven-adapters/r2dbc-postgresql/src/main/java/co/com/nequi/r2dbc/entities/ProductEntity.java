package co.com.nequi.r2dbc.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("products")
public class ProductEntity {
    @Id
    private Long id;
    private String name;
    private Long stock;
    @Column("branch_id")
    private Long branchId;
}
