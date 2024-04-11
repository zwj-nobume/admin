package cn.colonq.admin.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public void handleNoResourceFound(NoResourceFoundException e) {
    }

    @ExceptionHandler(NumberFormatException.class)
    public void handleNumb(NumberFormatException e) {
        e.printStackTrace();
        // TODO 入日志库
    }

    @ExceptionHandler(Throwable.class)
    public void handleThrowable(Throwable e) {
        // TODO 入日志库
        e.printStackTrace();
    }
}
