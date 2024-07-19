package cn.colonq.admin.utils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.colonq.admin.config.ServiceException;

@Component
public class StringUtils {
	private final Pattern humpPattern = Pattern.compile("[A-Z]");
	private final ThreadSafePool<StringBuilder> stringBuilderPool;
	private final ThreadSafePool<ObjectMapper> objectMapperPool;
	private final DateUtils dateUtils;

	public StringUtils(
			final ThreadSafePool<StringBuilder> stringBuilderPool,
			final ThreadSafePool<ObjectMapper> objectMapperPool,
			final DateUtils dateUtils) {
		this.stringBuilderPool = stringBuilderPool;
		this.objectMapperPool = objectMapperPool;
		this.dateUtils = dateUtils;
	}

	public boolean isEmpty(Object value) {
		return value == null || "".equals(value);
	}

	public <T> T redisValueToObject(String redisString, Class<? extends T> cls) {
		if (!isEmpty(redisString)) {
			String[] split = redisString.split(":", 2);
			if (split.length == 2) {
				final String type = split[0];
				final String value = split[1];
				if ("number".equals(type) && Integer.class == cls)
					return jsonToObj(value, cls);
				else if ("text".equals(type) && cls == String.class)
					return jsonToObj(value, cls);
				else if ("array".equals(type) && cls == String[].class)
					return jsonToObj(value, cls);
				else if ("object".equals(type))
					return jsonToObj(value, cls);
			}
		}
		return null;
	}

	public <T> String toJsonString(T t) {
		final ObjectMapper mapper = objectMapperPool.getItem();
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(t);
		} catch (JsonProcessingException e) {
			throw new ServiceException(e.getMessage());
		}
		objectMapperPool.putItem(mapper);
		return jsonString;
	}

	public <T> T jsonToObj(String jsonString, Class<? extends T> cls) {
		final ObjectMapper mapper = objectMapperPool.getItem();
		T value = null;
		try {
			value = mapper.readValue(jsonString, cls);
		} catch (JsonProcessingException e) {
			throw new ServiceException(e.getMessage());
		}
		objectMapperPool.putItem(mapper);
		return value;
	}

	public String humpToLine(String str) {
		final StringBuilder builder = stringBuilderPool.getItem();
		builder.setLength(0);
		final Matcher matcher = humpPattern.matcher(str);
		while (matcher.find()) {
			matcher.appendReplacement(builder, "_" + matcher.group(0).toLowerCase());
		}
		matcher.appendTail(builder);

		final String value = builder.toString();
		stringBuilderPool.putItem(builder);
		return value;
	}

	public boolean matches(String uri, Set<String> matches) {
		Stream<String> stream = matches.stream();
		List<String> matchesList = stream.collect(Collectors.toList());
		return matches(uri, matchesList, 0);
	}

	public boolean matches(String uri, List<String> matches) {
		return matches(uri, matches, 0);
	}

	public boolean matches(String uri, List<String> matches, int i) {
		if (matches == null || matches.size() == i) {
			return false;
		}
		final String matchStr = matches.get(i);
		return uri.matches(matchStr) || matches(uri, matches, i + 1);
	}

	public String objToString(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Date) {
			return dateUtils.format((Date) value);
		} else {
			return value.toString();
		}
	}
}
