package cn.colonq.admin.mapper;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.utils.DateUtils;
import cn.colonq.admin.utils.StringUtils;
import cn.colonq.admin.utils.ThreadSafePool;

@Component
public class UserMapper extends BaseMapper<UserInfo> {

	public UserMapper(
			final DateUtils dateUtils,
			final JdbcClient jdbcClient,
			final StringUtils stringUtils,
			final ThreadSafePool<StringBuilder> stringBuilderPool) {
		super(UserInfo.class, dateUtils, jdbcClient, stringUtils, stringBuilderPool);
	}

	public boolean checkPwd(final String userName, final String password) {
		String sql = "SELECT password = PASSWORD(:password) FROM user_info WHERE user_name = :userName";
		return super.jdbcClient.sql(sql)
				.param("password", password)
				.param("userName", userName)
				.query(Boolean.class).single();
	}

	public int regenerateSalt(final String userId) {
		String sql = "UPDATE user_info SET salt = SHA2(MD5(RAND()),256) WHERE user_id = '" + userId + '\'';
		return super.jdbcClient.sql(sql).update();
	}
}
