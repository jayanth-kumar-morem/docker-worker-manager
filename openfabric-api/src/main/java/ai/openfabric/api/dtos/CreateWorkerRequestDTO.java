package ai.openfabric.api.dtos;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CreateWorkerRequestDTO {
    @Getter
    @Setter
    private String imageName;

    @Getter
    @Setter
    private String containerName;
}
