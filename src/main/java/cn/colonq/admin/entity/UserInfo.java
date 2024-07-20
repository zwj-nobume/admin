package cn.colonq.admin.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.colonq.admin.anno.Table;
import cn.colonq.admin.anno.TableField;
import cn.colonq.admin.enumcfg.CompEnum;
import cn.colonq.admin.enumcfg.TableFuncEnum;
import cn.colonq.admin.group.Insert;
import cn.colonq.admin.group.Login;
import cn.colonq.admin.group.Query;
import cn.colonq.admin.group.Update;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Table(tableName = "user_info", linkTable = "user_role_link", idName = "user_id")
public record UserInfo(
		@Null(message = "新增、登录用户时无需ID", groups = {
				Insert.class, Login.class
		}) @NotBlank(message = "用户ID不得为空", groups = {
				Update.class
		}) @Size(min = 36, max = 36, message = "用户ID长度异常", groups = {
				Query.class, Update.class
		}) @TableField(isInsert = false) String userId,

		@NotBlank(message = "用户名不得为空", groups = {
				Insert.class, Login.class
		}) @Size(min = 6, max = 20, message = "用户名长度必须在6到20之间", groups = {
				Insert.class, Update.class
		}) @Pattern(regexp = "^(admin|.{6,20})$", message = "用户名长度必须在6到20之间", groups = {
				Login.class
		}) @TableField(comp = CompEnum.LIKE) String userName,

		@Null(message = "查询、修改时无需password", groups = {
				Query.class, Update.class
		}) @NotBlank(message = "用户密码不得为空", groups = {
				Insert.class, Login.class
		}) @Size(min = 6, max = 20, message = "用户密码长度必须在6到20之间", groups = {
				Insert.class, Login.class
		}) @TableField(select = false, insert = TableFuncEnum.PWD, isUpdate = false) String password,

		@Null(message = "登录时无需邮箱", groups = {
				Login.class
		}) @Email(message = "邮箱格式不正确", groups = {
				Insert.class, Update.class
		}) @TableField(comp = CompEnum.LIKE) String email,

		@Null(message = "无法接收盐值", groups = {
				Query.class, Insert.class, Update.class, Login.class
		}) @TableField(select = false, isInsert = false, isUpdate = false) String salt,

		@Null(message = "新增、登录用户时无需createName", groups = {
				Insert.class, Login.class
		}) @TableField(comp = CompEnum.LIKE, isUpdate = false) String createName,

		@Null(message = "新增、登录用户时无需createTime", groups = {
				Insert.class, Login.class
		}) @TableField(comp = CompEnum.GE, isInsert = false, isUpdate = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:dd", timezone = "GMT+8") Date createTime){
}
