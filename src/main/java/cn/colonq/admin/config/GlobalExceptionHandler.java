package cn.colonq.admin.config;

import java.io.IOException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import cn.colonq.admin.entity.Result;
import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ResponseBody
	@ExceptionHandler(NoResourceFoundException.class)
	public Result handleNoResourceFound(NoResourceFoundException e) {
		return Result.notFound();
	}

	@ResponseBody
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Result handleValidated(MethodArgumentNotValidException e) {
		final String message = e.getMessage();
		return Result.error(message.substring(message.lastIndexOf("default message"), message.length() - 2));
	}

	@ResponseBody
	@ExceptionHandler(DuplicateKeyException.class)
	public Result handleDuplicateKey(DuplicateKeyException e) {
		// TODO 入日志库
		return Result.error("数据唯一值重复错误");
	}

	@ResponseBody
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Result handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
		// TODO 入日志库
		return Result.error("请求类型错误, GET、PUT、POST、DELETE");
	}

	@ResponseBody
	@ExceptionHandler(ServiceException.class)
	public Result handleServiceError(ServiceException e) {
		// TODO 入日志库
		return Result.error(e);
	}

	@ResponseBody
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public Result handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
		// TODO 入日志库
		return Result.error("无法解析请求Body");
	}

	public void filterServiceError(ServiceException e, HttpServletResponse response) {
		try {
			response.sendError(e.getStatusCode().value(), e.getMsg());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@ExceptionHandler(Throwable.class)
	public void handleThrowable(Throwable e) {
		// TODO 入日志库
		e.printStackTrace();
	}
}
