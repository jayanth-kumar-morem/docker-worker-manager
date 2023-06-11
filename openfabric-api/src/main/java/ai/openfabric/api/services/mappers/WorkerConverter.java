package ai.openfabric.api.services.mappers;

import ai.openfabric.api.dtos.WorkerInfoDTO;
import ai.openfabric.api.dtos.WorkerStatisticsDTO;
import ai.openfabric.api.enums.ContainerState;
import ai.openfabric.api.model.Worker;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;

import org.springframework.beans.BeanUtils;

public class WorkerConverter {

    public static Worker convertToWorker(Container container) {
        if (container == null) {
            return null;
        }

        Worker worker = new Worker();
        BeanUtils.copyProperties(container, worker);
        worker.setStatus(ContainerState.valueOf(container.getStatus()));
        return worker;
    }

    public static WorkerInfoDTO convertToWorkerInfoDTO(Worker worker) {
        if (worker == null) {
            return null;
        }
        WorkerInfoDTO responseDTO = new WorkerInfoDTO();
        BeanUtils.copyProperties(worker, responseDTO);
        responseDTO.setId(worker.getId());
        responseDTO.setImage(worker.getDockerImageName());
        responseDTO.setState(worker.getStatus());
        return responseDTO;
    }

    public static WorkerStatisticsDTO convertToWorkerStatsDTO(Statistics statistics) {
        if (statistics == null) {
            return null;
        }
        WorkerStatisticsDTO responseDTO = new WorkerStatisticsDTO();
        BeanUtils.copyProperties(statistics.getCpuStats().getCpuUsage(), responseDTO);
        responseDTO.setMemoryCapacity(statistics.getMemoryStats().getLimit());
        responseDTO.setMemoryUtilization(statistics.getMemoryStats().getUsage());
        responseDTO.setProcessId(statistics.getPidsStats().getCurrent());
        return responseDTO;
    }
}
