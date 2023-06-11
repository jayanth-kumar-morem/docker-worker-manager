package ai.openfabric.api.services.impl;

import ai.openfabric.api.dockermanager.DockerManager;
import ai.openfabric.api.config.DockerSettings;
import ai.openfabric.api.dtos.*;
import ai.openfabric.api.enums.ContainerState;
import ai.openfabric.api.exceptions.DockerWorkerException;
import ai.openfabric.api.exceptions.WorkerErrorInfo;
import ai.openfabric.api.JsonHelper;
import ai.openfabric.api.model.Worker;
import ai.openfabric.api.repository.WorkerRepository;
import ai.openfabric.api.services.IWorkerService;
import ai.openfabric.api.services.mappers.WorkerConverter;
import com.github.dockerjava.api.model.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class WorkerServiceImpl implements IWorkerService {

    @Autowired
    private DockerSettings dockerSettings;

    private DockerManager dockerManager;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    public WorkerServiceImpl(DockerSettings dockerSettings) {
        this.dockerSettings = dockerSettings;
        this.dockerManager = new DockerManager(dockerSettings);
    }

    @Override
    public WorkerInfoDTO setWorkerState(String workerId, ContainerState newStatus) throws DockerWorkerException {
        // Validate the worker ID
        validateWorkerId(workerId);

        // Fetch the worker info and persist it
        Worker existingWorker = fetchAndPersistWorkerInfo(workerId);

        // Check if the worker's status needs to be updated
        if (shouldUpdateStatus(newStatus, existingWorker.getStatus())) {
            // Execute the update operation
            executeWorkerStatusUpdate(newStatus, workerId);

            // Fetch the updated worker info and persist it
            existingWorker = fetchAndPersistWorkerInfo(workerId);
        }

        // Convert the updated worker into a response DTO and return it
        return WorkerConverter.convertToWorkerInfoDTO(existingWorker);
    }

    // Check if the worker's status needs to be updated
    private boolean shouldUpdateStatus(ContainerState newStatus, ContainerState currentStatus) {
        return !currentStatus.equals(newStatus);
    }

    // Execute the update operation to change the worker's status
    private void executeWorkerStatusUpdate(ContainerState newStatus, String workerId) throws DockerWorkerException {
        Optional<Worker> workerOptional = workerRepository.findById(workerId);
        if (workerOptional.isPresent()) {
            Worker worker = workerOptional.get();
            worker.setStatus(newStatus);
            workerRepository.save(worker);
        } else {
            throw new DockerWorkerException(
                    WorkerErrorInfo.builder()
                            .error("1234")
                            .description("No worker found with this ID.")
                            .build());
        }
    }



@Override
public WorkerInfoDTO retrieveWorkerInformation(String workerId) throws DockerWorkerException {
    // Validate the worker ID
    validateWorkerId(workerId);

    // Fetch and persist worker information
    Worker persistedWorker = fetchAndPersistWorkerInfo(workerId);

    // Convert the persisted worker to a response DTO
    WorkerInfoDTO responseDTO = WorkerConverter.convertToWorkerInfoDTO(persistedWorker);

    // Return the response DTO
    return responseDTO;
}

    @Override
    public List<WorkerInfoDTO> retrieveContainerList(ContainersListRequestDTO requestDTO) {
        // Check if the requestDTO is null
        if (requestDTO == null) {
            return new ArrayList<>(); // Return an empty list
        }

        // Get the page size and page number from requestDTO with default values
        int pageSize = JsonHelper.safeResolve(() -> requestDTO.getPageLimit()).orElse(10);
        int pageNumber = JsonHelper.safeResolve(() -> requestDTO.getPageIndex()).orElse(0);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // Initialize the list of filtered workers and retrieve the current containers
        List<Worker> filteredWorkers = new ArrayList<>();
        List<Worker> currentContainers = dockerManager.retrieveCurrentContainers();

        // Save the current containers to the worker repository
        workerRepository.saveAll(currentContainers);

        // Check if page filters exist in the requestDTO
        if (Objects.nonNull(requestDTO.getFilterOptions())) {
            FilterOptions filterOptions = requestDTO.getFilterOptions();

            // Check if the container ID filter is present
            if (Objects.nonNull(filterOptions.getIdFilter())) {
                filteredWorkers = workerRepository.findAllById(filterOptions.getIdFilter(), pageable);
            }
            // Check if the status filter is present
            else if (Objects.nonNull(filterOptions.getContainerState())) {
                filteredWorkers = workerRepository.findAllByStatus(filterOptions.getContainerState(), pageable);
            }
        }
        // If page filters do not exist, retrieve all workers with pagination
        else {
            Page<Worker> workerPage = workerRepository.findAll(pageable);
            filteredWorkers = workerPage.getContent();
        }

        // Map the filtered workers to WorkerInfoDTO and collect them into a list
        return filteredWorkers.stream()
                .map(WorkerConverter::convertToWorkerInfoDTO)
                .collect(Collectors.toList());
    }

    @Override
    public WorkerStatisticsDTO retrieveWorkerStatistics(String workerId) throws DockerWorkerException {
        // Validate the worker ID
        validateWorkerId(workerId);

        // Retrieve the container statistics for the worker
        Statistics containerStats = dockerManager.retrieveContainerStatistics(workerId);

        // Map the container statistics to a WorkerStatisticsDTO object
        WorkerStatisticsDTO workerStatisticsDTO = WorkerConverter.convertToWorkerStatsDTO(containerStats);

        // Set the worker ID in the response DTO
        workerStatisticsDTO.setId(workerId);

        // Return the worker statistics response DTO
        return workerStatisticsDTO;
    }

    private Worker fetchAndPersistWorkerInfo(String workerId) throws DockerWorkerException {
        // Fetch worker information from the Docker client
        Worker worker = dockerManager.retrieveContainerDetails(workerId);

        // Check if worker information is null
        if (Objects.isNull(worker)) {
            // Throw a DockerWorkerException if worker information is not found
            throw new DockerWorkerException(
                    WorkerErrorInfo.builder()
                            .error("1234")
                            .description("No worker found with this ID.")
                            .build());
        }

        // Save the worker information to the repository
        return workerRepository.save(worker);
    }

    private void validateWorkerId(String workerId) throws DockerWorkerException {
        // Check if the worker ID is empty, has an invalid length, or doesn't match the expected pattern
        if (StringUtils.isEmpty(workerId)
                || workerId.length() != 64
                || !Pattern.matches("^[0-9a-z]{64}$", workerId)) {
            // Throw a DockerWorkerException if the worker ID is invalid
            throw new DockerWorkerException(
                    WorkerErrorInfo.builder()
                            .error("1234")
                            .description("Invalid worker ID.")
                            .build());
        }
    }

    @Override
    public ResponseEntity<String> initializeWorker(String workerId) {
        // Check if the worker exists
        Optional<Worker> workerOptional = workerRepository.findById(workerId);
        if (workerOptional.isPresent()) {
            // Worker found, start the container
            dockerManager.launchContainer(workerId);
            return ResponseEntity.ok("Worker started successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Worker not found");
        }
    }

    @Override
    public ResponseEntity<String> shutdownWorker(String workerId) {
        // Check if the worker exists
        Optional<Worker> workerOptional = workerRepository.findById(workerId);
        if (workerOptional.isPresent()) {
            // Worker found, stop the container
            dockerManager.terminateContainer(workerId);
            return ResponseEntity.ok("Worker stopped successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Worker not found");
        }
    }

    @Override
    public CreateContainerResponseDTO createWorker(CreateWorkerRequestDTO requestDTO) {
        try {
            // Create the container
            CreateContainerResponseDTO responseDTO = dockerManager.buildContainer(requestDTO.getImageName(), requestDTO.getContainerName());

            // Create a new Worker object
            Worker worker = Worker.builder()
                    .id(responseDTO.getId())
                    .workerName(requestDTO.getContainerName())
                    .dockerImageName(requestDTO.getImageName())
                    .dockerImageId(responseDTO.getId())
                    .status(ContainerState.created)
                    .build();

            // Save the worker in the repository
            workerRepository.save(worker);

            return responseDTO;
        } catch (com.github.dockerjava.api.exception.NotFoundException e) {
            return CreateContainerResponseDTO.builder().message("Image Not Found").build();
        }
    }

}