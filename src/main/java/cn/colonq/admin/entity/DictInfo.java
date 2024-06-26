package cn.colonq.admin.entity;

import cn.colonq.admin.group.Insert;
import cn.colonq.admin.group.Update;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record DictInfo(
		@NotBlank(message = "字典KEY不得为空", groups = {
				Insert.class, Update.class
		}) @Max(value = 20, message = "字典KEY最大不得超过20个字符", groups = {
				Insert.class, Update.class
		}) String key,
		@NotBlank(message = "字典值不得为空", groups = {
				Insert.class, Update.class
		}) @Max(value = 1024, message = "字典值最大不得超过1024个字符", groups = {
				Insert.class, Update.class
		}) String value){
}
