package cn.colonq.admin.entity;

import cn.colonq.admin.group.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserEditPwd(
		@NotBlank(message = "旧密码不能为空", groups = {
				Update.class
		}) String oldPassword,
		@NotBlank(message = "新密码不得为空", groups = {
				Update.class
		}) @Size(min = 6, max = 20, message = "新密码长度必须在6到20之间", groups = {
				Update.class
		}) String newPassword,
		@NotBlank(message = "校验密码不得为空", groups = {
				Update.class
		}) String chkPassword){
}
