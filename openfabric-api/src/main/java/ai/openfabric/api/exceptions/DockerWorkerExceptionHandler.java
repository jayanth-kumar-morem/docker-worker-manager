package ai.openfabric.api.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class DockerWorkerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(DockerWorkerException.class)
    public ResponseEntity<WorkerErrorInfo> handleWorkerException(DockerWorkerException ex) {
        WorkerErrorInfo errorDetail = ex.getErrorInfo();
        return ResponseEntity.status(500).body(errorDetail);
    }
}
