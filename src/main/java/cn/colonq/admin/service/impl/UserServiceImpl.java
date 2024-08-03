package cn.colonq.admin.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import cn.colonq.admin.config.ServiceException;
import cn.colonq.admin.entity.Header;
import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.RoleInfo;
import cn.colonq.admin.entity.UserEditPwd;
import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.mapper.UserMapper;
import cn.colonq.admin.service.IUserService;
import cn.colonq.admin.utils.JWT;
import cn.colonq.admin.utils.StringUtils;

@Service(value = "userService")
public class UserServiceImpl extends BaseServiceImpl<UserInfo, UserMapper> implements IUserService {

	public UserServiceImpl(
			final StringUtils stringUtils,
			final UserMapper userMapper,
			final JWT jwt) {
		super(UserInfo.class, stringUtils, userMapper, jwt);
	}

	@Override
	public Result login(final UserInfo info) {
		final boolean chk = super.tmapper.checkPwd(info.userName(), info.password());
		if (chk) {
			final UserInfo user = super.tmapper.selectOne("user_name", info.userName());
			final UserInfo payload = new UserInfo(user.userId(), user.userName(), null,
					user.email(), null, user.createName(), user.createTime());
			final Header header = new Header("HmacSHA256", "JWT");
			try {
				final String token = super.jwt.generateToken(header, payload, user.salt());
				if (token == null) {
					throw new ServiceException("生成Token异常");
				}
				return Result.ok("登录成功", token);
			} catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
				throw new ServiceException(e.getMessage());
			}
		}
		throw new ServiceException("用户名或密码错误");
	}

	@Override
	public Result regenerateSalt() {
		final UserInfo payload = super.jwt.getPayload();
		final int row = super.tmapper.regenerateSalt(payload.userId());
		if (row == 1) {
			return Result.ok("重新生成盐值成功");
		}
		throw new ServiceException("重新生成盐值失败, row = " + row);
	}

	@Override
	public Result permission() {
		final UserInfo payload = super.jwt.getPayload();
		final List<String> permission = super.tmapper.selectUserPermission(payload.userId());
		return Result.ok(permission);
	}

	@Override
	public Result selectRoleIds(final String userId) {
		final Set<String> roleIds = super.tmapper.selectLinkById("user_role_link", "user_id", "role_id", userId);
		return Result.ok(roleIds);
	}

	@Override
	public Result editpwd(UserEditPwd info) {
		if (!info.chkPassword().equals(info.newPassword()))
			throw new ServiceException("两次新密码不一致");
		final UserInfo payload = super.jwt.getPayload();
		final boolean chk = super.tmapper.checkPwd(payload.userName(), info.oldPassword());
		if (chk) {
			final int row = super.tmapper.updatePassword(payload.userId(), info.newPassword());
			if (row != 0) {
				return Result.ok("修改密码成功");
			}
			throw new ServiceException("修改密码失败, row = " + row);
		}
		throw new ServiceException("原密码错误");
	}

	@Override
	public Result linkUserRole(final LinkInfo info) {
		int count = super.tmapper.selectCountIds(Set.of(info.id()));
		if (count == 0) {
			throw new ServiceException("链接失败, id不匹配, count = " + count);
		}
		count = super.tmapper.selectCountIds(RoleInfo.class, info.ids());
		if (count != info.ids().size()) {
			throw new ServiceException("链接失败, ids数量不匹配, count = " + count);
		}
		final int row = super.tmapper.link("user_role_link", "user_id", "role_id", info.id(), info.ids());
		if (row != 0) {
			return Result.ok("链接成功");
		}
		throw new ServiceException("链接失败, row = " + row);
	}

	@Override
	public boolean checkPermission(String permission) {
		final UserInfo payload = jwt.getPayload();
		final List<String> permissionList = super.tmapper.selectUserPermission(payload.userId());
		final Stream<String> stream = permissionList.stream();
		final Optional<String> first = stream.filter(perm -> perm.equals(permission)).findFirst();
		stream.close();
		return first.isPresent();
	}
}
