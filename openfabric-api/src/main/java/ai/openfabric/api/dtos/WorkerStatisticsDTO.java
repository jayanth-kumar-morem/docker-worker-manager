package ai.openfabric.api.dtos;


import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerStatisticsDTO {
    private String id;
    private String name;
    private double cpuUtilization;
    private double memoryUtilization;
    private double memoryCapacity;
    private double networkIO;
    private double blockDeviceIO;
    private double processId;
}