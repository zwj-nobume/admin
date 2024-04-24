package cn.colonq.admin.service.impl;

import org.springframework.stereotype.Service;

import cn.colonq.admin.config.ServiceException;
import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.MenuInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.RoleInfo;
import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.mapper.RoleMapper;
import cn.colonq.admin.service.IRoleService;
import cn.colonq.admin.utils.JWT;

@Service
public class RoleServiceImpl extends BaseServiceImpl<RoleInfo, RoleMapper> implements IRoleService {

	public RoleServiceImpl(final RoleMapper roleMapper, final JWT jwt) {
		super(roleMapper, jwt);
	}

	@Override
	public Result linkUserRole(final LinkInfo info) {
		int count = super.tmapper.selectCountIds(UserInfo.class, info.ids());
		if (count != info.ids().size()) {
			throw new ServiceException("链接失败, ids数量不匹配, count = " + count);
		}
		int row = super.tmapper.link("user_role_link", "role_id", "user_id", info.id(), info.ids());
		if (row != 0) {
			return Result.ok("链接成功");
		}
		throw new ServiceException("链接失败, row = " + row);
	}

	@Override
	public Result linkRoleMenu(final LinkInfo info) {
		int count = super.tmapper.selectCountIds(MenuInfo.class, info.ids());
		if (count != info.ids().size()) {
			throw new ServiceException("链接失败, ids数量不匹配, count = " + count);
		}
		int row = super.tmapper.link("role_menu_link", "role_id", "menu_id", info.id(), info.ids());
		if (row != 0) {
			return Result.ok("链接成功");
		}
		throw new ServiceException("链接失败, row = " + row);
	}
}
