package cn.colonq.admin.entity;

public record FileMsg(
		String name,
		long size,
		boolean dir,
		boolean hide) {
}
