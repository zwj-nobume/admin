package cn.colonq.admin.entity;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record LinkInfo(
		@NotBlank(message = "id参数不能为空") String id,

		@NotEmpty(message = "ids参数不能为空数组") Set<String> ids) {
}
