package cn.colonq.admin.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

import cn.colonq.admin.anno.CacheAble;
import cn.colonq.admin.anno.CacheEvict;
import cn.colonq.admin.anno.Table;
import cn.colonq.admin.anno.TableField;
import cn.colonq.admin.config.ServiceException;
import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.enumcfg.CompEnum;
import cn.colonq.admin.enumcfg.LogTypeEnum;
import cn.colonq.admin.enumcfg.TableFuncEnum;
import cn.colonq.admin.utils.DateUtils;
import cn.colonq.admin.utils.StringUtils;
import cn.colonq.admin.utils.ThreadSafePool;

public class BaseMapper<T> {
	protected final Class<? extends T> cls;
	protected final DateUtils dateUtils;
	protected final JdbcClient jdbcClient;
	protected final StringUtils stringUtils;
	protected final ThreadSafePool<StringBuilder> stringBuilderPool;

	public BaseMapper(
			final Class<? extends T> cls,
			final DateUtils dateUtils,
			final JdbcClient jdbcClient,
			final StringUtils stringUtils,
			final ThreadSafePool<StringBuilder> stringBuilderPool) {
		this.cls = cls;
		this.dateUtils = dateUtils;
		this.jdbcClient = jdbcClient;
		this.stringUtils = stringUtils;
		this.stringBuilderPool = stringBuilderPool;
	}

	public int selectCountIds(final Set<String> ids) {
		return selectCountIds(cls, ids);
	}

	public <TS> int selectCountIds(final Class<? extends TS> clst, final Set<String> ids) {
		final String tableName = getTableName(clst);
		final String idName = getIdName(clst);
		final String sql = "SELECT COUNT(1) FROM " + tableName +
				" WHERE " + idName + " in ('" + String.join("','", ids) + "')";
		return jdbcClient.sql(sql).query(Integer.class).single();
	}

	@CacheAble(cacheName = "BaseMapper.selectLinkById")
	public Set<String> selectLinkById(
			final String tableName,
			final String columName,
			final String targetName,
			final String columId) {
		final String sql = "SELECT " + targetName + " FROM " + tableName
				+ " WHERE " + columName + " = '" + columId + '\'';
		return jdbcClient.sql(sql).query(String.class).set();
	}

	@CacheAble(cacheName = "BaseMapper.selectOne")
	public T selectOne(final String indexName, final String indexValue) {
		final String tableName = getTableName(cls);
		final StringBuilder builder = stringBuilderPool.getItem();
		builder.setLength(0);
		builder.append("SELECT ");
		final Stream<Field> fieldStream = Arrays.asList(cls.getDeclaredFields()).stream();
		fieldStream.map(Field::getName).map(str -> str + ',').map(stringUtils::humpToLine).forEach(builder::append);
		fieldStream.close();
		builder.deleteCharAt(builder.length() - 1);
		builder.append(" FROM ");
		builder.append(stringUtils.humpToLine(tableName));
		builder.append(" WHERE ");
		builder.append(indexName);
		builder.append(" = '");
		builder.append(indexValue);
		builder.append('\'');
		final String sql = builder.toString();
		stringBuilderPool.putItem(builder);
		final Stream<? extends T> stream = jdbcClient.sql(sql).query(cls).stream();
		final Optional<? extends T> first = stream.findFirst();
		stream.close();
		if (first.isPresent()) {
			return first.get();
		}
		return null;
	}

