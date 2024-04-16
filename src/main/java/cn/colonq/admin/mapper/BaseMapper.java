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
import cn.colonq.admin.config.TableFuncEnum;
import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.utils.DateUtils;
import cn.colonq.admin.utils.StringUtils;
import cn.colonq.admin.utils.ThreadSafePool;

public class BaseMapper<T> {
	protected final DateUtils dateUtils;
	protected final JdbcClient jdbcClient;
	protected final StringUtils stringUtils;
	protected final ThreadSafePool<StringBuilder> stringBuilderPool;

	public BaseMapper(
			final DateUtils dateUtils,
			final JdbcClient jdbcClient,
			final StringUtils stringUtils,
			final ThreadSafePool<StringBuilder> stringBuilderPool) {
		this.dateUtils = dateUtils;
		this.jdbcClient = jdbcClient;
		this.stringUtils = stringUtils;
		this.stringBuilderPool = stringBuilderPool;
	}

	public PageList<T> selectPage(final T param, final long pageNum, final long pageSize) {
		@SuppressWarnings("unchecked")
		Class<T> cls = (Class<T>) param.getClass();
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
			boolean canAccess = field.canAccess(param);
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
				throw new InternalError(e);
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
		long offset = (pageNum - 1) * pageSize;
		builder.append(" LIMIT ");
		builder.append(pageSize);
		if (offset != 0) {
			builder.append(" OFFSET ");
			builder.append(offset);
		}

		final String sql = builder.toString();
		builder.delete(0, selectLength);
		builder.delete(builder.lastIndexOf(" LIMIT "), builder.length());
		builder.insert(0, "SELECT COUNT(1)");
		final String countSql = builder.toString();
		stringBuilderPool.putItem(builder);
		long total = jdbcClient.sql(countSql).query(Long.class).single();
		List<T> list = null;
		if (total > 0) {
			list = jdbcClient.sql(sql).query(getRowMapper(cls)).list();
		}
		return new PageList<>(pageNum, pageSize, total, list);
	}

	public int insert(final T param) {
		@SuppressWarnings("unchecked")
		Class<T> cls = (Class<T>) param.getClass();
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
				if (value instanceof Date) {
					valueStr = dateUtils.format((Date) value);
				} else {
					valueStr = value.toString();
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new InternalError(e);
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

	public int update(final T param) {
		@SuppressWarnings("unchecked")
		Class<T> cls = (Class<T>) param.getClass();
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
				throw new InternalError(e);
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

	public int delete(Class<? extends T> cls, final Set<String> ids) {
		String tableName = getTableName(cls);
		String idName = getIdName(cls);
		final StringBuilder builder = stringBuilderPool.getItem();
		builder.setLength(0);
		builder.append("DELETE FROM ");
		builder.append(tableName);
		builder.append(" WHERE ");
		builder.append(idName);
		builder.append(" in ('");
		builder.append(String.join("','", ids));
		builder.append("')");

		final String sql = builder.toString();
		stringBuilderPool.putItem(builder);
		return jdbcClient.sql(sql).update();
	}

	private String getTableName(Class<?> cls) {
		Table anno = cls.getAnnotation(Table.class);
		return anno.tableName();
	}

	private String getIdName(Class<?> cls) {
		Table anno = cls.getAnnotation(Table.class);
		return anno.idName();
	}

	private RowMapper<T> getRowMapper(Class<T> cls) {
		return new RowMapper<T>() {
			@Override
			public T mapRow(ResultSet rs, int rowNum) throws SQLException {
				Field[] fields = cls.getDeclaredFields();
				Class<?>[] parameterTypes = new Class<?>[fields.length];
				Object[] initargs = new Object[fields.length];
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
					throw new InternalError(e);
				}
			}
		};
	}
}
