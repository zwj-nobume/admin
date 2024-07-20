package cn.colonq.admin.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.colonq.admin.anno.Table;
import cn.colonq.admin.anno.TableField;
import cn.colonq.admin.enumcfg.CompEnum;
import cn.colonq.admin.enumcfg.LogTypeEnum;
import cn.colonq.admin.group.Insert;
import cn.colonq.admin.group.Query;
import cn.colonq.admin.group.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

@Table(tableName = "log_info", linkTable = {}, idName = "log_id")
public record LogInfo(
		@Null(message = "新增日志时无需ID", groups = {
				Insert.class
		}) @NotBlank(message = "日志ID不得为空", groups = {
				Update.class
		}) @Size(min = 36, max = 36, message = "日志ID长度异常", groups = {
				Query.class, Update.class
		}) @TableField(isInsert = false) String logId,

		@NotBlank(message = "日志类型不得为空", groups = {
				Insert.class
		}) LogTypeEnum logType,

		@TableField(comp = CompEnum.LIKE) String apiParams,

		@NotBlank(message = "日志简介不得为空", groups = {
				Insert.class
		}) @TableField(comp = CompEnum.LIKE) String logIntro,

		@TableField(comp = CompEnum.LIKE) String logData,

		@Null(message = "新增时无需createName", groups = {
				Insert.class
		}) @TableField(comp = CompEnum.LIKE, isUpdate = false) String createName,

		@Null(message = "新增时无需createTime", groups = {
				Insert.class
		}) @TableField(comp = CompEnum.GE, isInsert = false, isUpdate = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:dd", timezone = "GMT+8") Date createTime){
}
