package cn.colonq.admin.entity;

import java.util.Date;

import cn.colonq.admin.anno.Table;
import cn.colonq.admin.anno.TableField;
import cn.colonq.admin.config.CompEnum;

@Table(tableName = "role_info", idName = "role_id")
public record RoleInfo(
		@TableField(query = false, isInsert = false) String roleId,
		@TableField(comp = CompEnum.like) String roleName,
		@TableField(comp = CompEnum.like) String roleLabel,
		@TableField(comp = CompEnum.like, isUpdate = false) String createName,
		@TableField(comp = CompEnum.ge, isInsert = false, isUpdate = false) Date createTime) {
}
