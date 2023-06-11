package ai.openfabric.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateContainerResponseDTO {
    @JsonProperty("Id")
    private String id;

    @JsonProperty("Warnings")
    private List<String> warnings;

    @JsonProperty("message")
    private String message;
}
