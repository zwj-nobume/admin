package cn.colonq.admin.mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import cn.colonq.admin.anno.CacheAble;
import cn.colonq.admin.anno.CacheEvict;
import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.utils.DateUtils;
import cn.colonq.admin.utils.StringUtils;
import cn.colonq.admin.utils.ThreadSafePool;

@Component(value = "userMapper")
public class UserMapper extends BaseMapper<UserInfo> {

	public UserMapper(
			final DateUtils dateUtils,
			final JdbcClient jdbcClient,
			final StringUtils stringUtils,
			final ThreadSafePool<StringBuilder> stringBuilderPool) {
		super(UserInfo.class, dateUtils, jdbcClient, stringUtils, stringBuilderPool);
	}

	public boolean checkPwd(final String userName, final String password) {
		final String sql = "SELECT password = PASSWORD(:password) FROM user_info WHERE user_name = :userName";
		final Stream<Boolean> stream = super.jdbcClient.sql(sql)
				.param("password", password)
				.param("userName", userName)
				.query(Boolean.class).stream();
		final Optional<Boolean> first = stream.findFirst();
		stream.close();
		return first.isPresent() && first.get();
	}

	@CacheEvict(cacheName = { "BaseMapper.selectOne" })
	public int regenerateSalt(final String userId) {
		final String sql = "UPDATE user_info SET salt = SHA2(MD5(RAND()),256) WHERE user_id = '" + userId + '\'';
		return super.jdbcClient.sql(sql).update();
	}

	@CacheAble(cacheName = "UserMapper.selectUserPermission")
	public List<String> selectUserPermission(final String userId) {
		final String sql = """
				SELECT mi.permission FROM menu_info mi
				LEFT JOIN role_menu_link rml ON rml.menu_id = mi.menu_id
				LEFT JOIN user_role_link url ON url.role_id = rml.role_id
				WHERE url.user_id = :userId
					""";
		return super.jdbcClient.sql(sql).param("userId", userId).query(String.class).list();
	}

	@CacheEvict(cacheName = { "BaseMapper.selectOne" })
	public int updatePassword(final String userId, final String newPassword) {
		final String sql = "UPDATE user_info SET password = PASSWORD(:newPassword) WHERE user_id = :userId";
		return super.jdbcClient.sql(sql)
				.param("newPassword", newPassword)
				.param("userId", userId).update();
	}
}
