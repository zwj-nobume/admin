package cn.colonq.admin.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.colonq.admin.anno.Table;
import cn.colonq.admin.anno.TableField;
import cn.colonq.admin.config.CompEnum;
import cn.colonq.admin.group.Insert;
import cn.colonq.admin.group.Query;
import cn.colonq.admin.group.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

@Table(tableName = "role_info", linkTable = { "user_role_link", "role_menu_link" }, idName = "role_id")
public record RoleInfo(
		@Null(message = "新增角色时无需ID", groups = {
				Insert.class
		}) @NotBlank(message = "角色ID不得为空", groups = {
				Update.class
		}) @Size(min = 36, max = 36, message = "角色ID长度异常", groups = {
				Query.class, Update.class
		}) @TableField(isInsert = false) String roleId,

		@NotBlank(message = "角色名不得为空", groups = {
				Insert.class
		}) @TableField(comp = CompEnum.LIKE) String roleName,

		@NotBlank(message = "角色标签不得为空", groups = {
				Insert.class
		}) @TableField(comp = CompEnum.LIKE) String roleLabel,

		@Null(message = "新增时无需createName", groups = {
				Insert.class
		}) @TableField(comp = CompEnum.LIKE, isUpdate = false) String createName,

		@Null(message = "新增时无需createTime", groups = {
				Insert.class
		}) @TableField(comp = CompEnum.GE, isInsert = false, isUpdate = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:dd", timezone = "GMT+8") Date createTime){
}
