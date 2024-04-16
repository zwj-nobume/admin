package cn.colonq.admin.mapper;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.utils.DateUtils;
import cn.colonq.admin.utils.StringUtils;
import cn.colonq.admin.utils.ThreadSafePool;

@Component
public class UserMapper extends BaseMapper<UserInfo> {

    public UserMapper(DateUtils dateUtils, JdbcClient jdbcClient, StringUtils stringUtils,
            ThreadSafePool<StringBuilder> stringBuilderPool) {
        super(dateUtils, jdbcClient, stringUtils, stringBuilderPool);
    }
}
