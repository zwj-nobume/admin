package cn.colonq.admin.entity;

import java.util.Set;

public record LinkInfo(
		String id,
		Set<String> ids) {
}
