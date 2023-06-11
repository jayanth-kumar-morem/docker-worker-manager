package ai.openfabric.api.dtos;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class ContainersListRequestDTO {
    @Min(value = 1, message = "pageSize must be greater than or equal to 1")
    private int pageLimit;

    @Min(value = 0, message = "pageNumber must be greater than or equal to 0")
    private int pageIndex;

    private FilterOptions filterOptions;
}
