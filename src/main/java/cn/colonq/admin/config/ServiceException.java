package cn.colonq.admin.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ServiceException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public ServiceException(String message) {
        super(message);
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ServiceException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ServiceException(String message, HttpStatusCode statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public ServiceException(String message, HttpStatusCode statusCode, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
