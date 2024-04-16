package cn.colonq.admin.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.colonq.admin.config.CompEnum;
import cn.colonq.admin.config.TableFuncEnum;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableField {
    boolean query() default true;

    boolean select() default true;

    TableFuncEnum insert() default TableFuncEnum.def;

    boolean isInsert() default true;

    boolean isUpdate() default true;

    CompEnum comp() default CompEnum.eq;
}
