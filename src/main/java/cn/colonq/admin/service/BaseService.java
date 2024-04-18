package cn.colonq.admin.service;

import java.util.Set;

import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.entity.Result;

public interface BaseService<T> {

	PageList<T> selectPage(final T param, final long pageNum, final long pageSize);

	Result insert(final T param);

	Result update(final T param);

	Result delete(final Class<? extends T> cls, final Set<String> ids);
}
