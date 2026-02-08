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
@Table("branches")
public class BranchEntity {
    @Id
    private Long id;
    private String name;

    @Column("franchise_id")
    private Long franchiseId;
}
