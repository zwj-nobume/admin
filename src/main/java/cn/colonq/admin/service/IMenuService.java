package cn.colonq.admin.service;

import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.MenuInfo;
import cn.colonq.admin.entity.Result;

public interface IMenuService extends IBaseService<MenuInfo> {

	Result selectRoleIds(final String menuId);

	Result linkRoleMenu(final LinkInfo info);
}
