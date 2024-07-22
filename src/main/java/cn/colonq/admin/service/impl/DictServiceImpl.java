package cn.colonq.admin.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.colonq.admin.config.ServiceException;
import cn.colonq.admin.entity.DictInfo;
import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.service.IDictService;
import cn.colonq.admin.utils.StringUtils;

@Service(value = "dictService")
public class DictServiceImpl implements IDictService {
	private final StringUtils stringUtils;
	private final BoundHashOperations<String, String, String> dictData;

	public DictServiceImpl(final StringUtils stringUtils, final RedisTemplate<String, Object> redisTemplate) {
		this.stringUtils = stringUtils;
		this.dictData = redisTemplate.boundHashOps("dictData");
	}

	@Override
	public PageList<String> selectPage(final String key, final Long pageNum, final Long pageSize) {
		final long start = pageSize * (pageNum - 1);
		final Set<String> keySet = this.dictData.keys();
		final List<String> data = keySet.stream()
				.filter(item -> item.indexOf(key) != -1)
				.sorted().skip(start).limit(pageSize)
				.collect(Collectors.toList());
		return new PageList<>(pageNum, pageSize, keySet.size(), data);
	}

	@Override
	public Result selectValue(final String key) {
		final String value = this.dictData.get(key);
		return Result.ok("获取字典值成功", value);
	}

	@Override
	public <T> T selectValue(final String key, final T defaultValue, final Class<? extends T> cls) {
		final String value = this.dictData.get(key);
		if (!stringUtils.isEmpty(value)) {
			final T returnValue = stringUtils.redisValueToObject(value, cls);
			return returnValue == null ? defaultValue : returnValue;
		}
		return defaultValue;
	}

	@Override
	public Result selectValue(final Set<String> keys) {
		final Set<String> keySet = this.dictData.keys();
		final Map<String, String> data = keySet.stream().filter(keys::contains).sorted()
				.collect(Collectors.toMap(String::toString, this.dictData::get));
		return Result.ok("获取字典列表成功", data);
	}

	@Override
	public Result insertDictInfo(final DictInfo info) {
		if (this.dictData.hasKey(info.key())) {
			throw new ServiceException("字典值已存在");
		}
		this.dictData.put(info.key(), info.value());
		return Result.ok("存入字典成功");
	}

	@Override
	public Result updateDictInfo(final DictInfo info) {
		if (!this.dictData.hasKey(info.key())) {
			throw new ServiceException("字典KEY不存在");
		}
		this.dictData.put(info.key(), info.value());
		return Result.ok("修改字典值成功");
	}

	@Override
	public Result deleteDictInfo(final Set<String> keys) {
		if (keys == null || keys.size() == 0) {
			throw new ServiceException("需删除的字典KEY不得为空");
		}
		final Object[] array = keys.stream().toArray();
		final long row = this.dictData.delete(array);
		if (row == 0) {
			throw new ServiceException("删除字典值失败, row = " + row);
		}
		return Result.ok("删除字典值成功, row = " + row);
	}
}
