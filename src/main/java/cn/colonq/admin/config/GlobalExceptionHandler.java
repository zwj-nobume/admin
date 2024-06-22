package cn.colonq.admin.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.colonq.admin.entity.Result;
import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
	private final ObjectMapper objectMapper;

	public GlobalExceptionHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public void handleNoResourceFound(NoResourceFoundException e, HttpServletResponse res) {
		sendError(Result.notFound(), res);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public void handleValidated(MethodArgumentNotValidException e, HttpServletResponse res) {
		final String message = e.getMessage();
		final String errMsg = message.substring(message.lastIndexOf("default message"), message.length() - 2);
		sendError(Result.error(errMsg), res);
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

	@ExceptionHandler(UnsupportedEncodingException.class)
	public void handleUnsupportedEncodingException(UnsupportedEncodingException e, HttpServletResponse res) {
		// TODO 入日志库
		sendError(Result.error("URL Decoder 解码 URL 错误"), res);
	}

	private void sendError(Result result, HttpServletResponse res) {
		res.setStatus(result.status());
		res.setHeader("Access-Control-Allow-Origin", "*");
		res.setHeader("Cache-Control", "no-cache");
		res.setCharacterEncoding("UTF-8");
		res.setContentType("application/json");
		try {
			PrintWriter writer = res.getWriter();
			writer.println(objectMapper.writeValueAsString(result));
			writer.flush();
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
