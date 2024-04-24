package cn.colonq.admin.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import cn.colonq.admin.config.ServiceException;
import cn.colonq.admin.entity.Header;
import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.RoleInfo;
import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.mapper.UserMapper;
import cn.colonq.admin.service.IUserService;
import cn.colonq.admin.utils.JWT;
import cn.colonq.admin.utils.StringUtils;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserInfo, UserMapper> implements IUserService {

	public UserServiceImpl(final UserMapper userMapper, final StringUtils stringUtils, final JWT jwt) {
		super(userMapper, jwt);
	}

	@Override
	public Result login(final UserInfo info) {
		boolean chk = super.tmapper.checkPwd(info.userName(), info.password());
		if (chk) {
			final UserInfo user = super.tmapper.selectOne("user_name", info.userName());
			final UserInfo payload = new UserInfo(user.userId(), user.userName(), null,
					user.email(), null, user.createName(), user.createTime());
			Header header = new Header("HmacSHA256", "JWT");
			try {
				return Result.ok("登录成功", super.jwt.generateToken(header, payload, user.salt()));
			} catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
				throw new ServiceException(e.getMessage());
			}
		}
		throw new ServiceException("用户名或密码错误");
	}

	@Override
	public Result regenerateSalt() {
		UserInfo payload = super.jwt.getPayload();
		int row = super.tmapper.regenerateSalt(payload.userId());
		if (row == 1) {
			return Result.ok("重新生成盐值成功");
		}
		throw new ServiceException("重新生成盐值失败, row = " + row);
	}

	@Override
	public Result linkUserRole(final LinkInfo info) {
		int count = super.tmapper.selectCountIds(RoleInfo.class, info.ids());
		if (count != info.ids().size()) {
			throw new ServiceException("链接失败, ids数量不匹配, count = " + count);
		}
		int row = super.tmapper.link("user_role_link", "user_id", "role_id", info.id(), info.ids());
		if (row != 0) {
			return Result.ok("链接成功");
		}
		throw new ServiceException("链接失败, row = " + row);
	}
}
