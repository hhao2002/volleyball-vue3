package cn.edu.hnzj.volleyball.common;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final int code;
    private final int httpStatus;

    public BusinessException(String message) {
        this(400, 400, message);
    }

    public BusinessException(int code, String message) {
        this(code, 400, message);
    }

    public BusinessException(int code, int httpStatus, String message) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(401, 401, message);
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(403, 403, message);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(404, 404, message);
    }
}
