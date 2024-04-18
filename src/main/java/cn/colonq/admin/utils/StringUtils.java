package cn.colonq.admin.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class StringUtils {
	private final Pattern humpPattern = Pattern.compile("[A-Z]");
	private final ThreadSafePool<StringBuilder> stringBuilderPool;

	public StringUtils(ThreadSafePool<StringBuilder> stringBuilderPool) {
		this.stringBuilderPool = stringBuilderPool;
	}

	public boolean isEmpty(Object value) {
		return value == null || "".equals(value);
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
}
