package ai.openfabric.api.controller;

import ai.openfabric.api.dtos.*;
import ai.openfabric.api.enums.ContainerState;
import ai.openfabric.api.exceptions.DockerWorkerException;
import ai.openfabric.api.services.IWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("${node.api.path}/worker")
public class WorkerController {

    private final IWorkerService workerService;

    @Autowired
    public WorkerController(IWorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping("/info/{workerId}")
    public WorkerInfoDTO fetchWorkerInfo(@PathVariable String workerId) throws DockerWorkerException {
        return workerService.retrieveWorkerInformation(workerId);
    }

    @PostMapping("/stop/{id}")
    public ResponseEntity<String> stopWorker(@PathVariable String id) {
        return workerService.shutdownWorker(id);
    }

    @PostMapping("/start/{id}")
    public ResponseEntity<String> startWorker(@PathVariable String id) {
        return workerService.initializeWorker(id);
    }

    @PostMapping("/list")
    public List<WorkerInfoDTO> fetchWorkerList(@Valid @RequestBody ContainersListRequestDTO pageInfo) throws DockerWorkerException {
        return workerService.retrieveContainerList(pageInfo);
    }

    @PostMapping("/status/{workerId}")
    public WorkerInfoDTO updateWorkerStatus(
            @PathVariable String workerId, @RequestHeader ContainerState newStatus) throws DockerWorkerException {
        return workerService.setWorkerState(workerId, newStatus);
    }

    @GetMapping("/stats/{workerId}")
    public WorkerStatisticsDTO fetchWorkerStatus(@PathVariable String workerId) throws DockerWorkerException {
        return workerService.retrieveWorkerStatistics(workerId);
    }

    @PostMapping("/create")
    public ResponseEntity<CreateContainerResponseDTO> createWorker(@RequestBody CreateWorkerRequestDTO requestDTO) throws DockerWorkerException {
        CreateContainerResponseDTO createContainerResponseDTO = workerService.createWorker(requestDTO);
        return ResponseEntity.ok(createContainerResponseDTO);
    }
}
