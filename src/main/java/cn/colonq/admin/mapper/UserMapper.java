package cn.colonq.admin.mapper;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.utils.StringUtils;
import cn.colonq.admin.utils.ThreadSafePool;

@Component
public class UserMapper extends BaseMapper<UserInfo> {

    public UserMapper(
            final JdbcClient jdbcClient,
            final StringUtils stringUtils,
            final ThreadSafePool<StringBuilder> stringBuilderPool) {
        super(jdbcClient, stringUtils, stringBuilderPool);
    }
}
