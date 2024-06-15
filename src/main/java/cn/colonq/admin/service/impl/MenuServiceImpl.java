package cn.colonq.admin.service.impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import cn.colonq.admin.config.ServiceException;
import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.MenuInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.RoleInfo;
import cn.colonq.admin.mapper.MenuMapper;
import cn.colonq.admin.service.IMenuService;
import cn.colonq.admin.utils.JWT;
import cn.colonq.admin.utils.StringUtils;

@Service(value = "menuService")
public class MenuServiceImpl extends BaseServiceImpl<MenuInfo, MenuMapper> implements IMenuService {

	public MenuServiceImpl(
			final StringUtils stringUtils,
			final MenuMapper menuMapper,
			final JWT jwt) {
		super(stringUtils, MenuInfo.class, menuMapper, jwt);
	}

	@Override
	public Result selectRoleIds(String menuId) {
		final Set<String> roleIds = super.tmapper.selectLinkById("role_menu_link", "menu_id", "role_id", menuId);
		return Result.ok(roleIds);
	}

	@Override
	public Result linkRoleMenu(final LinkInfo info) {
		int count = super.tmapper.selectCountIds(Set.of(info.id()));
		if (count == 0) {
			throw new ServiceException("链接失败, id不匹配, count = " + count);
		}
		count = super.tmapper.selectCountIds(RoleInfo.class, info.ids());
		if (count != info.ids().size()) {
			throw new ServiceException("链接失败, ids数量不匹配, count = " + count);
		}
		int row = super.tmapper.link("role_menu_link", "menu_id", "role_id", info.id(), info.ids());
		if (row != 0) {
			return Result.ok("链接成功");
		}
		throw new ServiceException("链接失败, row = " + row);
	}
}
