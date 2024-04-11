package cn.colonq.admin.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.colonq.admin.anno.Table;
import cn.colonq.admin.anno.TableField;
import cn.colonq.admin.config.CompEnum;

@Table(tableName = "user_info", idName = "user_id")
public record UserInfo(
		@TableField(query = false) String userId,
		@TableField(comp = CompEnum.like) String userName,
		@TableField(select = false) String password,
		@TableField(comp = CompEnum.like) String email,
		@TableField(comp = CompEnum.like) String createName,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") Date createTime) {
}
