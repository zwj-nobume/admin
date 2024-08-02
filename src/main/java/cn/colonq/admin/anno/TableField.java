package cn.colonq.admin.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.colonq.admin.enumcfg.CompEnum;
import cn.colonq.admin.enumcfg.TableFuncEnum;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableField {

    boolean select() default true;

    TableFuncEnum insert() default TableFuncEnum.DEF;

    boolean isInsert() default true;

	boolean parent() default false;

    boolean isUpdate() default true;

    TableFuncEnum update() default TableFuncEnum.DEF;

    CompEnum comp() default CompEnum.EQ;
}
