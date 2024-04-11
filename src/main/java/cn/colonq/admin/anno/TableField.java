package cn.colonq.admin.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.colonq.admin.config.CompEnum;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableField {
    boolean query() default true;

    boolean select() default true;

    CompEnum comp() default CompEnum.eq;
}
