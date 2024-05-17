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

@Table(tableName = "menu_info", linkTable = "role_menu_link", idName = "menu_id")
public record MenuInfo(
		@Null(message = "新增菜单时无需ID", groups = {
				Insert.class
		}) @NotBlank(message = "菜单ID不得为空", groups = {
				Update.class
		}) @Size(min = 36, max = 36, message = "菜单ID长度异常", groups = {
				Query.class, Update.class
		}) @TableField(isInsert = false) String menuId,

		@NotBlank(message = "菜单名不得为空", groups = {
				Insert.class
		}) @Size(max = 20, message = "菜单名长度不超过20", groups = {
				Insert.class, Update.class
		}) @TableField(comp = CompEnum.like) String menuName,

		@NotBlank(message = "菜单标签不得为空", groups = {
				Insert.class
		}) @Size(min = 6, max = 50, message = "菜单标签长度必须在6到50之间", groups = {
				Insert.class, Update.class
		}) @TableField(comp = CompEnum.like) String menuLabel,

		@NotBlank(message = "权限标识不得为空", groups = {
				Insert.class
		}) @Size(max = 50, message = "权限标识长度不超过50", groups = {
				Insert.class, Update.class
		}) @TableField(comp = CompEnum.like) String permission,

		@Size(min = 36, max = 36, message = "父级ID长度异常", groups = {
				Query.class, Insert.class, Update.class
		}) @TableField(parent = true) String parentId,

		@Null(message = "新增、修改菜单时无需createName", groups = {
				Insert.class, Update.class
		}) @TableField(comp = CompEnum.like, isUpdate = false) String createName,

		@Null(message = "新增、修改菜单时无需createTime", groups = {
				Insert.class, Update.class
		}) @TableField(comp = CompEnum.ge, isInsert = false, isUpdate = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:dd", timezone = "GMT+8") Date createTime){
}
