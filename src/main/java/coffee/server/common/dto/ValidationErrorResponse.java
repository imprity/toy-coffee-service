package coffee.server.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ValidationErrorResponse {
    private @Nullable List<String> globalErrors;
    private @Nullable Map<String, String> fieldErrors;

    public void putGlobalError(ObjectError err) {
        if (this.globalErrors == null) {
            this.globalErrors = new ArrayList<>();
        }

        this.globalErrors.add(err.getDefaultMessage());
    }

    public void putFieldError(FieldError err) {
        if (this.fieldErrors == null) {
            this.fieldErrors = new HashMap<>();
        }

        this.fieldErrors.put(err.getField(), err.getDefaultMessage());
    }
}
