package cn.colonq.admin.service;

import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.RoleInfo;

public interface IRoleService extends BaseService<RoleInfo> {

	Result selectUserIds(final String roleId);

	Result selectMenuIds(final String roleId);

	Result linkUserRole(final LinkInfo info);

	Result linkRoleMenu(final LinkInfo info);
}
