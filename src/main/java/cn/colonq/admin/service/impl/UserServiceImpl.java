package cn.colonq.admin.service.impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import cn.colonq.admin.entity.PageList;
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
    public int insert(final UserInfo param) {
        String createName = "admin";
        UserInfo insertParam = new UserInfo(null, param.userName(), param.password(), param.email(), createName, null);
        return userMapper.insert(insertParam);
    }

    @Override
    public int update(final UserInfo param) {
        return userMapper.update(param);
    }

    @Override
    public int delete(final Set<String> ids) {
        return userMapper.delete(UserInfo.class, ids);
    }
}
