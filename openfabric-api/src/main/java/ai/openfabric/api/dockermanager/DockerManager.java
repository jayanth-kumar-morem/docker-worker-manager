package ai.openfabric.api.dockermanager;

import ai.openfabric.api.config.DockerSettings;
import ai.openfabric.api.dtos.CreateContainerResponseDTO;
import ai.openfabric.api.exceptions.WorkerErrorInfo;
import ai.openfabric.api.exceptions.DockerWorkerException;
import ai.openfabric.api.model.Worker;
import ai.openfabric.api.services.mappers.WorkerConverter;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.InvocationBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DockerManager implements AutoCloseable {

    private final com.github.dockerjava.api.DockerClient dockerClient;
    private final DockerHttpClient dockerHttpClient;
    private final DockerSettings dockerSettings;

    public DockerManager(DockerSettings dockerSettings) {
        this.dockerSettings = dockerSettings;
        DockerClientConfig config = retrieveDefaultClientConfig();
        dockerHttpClient = initializeDockerHttpClient(config);
        dockerClient = DockerClientImpl.getInstance(config, dockerHttpClient);
    }

    public List<Worker> retrieveCurrentContainers() {
        List<Container> containers = dockerClient.listContainersCmd().exec();
        return containers.stream()
                .map(WorkerConverter::convertToWorker)
                .collect(Collectors.toList());
    }

    public void launchContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }

    @SneakyThrows
    public Statistics retrieveContainerStatistics(String containerId) throws DockerWorkerException {
        InvocationBuilder.AsyncResultCallback<Statistics> callback = new InvocationBuilder.AsyncResultCallback<>();
        dockerClient.statsCmd(containerId).exec(callback);
        try {
            return callback.awaitResult();
        } catch (RuntimeException e) {
            throw new DockerWorkerException(WorkerErrorInfo.builder().build(), 500);
        } finally {
            callback.close();
        }
    }

    public Worker retrieveContainerDetails(String containerId) {
        List<Container> containers = dockerClient.listContainersCmd()
                .withIdFilter(Collections.singletonList(containerId))
                .exec();
        return StringUtils.isNotEmpty(containerId) ? WorkerConverter.convertToWorker(containers.get(0)) : null;
    }

    public void terminateContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
    }

    public CreateContainerResponseDTO buildContainer(String imageName, String containerName) throws com.github.dockerjava.api.exception.NotFoundException {
        CreateContainerResponse response = dockerClient.createContainerCmd(imageName)
                .withUser("root")
                .withName(containerName)
                .exec();
        return CreateContainerResponseDTO.builder()
                .id(response.getId())
                .warnings(Arrays.asList(response.getWarnings()))
                .build();
    }

    private DockerHttpClient initializeDockerHttpClient(DockerClientConfig configuration) {
        return new ApacheDockerHttpClient.Builder()
                .dockerHost(configuration.getDockerHost())
                .sslConfig(configuration.getSSLConfig())
                .maxConnections(dockerSettings.getDockerClientMaxConnections())
                .connectionTimeout(Duration.ofSeconds(dockerSettings.getDockerClientConnectionTimeout()))
                .responseTimeout(Duration.ofSeconds(dockerSettings.getDockerClientResponseTimeout()))
                .build();
    }

    private DockerClientConfig retrieveDefaultClientConfig() {
        return buildClientConfiguration(dockerSettings);
    }

    private DockerClientConfig buildClientConfiguration(DockerSettings dockerSettings) {
        return DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerSettings.getDockerHost())
                .withDockerTlsVerify(dockerSettings.isTlsVerificationRequired())
                .withDockerCertPath(dockerSettings.getDockerCertPath())
                .withRegistryUsername(dockerSettings.getRegistryUsername())
                .withRegistryPassword(dockerSettings.getRegistryPassword())
                .withRegistryEmail(dockerSettings.getRegistryMail())
                .withRegistryUrl(dockerSettings.getRegistryUrl())
                .build();
    }

    @Override
    public void close() throws RuntimeException {
        if (dockerHttpClient != null) {
            try {
                dockerHttpClient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
