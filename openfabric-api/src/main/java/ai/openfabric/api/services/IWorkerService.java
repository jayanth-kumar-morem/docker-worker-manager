package ai.openfabric.api.services;

import ai.openfabric.api.dtos.*;
import ai.openfabric.api.enums.ContainerState;
import ai.openfabric.api.exceptions.DockerWorkerException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IWorkerService {
    WorkerInfoDTO retrieveWorkerInformation(String workerId) throws DockerWorkerException;
    List<WorkerInfoDTO> retrieveContainerList(ContainersListRequestDTO pageInfo) throws DockerWorkerException;
    WorkerStatisticsDTO retrieveWorkerStatistics(String workerId) throws DockerWorkerException;
    public ResponseEntity<String> initializeWorker(String workerID);
    public ResponseEntity<String> shutdownWorker(String workerID);

    public CreateContainerResponseDTO createWorker(CreateWorkerRequestDTO requestDTO) ;

    WorkerInfoDTO setWorkerState(String workerId, ContainerState newStatus) throws DockerWorkerException;
}