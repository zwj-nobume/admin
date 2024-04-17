package cn.colonq.admin.service.impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import cn.colonq.admin.config.ServiceException;
import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.RoleInfo;
import cn.colonq.admin.mapper.RoleMapper;
import cn.colonq.admin.service.IRoleService;

@Service
public class RoleServiceImpl implements IRoleService {
	private final RoleMapper roleMapper;

	public RoleServiceImpl(RoleMapper roleMapper) {
		this.roleMapper = roleMapper;
	}

	@Override
	public PageList<RoleInfo> selectPage(RoleInfo param, long pageNum, long pageSize) {
		return roleMapper.selectPage(param, pageNum, pageSize);
	}

	@Override
	public Result insert(RoleInfo param) {
		// TODO 获取登录用户名
		String createName = "admin";
		RoleInfo insertParam = new RoleInfo(null, param.roleName(), param.roleLabel(), createName, null);
		int row = roleMapper.insert(insertParam);
		if (row == 1) {
			return Result.ok("新增角色成功");
		}
		throw new ServiceException("新增角色失败, row = " + row);
	}

	@Override
	public Result update(RoleInfo param) {
		int row = roleMapper.update(param);
		if (row == 1) {
			return Result.ok("修改角色成功");
		}
		throw new ServiceException("修改角色失败, row = " + row);
	}

	@Override
	public Result delete(Set<String> ids) {
		int row = roleMapper.delete(RoleInfo.class, ids);
		if (row > 0) {
			return Result.ok("删除角色成功");
		}
		throw new ServiceException("删除角色失败, row = " + row);
	}
}
