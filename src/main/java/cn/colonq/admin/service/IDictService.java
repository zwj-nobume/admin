package cn.colonq.admin.service;

import java.util.Set;

import cn.colonq.admin.entity.DictInfo;
import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.entity.Result;

public interface IDictService {

	PageList<String> selectPage(final String key, final Long pageNum, final Long pageSize);

	Result selectValue(final String key);

	<T> T selectValue(final String key, final T defaultValue, final Class<? extends T> cls);

	Result selectValue(final Set<String> keys);

	Result insertDictInfo(final DictInfo info);

	Result updateDictInfo(final DictInfo info);

	Result deleteDictInfo(final Set<String> keys);
}
