package co.com.nequi.r2dbc.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("franchises")
public class FranchiseEntity {
    @Id
    private Long id;
    private String name;
}
