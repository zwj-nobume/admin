package cn.colonq.admin.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ServiceException extends RuntimeException {
    private final HttpStatusCode statusCode;
    private final String msg;

    public ServiceException(String msg) {
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        this.msg = msg;
    }

    public ServiceException(HttpStatusCode statusCode, String msg) {
        this.statusCode = statusCode;
        this.msg = msg;
    }

    public ServiceException(String message, HttpStatusCode statusCode, String msg) {
        super(message);
        this.statusCode = statusCode;
        this.msg = msg;
    }

    public ServiceException(Throwable cause, HttpStatusCode statusCode, String msg) {
        super(cause);
        this.statusCode = statusCode;
        this.msg = msg;
    }

    public ServiceException(String message, Throwable cause, HttpStatusCode statusCode, String msg) {
        super(message, cause);
        this.statusCode = statusCode;
        this.msg = msg;
    }

    public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
            HttpStatusCode statusCode, String msg) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.statusCode = statusCode;
        this.msg = msg;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getMsg() {
        return msg;
    }
}
