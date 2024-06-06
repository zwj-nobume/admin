package cn.colonq.admin.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import cn.colonq.admin.anno.Table;
import cn.colonq.admin.anno.TableField;
import cn.colonq.admin.config.ServiceException;
import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.mapper.BaseMapper;
import cn.colonq.admin.service.BaseService;
import cn.colonq.admin.utils.JWT;
import cn.colonq.admin.utils.StringUtils;

public class BaseServiceImpl<T, Tmapper extends BaseMapper<T>> implements BaseService<T> {
	protected final StringUtils stringUtils;
	protected final Class<? extends T> cls;
	protected final Tmapper tmapper;
	protected final JWT jwt;

	public BaseServiceImpl(
			final StringUtils stringUtils,
			final Class<? extends T> cls,
			final Tmapper tmapper,
			final JWT jwt) {
		this.stringUtils = stringUtils;
		this.cls = cls;
		this.tmapper = tmapper;
		this.jwt = jwt;
	}

	@Override
	public T selectOne(T param) {
		PageList<T> page = tmapper.selectPage(param, 1, 1, "create_time");
		if (page != null && page.data() != null && page.data().size() >= 1) {
			return page.data().get(0);
		}
		throw new ServiceException("查询不到匹配数据");
	}

	@Override
	public PageList<T> selectPage(final T param, final long pageNum, final long pageSize, final String sortFlag) {
		return tmapper.selectPage(param, pageNum, pageSize, sortFlag);
	}

	@Override
	public Result insert(final T param) {
		final Field[] fields = cls.getDeclaredFields();
		final Class<?>[] parameterTypes = new Class<?>[fields.length];
		final Object[] initargs = new Object[fields.length];
		for (int i = 0; i < fields.length; i++) {
			final Field field = fields[i];
			parameterTypes[i] = field.getType();
			if ("createName".equals(field.getName())) {
				UserInfo payload = jwt.getPayload();
				initargs[i] = payload.userName();
				continue;
			}
			final TableField anno = field.getAnnotation(TableField.class);
			if (anno != null && !anno.isInsert()) {
				initargs[i] = null;
				continue;
			}
			final boolean canAccess = field.canAccess(param);
			field.setAccessible(true);
			Object value = null;
			try {
				value = field.get(param);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new ServiceException(e.getMessage());
			}
			if (anno != null && anno.parent() && value != null) {
				int count = tmapper.selectCountIds(Set.of(stringUtils.objToString(value)));
				if (count == 0) {
					throw new ServiceException("查询不到此父级ID");
				}
			}
			initargs[i] = value;
			field.setAccessible(canAccess);
		}
		T insertParam = null;
		try {
			insertParam = cls.getDeclaredConstructor(parameterTypes).newInstance(initargs);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new ServiceException(e.getMessage());
		}
		int row = tmapper.insert(insertParam);
		if (row == 1) {
			return Result.ok("新增成功");
		}
		throw new ServiceException("新增失败, row = " + row);
	}

	@Override
	public Result update(final T param) {
		final Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			final Field field = fields[i];
			final TableField anno = field.getAnnotation(TableField.class);
			final boolean canAccess = field.canAccess(param);
			field.setAccessible(true);
			Object value = null;
			try {
				value = field.get(param);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new ServiceException(e.getMessage());
			}
			if (anno != null && anno.parent() && value != null) {
				int count = tmapper.selectCountIds(Set.of(stringUtils.objToString(value)));
				if (count == 0) {
					throw new ServiceException("查询不到此父级ID");
				}
			}
			field.setAccessible(canAccess);
		}
		int row = tmapper.update(param);
		if (row == 1) {
			return Result.ok("修改成功");
		}
		throw new ServiceException("修改失败, row = " + row);
	}

	@Override
	public Result delete(final Set<String> ids) {
		int row = tmapper.delete(ids);
		if (row > 0) {
			final Table anno = cls.getAnnotation(Table.class);
			for (Field field : cls.getDeclaredFields()) {
				final TableField fieldAnno = field.getAnnotation(TableField.class);
				if (fieldAnno != null && fieldAnno.parent()) {
					final String tableName = anno.tableName();
					final String parentName = stringUtils.humpToLine(field.getName());
					tmapper.updateParentNull(tableName, parentName, ids);
				}
			}
			final String idName = anno.idName();
			for (String tableName : anno.linkTable()) {
				tmapper.deleteLink(tableName, idName, ids);
			}
			return Result.ok("删除成功");
		}
		throw new ServiceException("删除失败, row = " + row);
	}
}
