package cn.colonq.admin.service;

import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.UserInfo;

public interface IUserService extends BaseService<UserInfo> {

	Result login(final UserInfo info);

	Result regenerateSalt();

	Result permission();

	Result selectRoleIds(final String userId);

	Result linkUserRole(final LinkInfo info);

	boolean checkPermission(String value);
}
