package ai.openfabric.api.repository;

import ai.openfabric.api.enums.ContainerState;
import ai.openfabric.api.model.Worker;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerRepository extends PagingAndSortingRepository<Worker, String> {

    @Override
    @Query("SELECT w FROM Worker w WHERE w.dockerImageId = :workerId")
    Optional<Worker> findById(@Param("workerId") String workerId);

    Worker save(Worker worker);

    List<Worker> findAllByStatus(ContainerState status, Pageable pageable);

    Page<Worker> findAll(Pageable pageable);

    List<Worker> findAllById(String id, Pageable pageable);
}
