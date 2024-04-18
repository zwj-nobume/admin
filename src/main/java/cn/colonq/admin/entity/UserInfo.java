package cn.colonq.admin.entity;

import java.util.Date;

import cn.colonq.admin.anno.Table;
import cn.colonq.admin.anno.TableField;
import cn.colonq.admin.config.CompEnum;
import cn.colonq.admin.config.TableFuncEnum;

@Table(tableName = "user_info", linkTable = "user_role_link", idName = "user_id")
public record UserInfo(
		@TableField(query = false, isInsert = false) String userId,
		@TableField(comp = CompEnum.like) String userName,
		@TableField(select = false, insert = TableFuncEnum.pwd, isUpdate = false) String password,
		@TableField(comp = CompEnum.like) String email,
		@TableField(comp = CompEnum.like, isUpdate = false) String createName,
		@TableField(comp = CompEnum.ge, isInsert = false, isUpdate = false) Date createTime) {
}
