package cn.colonq.admin.service.impl;

import org.springframework.stereotype.Service;

import cn.colonq.admin.config.ServiceException;
import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.RoleInfo;
import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.mapper.UserMapper;
import cn.colonq.admin.service.IUserService;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserInfo> implements IUserService {

	public UserServiceImpl(final UserMapper userMapper) {
		super(userMapper);
	}

	@Override
	public Result linkUserRole(final LinkInfo info) {
		int count = baseMapper.selectCountIds(RoleInfo.class, info.ids());
		if (count != info.ids().size()) {
			throw new ServiceException("链接失败, ids数量不匹配, count = " + count);
		}
		int row = baseMapper.link("user_role_link", "user_id", "role_id", info.id(), info.ids());
		if (row != 0) {
			return Result.ok("链接成功");
		}
		throw new ServiceException("链接失败, row = " + row);
	}
}
