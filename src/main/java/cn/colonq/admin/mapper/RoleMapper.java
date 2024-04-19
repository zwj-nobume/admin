package cn.colonq.admin.mapper;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import cn.colonq.admin.entity.RoleInfo;
import cn.colonq.admin.utils.DateUtils;
import cn.colonq.admin.utils.StringUtils;
import cn.colonq.admin.utils.ThreadSafePool;

@Component
public class RoleMapper extends BaseMapper<RoleInfo> {

	public RoleMapper(
			final DateUtils dateUtils,
			final JdbcClient jdbcClient,
			final StringUtils stringUtils,
			final ThreadSafePool<StringBuilder> stringBuilderPool) {
		super(RoleInfo.class, dateUtils, jdbcClient, stringUtils, stringBuilderPool);
	}
}
