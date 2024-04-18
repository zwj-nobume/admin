package cn.colonq.admin.service.impl;

import org.springframework.stereotype.Service;

import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.mapper.UserMapper;
import cn.colonq.admin.service.IUserService;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserInfo> implements IUserService {

	public UserServiceImpl(UserMapper userMapper) {
		super(userMapper);
	}
}
