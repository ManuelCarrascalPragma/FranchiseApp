package co.com.nequi.model.franchise;

import co.com.nequi.model.branch.Branch;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class Franchise {
    Long id;
    String name;
    List<Branch> branches;

}
