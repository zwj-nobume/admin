package cn.colonq.admin.mapper;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import cn.colonq.admin.entity.LogInfo;
import cn.colonq.admin.utils.DateUtils;
import cn.colonq.admin.utils.StringUtils;
import cn.colonq.admin.utils.ThreadSafePool;

@Component(value = "logMapper")
public class LogMapper extends BaseMapper<LogInfo> {

	public LogMapper(
			final DateUtils dateUtils,
			final JdbcClient jdbcClient,
			final StringUtils stringUtils,
			final ThreadSafePool<StringBuilder> stringBuilderPool) {
		super(LogInfo.class, dateUtils, jdbcClient, stringUtils, stringBuilderPool);
	}

}
