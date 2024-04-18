package cn.colonq.admin.service.impl;

import org.springframework.stereotype.Service;

import cn.colonq.admin.entity.RoleInfo;
import cn.colonq.admin.mapper.RoleMapper;
import cn.colonq.admin.service.IRoleService;

@Service
public class RoleServiceImpl extends BaseServiceImpl<RoleInfo> implements IRoleService {

	public RoleServiceImpl(RoleMapper roleMapper) {
		super(roleMapper);
	}
}
