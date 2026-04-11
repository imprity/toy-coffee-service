package coffee.server.common.dto;

import org.jspecify.annotations.Nullable;

public record BaseResponse<T extends @Nullable Object>(
        @Nullable T data, Boolean success, @Nullable String errorCode, @Nullable String errorMessage) {
    public static <T> BaseResponse<T> success(@Nullable T data) {
        return new BaseResponse<T>(data, true, null, null);
    }

    public static <T> BaseResponse<T> fail(String errorCode, @Nullable String errorMessage) {
        return new BaseResponse<T>(null, false, errorCode, errorMessage);
    }

    public static <T> BaseResponse<T> fail(String errorCode, @Nullable String errorMessage, @Nullable T data) {
        return new BaseResponse<T>(data, false, errorCode, errorMessage);
    }
}
