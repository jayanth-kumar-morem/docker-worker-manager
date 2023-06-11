package ai.openfabric.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import java.util.Optional;
import java.util.function.Supplier;

@UtilityClass
public class JsonHelper {
    private final ObjectMapper mapper = new ObjectMapper();

    public static String toJsonString(Object data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public static <T> Optional<T> safeResolve(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }
}
