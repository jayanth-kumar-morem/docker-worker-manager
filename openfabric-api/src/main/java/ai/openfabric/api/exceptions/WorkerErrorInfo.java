package ai.openfabric.api.exceptions;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WorkerErrorInfo {
    private String error;
    private String description;
}