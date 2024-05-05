package cn.colonq.admin.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

import cn.colonq.admin.anno.Table;
import cn.colonq.admin.anno.TableField;
import cn.colonq.admin.config.CompEnum;
import cn.colonq.admin.config.ServiceException;
import cn.colonq.admin.config.TableFuncEnum;
import cn.colonq.admin.entity.PageList;
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

	public <TS> int selectCountIds(Class<? extends TS> clst, final Set<String> ids) {
		final String tableName = getTableName(clst);
		final String idName = getIdName(clst);
		final String sql = "SELECT COUNT(1) FROM " + tableName +
				" WHERE " + idName + " in ('" + String.join("','", ids) + "')";
		return jdbcClient.sql(sql).query(Integer.class).single();
	}

	public T selectOne(final String indexName, final String indexValue) {
		final String tableName = getTableName(cls);
		final StringBuilder builder = stringBuilderPool.getItem();
		builder.setLength(0);
		builder.append("SELECT ");
		Arrays.asList(cls.getDeclaredFields()).stream()
				.map(Field::getName)
				.map(str -> str + ',')
				.map(stringUtils::humpToLine)
				.forEach(builder::append);
		builder.deleteCharAt(builder.length() - 1);
		builder.append(" FROM ");
		builder.append(stringUtils.humpToLine(tableName));
		builder.append(" WHERE ");
		builder.append(indexName);
		builder.append(" = '");
		builder.append(indexValue);
		builder.append('\'');
		String sql = builder.toString();
		stringBuilderPool.putItem(builder);
		return jdbcClient.sql(sql).query(cls).single();
	}

	public PageList<T> selectPage(final T param, final long pageNum, final long pageSize) {
		final String tableName = getTableName(cls);
		final StringBuilder builder = stringBuilderPool.getItem();
		builder.setLength(0);
		builder.append("SELECT ");
		Arrays.asList(cls.getDeclaredFields()).stream()
				.filter(field -> {
					TableField anno = field.getAnnotation(TableField.class);
					return anno == null || anno.select();
				})
				.map(Field::getName)
				.map(str -> str + ',')
				.map(stringUtils::humpToLine)
				.forEach(builder::append);
		builder.deleteCharAt(builder.length() - 1);
		final int selectLength = builder.length();
		builder.append(" FROM ");
		builder.append(stringUtils.humpToLine(tableName));
		builder.append(" WHERE 1=1");

		Arrays.asList(cls.getDeclaredFields()).stream().forEach(field -> {
			TableField anno = field.getAnnotation(TableField.class);
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
				builder.append(stringUtils.humpToLine(field.getName()));
				TableField tableField = field.getAnnotation(TableField.class);
				if (tableField == null || CompEnum.eq == tableField.comp()) {
					builder.append("='");
					builder.append(valueStr);
					builder.append('\'');
				} else if (CompEnum.like == tableField.comp()) {
					builder.append(" LIKE CONCAT('%','");
					builder.append(valueStr);
					builder.append("','%')");
				} else if (CompEnum.gt == tableField.comp()) {
					builder.append(">'");
					builder.append(valueStr);
					builder.append('\'');
				} else if (CompEnum.lt == tableField.comp()) {
					builder.append("<'");
					builder.append(valueStr);
					builder.append('\'');
				} else if (CompEnum.ge == tableField.comp()) {
					builder.append(">='");
					builder.append(valueStr);
					builder.append('\'');
				} else if (CompEnum.le == tableField.comp()) {
					builder.append("<='");
					builder.append(valueStr);
					builder.append('\'');
				}
			}
			field.setAccessible(canAccess);
		});
		builder.append(" ORDER BY create_time LIMIT ");
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

	public int insert(final T param) {
		final String tableName = getTableName(cls);
		final StringBuilder builder = stringBuilderPool.getItem();
		builder.setLength(0);
		builder.append("INSERT INTO ");
		builder.append(tableName);
		builder.append(" (");

		Arrays.asList(cls.getDeclaredFields()).forEach(field -> {
			TableField anno = field.getAnnotation(TableField.class);
			if (anno != null && anno.isInsert() == false) {
				return;
			}
			final String fieldName = stringUtils.humpToLine(field.getName());
			builder.append(fieldName);
			builder.append(',');
		});
		builder.deleteCharAt(builder.length() - 1);
		builder.append(") VALUES (");
		Arrays.asList(cls.getDeclaredFields()).forEach(field -> {
			TableField anno = field.getAnnotation(TableField.class);
			if (anno != null && anno.isInsert() == false) {
				return;
			}
			boolean canAccess = field.canAccess(param);
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
			if (anno == null || anno.insert() == TableFuncEnum.def) {
				builder.append('\'');
				builder.append(valueStr);
				builder.append("',");
			} else if (anno.insert() == TableFuncEnum.pwd) {
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

	public int update(final T param) {
		final String tableName = getTableName(cls);
		final String idName = getIdName(cls);
		String idValue = null;
		final StringBuilder builder = stringBuilderPool.getItem();
		builder.setLength(0);
		builder.append("UPDATE ");
		builder.append(tableName);
		builder.append(" SET ");
		for (Field field : cls.getDeclaredFields()) {
			TableField anno = field.getAnnotation(TableField.class);
			if (anno != null && anno.isUpdate() == false) {
				continue;
			}
			boolean canAccess = field.canAccess(param);
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
			if (anno == null || anno.insert() == TableFuncEnum.def) {
				builder.append("='");
				builder.append(valueStr);
				builder.append("',");
			} else if (anno.insert() == TableFuncEnum.pwd) {
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

	public int updateParentNull(final String tableName, final String parentName, final Set<String> ids) {
		final String idStr = '\'' + String.join("','", ids) + '\'';
		final String sql = "UPDATE " + tableName + " SET " + parentName + " = NULL WHERE " + parentName + " in (:ids)";
		return jdbcClient.sql(sql).param("ids", idStr).update();
	}

	public int delete(final Set<String> ids) {
		final String idStr = '\'' + String.join("','", ids) + '\'';
		final String idName = getIdName(cls);
		final String tableName = getTableName(cls);
		final String sql = getDelSql(tableName, idName);
		return jdbcClient.sql(sql).param("ids", idStr).update();
	}

	public int deleteLink(final String tableName, final String idName, final Set<String> ids) {
		final String idStr = '\'' + String.join("','", ids) + '\'';
		final String sql = getDelSql(tableName, idName);
		return jdbcClient.sql(sql).param("ids", idStr).update();
	}

	private String getTableName(Class<?> clst) {
		final Table anno = clst.getAnnotation(Table.class);
		return anno.tableName();
	}

	private String getIdName(Class<?> clst) {
		final Table anno = clst.getAnnotation(Table.class);
		return anno.idName();
	}

	private String getDelSql(final String tableName, final String idName) {
		return "DELETE FROM " + tableName + " WHERE " + idName + " in (:ids)";
	}

	private RowMapper<T> getRowMapper() {
		return new RowMapper<T>() {
			@Override
			public T mapRow(ResultSet rs, int rowNum) throws SQLException {
				final Field[] fields = cls.getDeclaredFields();
				final Class<?>[] parameterTypes = new Class<?>[fields.length];
				final Object[] initargs = new Object[fields.length];
				for (int i = 0; i < fields.length; i++) {
					Field field = fields[i];
					parameterTypes[i] = field.getType();
					TableField anno = field.getAnnotation(TableField.class);
					if (anno != null && !anno.select()) {
						initargs[i] = null;
						continue;
					}
					if (String.class.isAssignableFrom(parameterTypes[i])) {
						initargs[i] = rs.getString(stringUtils.humpToLine(field.getName()));
					}
					if (Integer.class.isAssignableFrom(parameterTypes[i])) {
						initargs[i] = rs.getInt(stringUtils.humpToLine(field.getName()));
					}
					if (Long.class.isAssignableFrom(parameterTypes[i])) {
						initargs[i] = rs.getLong(stringUtils.humpToLine(field.getName()));
					}
					if (Date.class.isAssignableFrom(parameterTypes[i])) {
						initargs[i] = rs.getTimestamp(stringUtils.humpToLine(field.getName()));
					}
				}
				try {
					return cls.getDeclaredConstructor(parameterTypes).newInstance(initargs);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					throw new ServiceException(e.getMessage());
				}
			}
		};
	}
}
