package cn.colonq.admin.service;

import java.util.Set;

import cn.colonq.admin.entity.DictInfo;
import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.entity.Result;

public interface IDictService {

	PageList<String> selectPage(String key, Long pageNum, Long pageSize);

	Result selectValue(String key);

	<T> T selectValue(String key, T defaultValue, Class<? extends T> cls);

	Result insertDictInfo(DictInfo info);

	Result updateDictInfo(DictInfo info);

	Result deleteDictInfo(Set<String> keys);
}
