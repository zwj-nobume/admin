package cn.colonq.admin.mapper;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import cn.colonq.admin.entity.MenuInfo;
import cn.colonq.admin.utils.DateUtils;
import cn.colonq.admin.utils.StringUtils;
import cn.colonq.admin.utils.ThreadSafePool;

@Component
public class MenuMapper extends BaseMapper<MenuInfo> {

    public MenuMapper(
            final DateUtils dateUtils,
            final JdbcClient jdbcClient,
            final StringUtils stringUtils,
            final ThreadSafePool<StringBuilder> stringBuilderPool) {
        super(dateUtils, jdbcClient, stringUtils, stringBuilderPool);
    }
}
