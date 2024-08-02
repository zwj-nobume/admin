package cn.colonq.admin.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.colonq.admin.entity.LogInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.enumcfg.LogTypeEnum;
import cn.colonq.admin.service.ILogService;
import cn.colonq.admin.utils.DateUtils;
import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
	private final StringWriter swriter;
	private final PrintWriter pwriter;
	private final DateUtils dateUtils;
	private final ILogService logService;
	private final ObjectMapper objectMapper;

	public GlobalExceptionHandler(
			final DateUtils dateUtils,
			final ILogService logService,
			final ObjectMapper objectMapper) {
		swriter = new StringWriter();
		pwriter = new PrintWriter(swriter);
		this.dateUtils = dateUtils;
		this.logService = logService;
		this.objectMapper = objectMapper;
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public void handleNoResourceFound(final NoResourceFoundException e, final HttpServletResponse res) {
		insertErrorLog(e, null);
		sendError(Result.notFound(), res);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public void handleValidated(final MethodArgumentNotValidException e, final HttpServletResponse res) {
		final String message = e.getMessage();
		final String shortMessage = message.substring(message.lastIndexOf("default message"), message.length() - 2);
		insertErrorLog(e, shortMessage);
		sendError(Result.error(shortMessage), res);
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public void handleDuplicateKey(final DuplicateKeyException e, final HttpServletResponse res) {
		insertErrorLog(e, null);
		sendError(Result.error("数据唯一值重复错误"), res);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public void handleHttpRequestMethodNotSupported(
			final HttpRequestMethodNotSupportedException e,
			final HttpServletResponse res) {
		insertErrorLog(e, null);
		sendError(Result.error("请求类型错误, GET、PUT、POST、DELETE"), res);
	}

	@ExceptionHandler(ServiceException.class)
	public void handleServiceError(final ServiceException e, final HttpServletResponse res) {
		// insertErrorLog(e, null);
		sendError(Result.error(e), res);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public void handleHttpMessageNotReadable(final HttpMessageNotReadableException e, final HttpServletResponse res) {
		insertErrorLog(e, null);
		sendError(Result.error("无法解析请求Body"), res);
	}

	@ExceptionHandler(UnsupportedEncodingException.class)
	public void handleUnsupportedEncodingException(final UnsupportedEncodingException e,
			final HttpServletResponse res) {
		insertErrorLog(e, null);
		sendError(Result.error("URL Decoder 解码 URL 错误"), res);
	}

	@ExceptionHandler(Throwable.class)
	public void handleThrowable(final Throwable e, final HttpServletResponse res) {
		insertErrorLog(e, null);
		sendError(Result.error(e), res);
	}

	private void sendError(final Result result, final HttpServletResponse res) {
		res.setStatus(result.status());
		res.setHeader("Access-Control-Allow-Origin", "*");
		res.setHeader("Cache-Control", "no-cache");
		res.setCharacterEncoding("UTF-8");
		res.setContentType("application/json");
		try {
			PrintWriter writer = res.getWriter();
			writer.println(objectMapper.writeValueAsString(result));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void insertErrorLog(final Throwable e, final String message) {
		StackTraceElement stackTrace = e.getStackTrace()[0];
		final String nowDateFormat = dateUtils.format(dateUtils.getNowDate());
		final String controlName = stackTrace.getClassName();
		final String methodName = stackTrace.getMethodName();
		final LogTypeEnum logType = LogTypeEnum.ERROR;
		final String errMessage = message == null ? e.getMessage().replaceAll("'", "\\\\'")
				: message.replaceAll("'", "\\\\'");
		final String logIntro = String.format("%s -- Controller: %s; Method: %s; LogType: %s; ErrMessage: %s",
				nowDateFormat, controlName, methodName, logType.toString(), errMessage);
		swriter.getBuffer().setLength(0);
		e.printStackTrace(pwriter);
		pwriter.flush();
		swriter.flush();
		final String logData = swriter.toString().replaceAll("'", "\\\\'");
		final LogInfo logInfo = new LogInfo(null, logType, null, logIntro, logData, null, null);
		logService.insert(logInfo);
	}
}
