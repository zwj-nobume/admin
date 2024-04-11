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
import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.utils.StringUtils;
import cn.colonq.admin.utils.ThreadSafePool;

public class BaseMapper<T> {
	protected final JdbcClient jdbcClient;
	protected final StringUtils stringUtils;
	protected final ThreadSafePool<StringBuilder> stringBuilderPool;

	public BaseMapper(
			final JdbcClient jdbcClient,
			final StringUtils stringUtils,
			final ThreadSafePool<StringBuilder> stringBuilderPool) {
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
			Object value = null;
			try {
				value = field.get(param);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new InternalError(e);
			}
			if (value != null) {
				builder.append(" AND ");
				builder.append(stringUtils.humpToLine(field.getName()));
				TableField tableField = field.getAnnotation(TableField.class);
				if (tableField == null || CompEnum.eq == tableField.comp()) {
					builder.append("='");
					builder.append(value.toString());
					builder.append('\'');
				} else if (CompEnum.like == tableField.comp()) {
					builder.append(" LIKE CONCAT('%','");
					builder.append(value.toString());
					builder.append("','%')");
				} else if (CompEnum.gt == tableField.comp()) {
					builder.append("&gt;'");
					builder.append(value.toString());
					builder.append('\'');
				} else if (CompEnum.lt == tableField.comp()) {
					builder.append("&lt;'");
					builder.append(value.toString());
					builder.append('\'');
				} else if (CompEnum.ge == tableField.comp()) {
					builder.append("&gt;='");
					builder.append(value.toString());
					builder.append('\'');
				} else if (CompEnum.le == tableField.comp()) {
					builder.append("&lt;='");
					builder.append(value.toString());
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
		builder.insert(0, "SELECT COUNT(1)");
		final String countSql = builder.toString();
		stringBuilderPool.putItem(builder);
		long total = jdbcClient.sql(countSql).query(Long.class).single();
		List<T> list = jdbcClient.sql(sql).query(getRowMapper(cls)).list();
		return new PageList<>(pageNum, pageSize, total, list);
	}

	public int insert(final T param) {
		@SuppressWarnings("unchecked")
		Class<T> cls = (Class<T>) param.getClass();
		final String tableName = getTableName(cls);
		// final String idName = getIdName(cls);
		final StringBuilder builder = stringBuilderPool.getItem();
		builder.setLength(0);
		builder.append("INSERT INTO ");
		builder.append(tableName);
		builder.append(" (");

		final String sql = builder.toString();
		return jdbcClient.sql(sql).update();
	}

	public int update(final T param) {
		return 0;
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
		builder.append("in ('");
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
