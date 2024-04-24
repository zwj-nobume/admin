package cn.colonq.admin.utils;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.colonq.admin.config.ServiceException;

@Component
public class StringUtils {
	private final Pattern humpPattern = Pattern.compile("[A-Z]");
	private final ThreadSafePool<StringBuilder> stringBuilderPool;
	private final ThreadSafePool<ObjectMapper> objectMapperPool;

	public StringUtils(
			final ThreadSafePool<StringBuilder> stringBuilderPool,
			final ThreadSafePool<ObjectMapper> objectMapperPool) {
		this.stringBuilderPool = stringBuilderPool;
		this.objectMapperPool = objectMapperPool;
	}

	public boolean isEmpty(Object value) {
		return value == null || "".equals(value);
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
		List<String> matchesList = matches.stream().collect(Collectors.toList());
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
}
