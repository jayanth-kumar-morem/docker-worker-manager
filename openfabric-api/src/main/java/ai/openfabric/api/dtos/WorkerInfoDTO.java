package ai.openfabric.api.dtos;

import ai.openfabric.api.enums.ContainerState;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class WorkerInfoDTO {
    private String id;
    private String name;
    private Long created;
    private String image;
    private ContainerState state;
    private Long portNumber;
}