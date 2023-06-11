package ai.openfabric.api.exceptions;

import lombok.Getter;

@Getter
public class DockerWorkerException extends Exception {
    private final WorkerErrorInfo errorInfo;
    private final int statusCode;

    public DockerWorkerException(String message, WorkerErrorInfo errorInfo, int statusCode) {
        super(message);
        this.errorInfo = errorInfo;
        this.statusCode = statusCode;
    }

    public DockerWorkerException(WorkerErrorInfo errorInfo, int statusCode) {
        this(errorInfo.getDescription(), errorInfo, statusCode);
    }

    public DockerWorkerException(WorkerErrorInfo errorInfo) {
        this(errorInfo, 400);
    }
}
