package coffee.server.common.exception;

import jakarta.servlet.RequestDispatcher;
import java.util.HashMap;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * Spring boot에서 에러가 났을 때 기본적으로 주는 에러 형식을 살짝
 * 바꾸기 위해서 만든 class 입니다.
 * <P>
 * 자세한 사항은 {@link DefaultErrorAttributes}참고해주세요.
 *
 */
@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> newErrorAttributes = new HashMap<>();

        newErrorAttributes.put("data", null);
        newErrorAttributes.put("success", false);
        addErrorCodeAndMessage(newErrorAttributes, webRequest);

        return newErrorAttributes;
    }

    private void addErrorCodeAndMessage(Map<String, Object> errorAttributes, RequestAttributes requestAttributes) {
        Integer status = getAttribute(requestAttributes, RequestDispatcher.ERROR_STATUS_CODE);
        if (status == null) {
            errorAttributes.put("errorCode", ErrorCode.ERROR.name());
            return;
        }
        try {
            errorAttributes.put("errorCode", HttpStatus.valueOf(status).name());
        } catch (Exception ex) {
            // Unable to obtain a HttpStatus name
            errorAttributes.put("errorCode", ErrorCode.ERROR.name());
        }
        try {
            errorAttributes.put("errorMessage", HttpStatus.valueOf(status).getReasonPhrase());
        } catch (Exception ex) {
            // Unable to obtain a HttpStatus phrase
            errorAttributes.put("errorMessage", ErrorCode.ERROR.name());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> @Nullable T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }
}
