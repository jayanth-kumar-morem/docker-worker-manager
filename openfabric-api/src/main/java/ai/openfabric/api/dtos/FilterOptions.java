package ai.openfabric.api.dtos;

import ai.openfabric.api.enums.ContainerState;
import lombok.Data;

@Data
public class FilterOptions {
    ContainerState containerState;
    Long createdTimestamp;
    String idFilter;
}