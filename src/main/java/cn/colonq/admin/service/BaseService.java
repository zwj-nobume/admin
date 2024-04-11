package cn.colonq.admin.service;

import java.util.Set;

import cn.colonq.admin.entity.PageList;

public interface BaseService<T> {

    PageList<T> selectPage(final T param, final long pageNum, final long pageSize);

    int insert(final T param);

    int update(final T param);

    int delete(final Set<String> ids);
}
