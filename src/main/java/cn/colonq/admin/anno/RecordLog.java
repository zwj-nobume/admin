package cn.colonq.admin.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.colonq.admin.enumcfg.LogTypeEnum;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordLog {
	LogTypeEnum type() default LogTypeEnum.INFO;
}