	@CacheAble(cacheName = "BaseMapper.selectPage")
	public PageList<T> selectPage(final T param, final long pageNum, final long pageSize, final String sortFlag) {
		final String tableName = getTableName(cls);
		final StringBuilder builder = stringBuilderPool.getItem();
		builder.setLength(0);
		builder.append("SELECT ");
		Stream<Field> stream = Arrays.asList(cls.getDeclaredFields()).stream();
		stream.filter(field -> {
			TableField anno = field.getAnnotation(TableField.class);
			return anno == null || anno.select();
		}).map(Field::getName).map(str -> str + ',').map(stringUtils::humpToLine).forEach(builder::append);
		stream.close();
		builder.deleteCharAt(builder.length() - 1);
		final int selectLength = builder.length();
		builder.append(" FROM ");
		builder.append(stringUtils.humpToLine(tableName));
		builder.append(" WHERE 1=1");

		stream = Arrays.asList(cls.getDeclaredFields()).stream();
		stream.forEach(field -> {
			final TableField anno = field.getAnnotation(TableField.class);
			if (anno != null && !anno.select()) {
				return;
			}
			final boolean canAccess = field.canAccess(param);
			field.setAccessible(true);
			String valueStr = null;
			try {
				Object value = field.get(param);
				if (value == null) {
					return;
				}
				if (value instanceof Date) {
					valueStr = dateUtils.format((Date) value);
				} else {
					valueStr = value.toString();
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new ServiceException(e.getMessage());
			}
			if (valueStr != null) {
				builder.append(" AND ");
				final String fieldName = stringUtils.humpToLine(field.getName());
				builder.append(fieldName);
				final TableField tableField = field.getAnnotation(TableField.class);
				if (tableField == null || CompEnum.EQ == tableField.comp()) {
					if ("NULL".equals(valueStr)) {
						builder.append(" IS NULL");
					} else {
						builder.append("='");
						builder.append(valueStr);
						builder.append('\'');
					}
				} else if (CompEnum.LIKE == tableField.comp()) {
					builder.append(" LIKE CONCAT('%','");
					builder.append(valueStr);
					builder.append("','%')");
				} else if (CompEnum.GT == tableField.comp()) {
					builder.append(">'");
					builder.append(valueStr);
					builder.append('\'');
				} else if (CompEnum.LT == tableField.comp()) {
					builder.append("<'");
					builder.append(valueStr);
					builder.append('\'');
				} else if (CompEnum.GE == tableField.comp()) {
					builder.append(">='");
					builder.append(valueStr);
					builder.append('\'');
				} else if (CompEnum.LE == tableField.comp()) {
					builder.append("<='");
					builder.append(valueStr);
					builder.append('\'');
				}
			}
			field.setAccessible(canAccess);
		});
		stream.close();
		builder.append(" ORDER BY " + sortFlag + " LIMIT ");
		final long offset = (pageNum - 1) * pageSize;
		builder.append(pageSize);
		if (offset != 0) {
			builder.append(" OFFSET ");
			builder.append(offset);
		}

		final String sql = builder.toString();
		builder.delete(0, selectLength);
		builder.delete(builder.lastIndexOf(" ORDER BY "), builder.length());
		builder.insert(0, "SELECT COUNT(1)");
		final String countSql = builder.toString();
		stringBuilderPool.putItem(builder);
		final long total = jdbcClient.sql(countSql).query(Long.class).single();
		List<T> list = null;
		if (total > 0) {
			list = jdbcClient.sql(sql).query(getRowMapper()).list();
		}
		return new PageList<>(pageNum, pageSize, total, list);
	}

	@CacheEvict(cacheName = { "BaseMapper.selectPage" })
	public int insert(final T param) {
		final String tableName = getTableName(cls);
		final StringBuilder builder = stringBuilderPool.getItem();
		builder.setLength(0);
		builder.append("INSERT INTO ");
		builder.append(tableName);
		builder.append(" (");

		Arrays.asList(cls.getDeclaredFields()).forEach(field -> {
			final TableField anno = field.getAnnotation(TableField.class);
			if (anno != null && !anno.isInsert()) {
				return;
			}
			final String fieldName = stringUtils.humpToLine(field.getName());
			builder.append(fieldName);
			builder.append(',');
		});
		builder.deleteCharAt(builder.length() - 1);
		builder.append(") VALUES (");
		Arrays.asList(cls.getDeclaredFields()).forEach(field -> {
			final TableField anno = field.getAnnotation(TableField.class);
			if (anno != null && !anno.isInsert()) {
				return;
			}
			final boolean canAccess = field.canAccess(param);
			field.setAccessible(true);
			String valueStr = null;
			try {
				Object value = field.get(param);
				if (value == null) {
					builder.append("NULL,");
					return;
				}
				valueStr = stringUtils.objToString(value);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new ServiceException(e.getMessage());
			}
			// 新增语句，数据库函数定制
			if (anno == null || anno.insert() == TableFuncEnum.DEF) {
				builder.append('\'');
				builder.append(valueStr);
				builder.append("',");
			} else if (anno.insert() == TableFuncEnum.PWD) {
				builder.append("PASSWORD('");
				builder.append(valueStr);
				builder.append("'),");
			}
			field.setAccessible(canAccess);
		});
		builder.deleteCharAt(builder.length() - 1);
		builder.append(')');

		final String sql = builder.toString();
		stringBuilderPool.putItem(builder);
		return jdbcClient.sql(sql).update();
	}

	/**
	 * 删除链接表内columName1的数据后重新链接
	 * 
	 * @param tableName
	 * @param columName1
	 * @param columName2
	 * @param id1
	 * @param ids2
	 * @return
	 */
	@CacheEvict(controlNames = {
			"cn.colonq.admin.mapper.UserMapper",
			"cn.colonq.admin.mapper.RoleMapper",
			"cn.colonq.admin.mapper.MenuMapper"
	}, cacheName = {
			"BaseMapper.selectLinkById",
			"UserMapper.selectUserPermission" })
	public int link(
			final String tableName,
			final String columName1,
			final String columName2,
			final String id1,
			final Set<String> ids2) {
		final StringBuilder builder = stringBuilderPool.getItem();
		builder.setLength(0);
		builder.append("INSERT INTO ");
		builder.append(tableName);
		builder.append(" (");
		builder.append(columName1);
		builder.append(',');
		builder.append(columName2);
		builder.append(") VALUES ");
		ids2.forEach(id2 -> {
			builder.append("('");
			builder.append(id1);
			builder.append("','");
			builder.append(id2);
			builder.append("'),");
		});
		builder.deleteCharAt(builder.length() - 1);

		final String sql = builder.toString();
		stringBuilderPool.putItem(builder);
		final String delSql = "DELETE FROM " + tableName + " WHERE " + columName1 + " = '" + id1 + '\'';
		jdbcClient.sql(delSql).update();
		return jdbcClient.sql(sql).update();
	}

	@CacheEvict(cacheName = { "BaseMapper.selectOne", "BaseMapper.selectPage", "UserMapper.checkPwd" })
	public int update(final T param) {
		final String tableName = getTableName(cls);
		final String idName = getIdName(cls);
		String idValue = null;
		final StringBuilder builder = stringBuilderPool.getItem();
		builder.setLength(0);
		builder.append("UPDATE ");
		builder.append(tableName);
		builder.append(" SET ");
		for (final Field field : cls.getDeclaredFields()) {
			final TableField anno = field.getAnnotation(TableField.class);
			if (anno != null && !anno.isUpdate()) {
				continue;
			}
			final boolean canAccess = field.canAccess(param);
			field.setAccessible(true);
			String valueStr = null;
			try {
				Object value = field.get(param);
				if (value == null) {
					continue;
				}
				if (value instanceof Date) {
					valueStr = dateUtils.format((Date) value);
				} else {
					valueStr = value.toString();
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new ServiceException(e.getMessage());
			}
			if (valueStr == null) {
				continue;
			}
			final String fieldName = stringUtils.humpToLine(field.getName());
			if (idName.equals(fieldName)) {
				idValue = valueStr;
				continue;
			}
			builder.append(fieldName);
			// 修改语句，数据库函数定制
			if (anno == null || anno.update() == TableFuncEnum.DEF) {
				builder.append("='");
				builder.append(valueStr);
				builder.append("',");
			} else if (anno.update() == TableFuncEnum.PWD) {
				builder.append("=");
				builder.append("PASSWORD('");
				builder.append(valueStr);
				builder.append("'),");
			}
			field.setAccessible(canAccess);
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append(" WHERE ");
		builder.append(idName);
		builder.append("='");
		builder.append(idValue);
		builder.append('\'');

		final String sql = builder.toString();
		stringBuilderPool.putItem(builder);
		return jdbcClient.sql(sql).update();
	}

	@CacheEvict(cacheName = { "BaseMapper.selectOne", "BaseMapper.selectPage", "UserMapper.checkPwd" })
	public int updateParentNull(final String tableName, final String parentName, final Set<String> ids) {
		final String idStr = '\'' + String.join("','", ids) + '\'';
		final String sql = "UPDATE " + tableName + " SET " + parentName + " = NULL WHERE " + parentName + " in ("
				+ idStr + ")";
		return jdbcClient.sql(sql).update();
	}

	@CacheEvict(cacheName = { "BaseMapper.selectOne", "BaseMapper.selectPage", "UserMapper.checkPwd" })
	public int delete(final Set<String> ids) {
		final String idName = getIdName(cls);
		final String tableName = getTableName(cls);
		return defaultDelete(tableName, idName, ids);
	}

	@CacheEvict(controlNames = {
			"cn.colonq.admin.mapper.UserMapper",
			"cn.colonq.admin.mapper.RoleMapper",
			"cn.colonq.admin.mapper.MenuMapper"
	}, cacheName = {
			"BaseMapper.selectLinkById",
			"UserMapper.selectUserPermission" })
	public int deleteLink(final String tableName, final String idName, final Set<String> ids) {
		return defaultDelete(tableName, idName, ids);
	}

	private String getTableName(final Class<?> clst) {
		final Table anno = clst.getAnnotation(Table.class);
		return anno.tableName();
	}

	private String getIdName(final Class<?> clst) {
		final Table anno = clst.getAnnotation(Table.class);
		return anno.idName();
	}

	private int defaultDelete(final String tableName, final String idName, final Set<String> ids) {
		final String idStr = '\'' + String.join("','", ids) + '\'';
		final String sql = "DELETE FROM " + tableName + " WHERE " + idName + " in (" + idStr + ")";
		return jdbcClient.sql(sql).update();
	}

	private RowMapper<T> getRowMapper() {
		return (rs, rowNum) -> {
			final Field[] fields = cls.getDeclaredFields();
			final Class<?>[] parameterTypes = new Class<?>[fields.length];
			final Object[] initargs = new Object[fields.length];
			for (int i = 0; i < fields.length; i++) {
				final Field field = fields[i];
				parameterTypes[i] = field.getType();
				final TableField anno = field.getAnnotation(TableField.class);
				if (anno != null && !anno.select()) {
					initargs[i] = null;
					continue;
				}
				final String fieldName = stringUtils.humpToLine(field.getName());
				if (String.class.isAssignableFrom(parameterTypes[i])) {
					initargs[i] = rs.getString(fieldName);
					continue;
				}
				if (Integer.class.isAssignableFrom(parameterTypes[i])) {
					initargs[i] = rs.getInt(fieldName);
					continue;
				}
				if (Long.class.isAssignableFrom(parameterTypes[i])) {
					initargs[i] = rs.getLong(fieldName);
					continue;
				}
				if (Date.class.isAssignableFrom(parameterTypes[i])) {
					initargs[i] = rs.getTimestamp(fieldName);
					continue;
				}
				if (LogTypeEnum.class.isAssignableFrom(parameterTypes[i])) {
					initargs[i] = LogTypeEnum.valueOf(rs.getString(fieldName).toUpperCase());
					continue;
				}
			}
			try {
				return cls.getDeclaredConstructor(parameterTypes).newInstance(initargs);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new ServiceException(e.getMessage());
			}
		};
	}
}
