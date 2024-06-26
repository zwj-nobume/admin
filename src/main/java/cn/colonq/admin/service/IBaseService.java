package cn.colonq.admin.service;

import java.util.Set;

import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.entity.Result;

public interface IBaseService<T> {

	T selectOne(final T param);

	PageList<T> selectPage(final T param, final long pageNum, final long pageSize, final String sortFlag);

	Result insert(final T param);

	Result update(final T param);

	Result delete(final Set<String> ids);
}
