package cn.colonq.admin.service.impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.mapper.UserMapper;
import cn.colonq.admin.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public PageList<UserInfo> selectPage(final UserInfo param, final long pageNum, final long pageSize) {
        return userMapper.selectPage(param, pageNum, pageSize);
    }

    @Override
    public Result insert(final UserInfo param) {
        // TODO 获取登录用户名
        String createName = "admin";
        UserInfo insertParam = new UserInfo(null, param.userName(), param.password(), param.email(), createName, null);
        int row = userMapper.insert(insertParam);
        if (row == 1) {
            return Result.ok("新增用户成功");
        }
        throw new InternalError("新增用户失败, row = " + row);
    }

    @Override
    public Result update(final UserInfo param) {
        int row = userMapper.update(param);
        if (row == 1) {
            return Result.ok("修改用户成功");
        }
        throw new InternalError("修改用户失败, row = " + row);
    }

    @Override
    public Result delete(final Set<String> ids) {
        int row = userMapper.delete(UserInfo.class, ids);
        if (row > 0) {
            return Result.ok("删除用户成功");
        }
        throw new InternalError("删除用户失败, row = " + row);
    }
}
