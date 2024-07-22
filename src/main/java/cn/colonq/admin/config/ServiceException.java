package cn.colonq.admin.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ServiceException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public ServiceException(final String message) {
        super(message);
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ServiceException(final String message, final HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ServiceException(
            final String message,
            final HttpStatusCode statusCode,
            final Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public ServiceException(
            final String message,
            final HttpStatusCode statusCode,
            final Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
