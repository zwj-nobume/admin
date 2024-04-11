package cn.colonq.admin.config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.LinkedList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import cn.colonq.admin.utils.ThreadSafePool;

@Configuration
public class ThreadSafePoolConfig {
	private final int maxThread;

	public ThreadSafePoolConfig() {
		this.maxThread = Runtime.getRuntime().availableProcessors();
	}

	@Bean(name = "stringBuilderPool")
	public ThreadSafePool<StringBuilder> stringBuilderPool() {
		return new ThreadSafePool<>(StringBuilder.class);
	}

	@Bean(name = "sha256DigestPool")
	public ThreadSafePool<MessageDigest> sha256DigestPool() {
		final LinkedList<MessageDigest> linked = new LinkedList<>();
		for (int i = 0; i < this.maxThread; i++) {
			try {
				final MessageDigest digest = MessageDigest.getInstance("SHA-256");
				linked.add(digest);
			} catch (NoSuchAlgorithmException e) {
				throw new InternalError(e);
			}
		}
		return new ThreadSafePool<>(linked);
	}

	@Bean(name = "calendarPool")
	public ThreadSafePool<Calendar> calendarPool() {
		final LinkedList<Calendar> linked = new LinkedList<>();
		for (int i = 0; i < this.maxThread; i++) {
			linked.add(Calendar.getInstance());
		}
		return new ThreadSafePool<>(linked);
	}

	@Bean(name = "restTemplatePool")
	public ThreadSafePool<RestTemplate> restTemplatePool() {
		final LinkedList<RestTemplate> linked = new LinkedList<>();
		for (int i = 0; i < this.maxThread; i++) {
			final RestTemplate restTemplate = new RestTemplate();
			final MappingJackson2HttpMessageConverter mj2hmc = new MappingJackson2HttpMessageConverter();
			mj2hmc.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_HTML, MediaType.TEXT_PLAIN));
			restTemplate.getMessageConverters().add(mj2hmc);
			linked.add(restTemplate);
		}
		return new ThreadSafePool<>(linked);
	}

	@Bean(name = "base64EncoderPool")
	public ThreadSafePool<Encoder> base64EncoderPool() {
		final LinkedList<Encoder> linked = new LinkedList<>();
		for (int i = 0; i < this.maxThread; i++) {
			linked.add(Base64.getEncoder());
		}
		return new ThreadSafePool<>(linked);
	}

	@Bean(name = "base64DecoderPool")
	public ThreadSafePool<Decoder> base64DecoderPool() {
		final LinkedList<Decoder> linked = new LinkedList<>();
		for (int i = 0; i < this.maxThread; i++) {
			linked.add(Base64.getDecoder());
		}
		return new ThreadSafePool<>(linked);
	}
}
