package cn.colonq.admin.config;

import java.io.IOException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import cn.colonq.admin.entity.Result;
import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NoResourceFoundException.class)
	public void handleNoResourceFound(NoResourceFoundException e, HttpServletResponse res) {
		sendError(Result.notFound(), res);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public void handleValidated(MethodArgumentNotValidException e, HttpServletResponse res) {
		final String message = e.getMessage();
		sendError(Result.error(message.substring(message.lastIndexOf("default message"), message.length() - 2)), res);
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public void handleDuplicateKey(DuplicateKeyException e, HttpServletResponse res) {
		// TODO 入日志库
		sendError(Result.error("数据唯一值重复错误"), res);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public void handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
			HttpServletResponse res) {
		// TODO 入日志库
		sendError(Result.error("请求类型错误, GET、PUT、POST、DELETE"), res);
	}

	@ExceptionHandler(ServiceException.class)
	public void handleServiceError(ServiceException e, HttpServletResponse res) {
		// TODO 入日志库
		sendError(Result.error(e), res);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public void handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpServletResponse res) {
		// TODO 入日志库
		sendError(Result.error("无法解析请求Body"), res);
	}

	public void filterServiceError(ServiceException err, HttpServletResponse res) {
		sendError(err, res);
	}

	private void sendError(Result result, HttpServletResponse res) {
		try {
			res.sendError(result.status(), result.message());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendError(ServiceException err, HttpServletResponse res) {
		try {
			res.sendError(err.getStatusCode().value(), err.getMsg());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@ExceptionHandler(Throwable.class)
	public void handleThrowable(Throwable e) {
		// TODO 入日志库
		e.printStackTrace();
	}
}
