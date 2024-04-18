package cn.colonq.admin.entity;

import java.util.Date;

import cn.colonq.admin.anno.Table;
import cn.colonq.admin.anno.TableField;
import cn.colonq.admin.config.CompEnum;

@Table(tableName = "menu_info", idName = "menu_id")
public record MenuInfo(
        @TableField(query = false, isInsert = false) String menuId,
        @TableField(comp = CompEnum.like) String menuName,
        @TableField(comp = CompEnum.like) String menuLabel,
        String parentId,
        @TableField(comp = CompEnum.like, isUpdate = false) String createName,
        @TableField(comp = CompEnum.ge, isInsert = false, isUpdate = false) Date createTime) {
}
