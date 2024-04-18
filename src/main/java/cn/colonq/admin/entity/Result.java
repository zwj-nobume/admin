package cn.colonq.admin.entity;

import org.springframework.http.HttpStatus;

import cn.colonq.admin.config.ServiceException;

public record Result(
		int code,
		String msg,
		Object data) {

	public static Result ok() {
		return new Result(HttpStatus.OK.value(), "操作成功", null);
	}

	public static Result ok(String msg) {
		return new Result(HttpStatus.OK.value(), msg, null);
	}

	public static Result ok(Object data) {
		return new Result(HttpStatus.OK.value(), "操作成功", data);
	}

	public static Result notFound() {
		return new Result(HttpStatus.NOT_FOUND.value(), "寻找不到资源", null);
	}

	public static Result forbidden() {
		return new Result(HttpStatus.FORBIDDEN.value(), "权限不足", null);
	}

	public static Result error(String msg) {
		return new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, null);
	}

	public static Result error(ServiceException err) {
		return new Result(err.getStatusCode().value(), err.getMsg(), null);
	}

	public static Result error(Throwable err) {
		return new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), err.getMessage(), null);
	}
}