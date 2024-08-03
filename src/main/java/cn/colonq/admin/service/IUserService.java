package cn.colonq.admin.service;

import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.UserEditPwd;
import cn.colonq.admin.entity.UserInfo;

public interface IUserService extends IBaseService<UserInfo> {

	Result login(final UserInfo info);

	Result regenerateSalt();

	Result permission();

	Result selectRoleIds(final String userId);

    Result editpwd(UserEditPwd info);

	Result linkUserRole(final LinkInfo info);

	boolean checkPermission(final String value);
}
