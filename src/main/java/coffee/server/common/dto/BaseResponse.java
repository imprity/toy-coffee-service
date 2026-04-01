package coffee.server.common.dto;

public record BaseResponse<T>(T data, Boolean success, String errorCode, String errorMessage) {
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(data, true, null, null);
    }

    public static <T> BaseResponse<T> fail(String errorCode, String errorMessage) {
        return new BaseResponse<T>(null, false, errorCode, errorMessage);
    }

    public static <T> BaseResponse<T> fail(String errorCode, String errorMessage, T data) {
        return new BaseResponse<T>(data, false, errorCode, errorMessage);
    }
}
