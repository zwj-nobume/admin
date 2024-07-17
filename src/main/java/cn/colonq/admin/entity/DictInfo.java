package cn.colonq.admin.entity;

import cn.colonq.admin.group.Insert;
import cn.colonq.admin.group.Query;
import cn.colonq.admin.group.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public record DictInfo(
		@NotBlank(message = "字典KEY不得为空", groups = {
				Query.class, Insert.class, Update.class
		}) @Size(max = 20, message = "字典KEY最大不得超过20个字符", groups = {
				Insert.class, Update.class
		}) String key,
		@NotBlank(message = "字典值不得为空", groups = {
				Update.class
		}) @Null(message = "此操作无需输入字典值", groups = {
				Query.class, Insert.class
		}) @Size(max = 1024, message = "字典值最大不得超过1024个字符", groups = {
				Update.class
		}) String value){
}
