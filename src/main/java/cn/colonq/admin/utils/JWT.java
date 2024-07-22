package cn.colonq.admin.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import cn.colonq.admin.entity.Header;
import cn.colonq.admin.entity.UserInfo;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JWT {
	private final ThreadSafePool<StringBuilder> stringBuilderPool;
	private final ThreadSafePool<Encoder> base64EncoderPool;
	private final ThreadSafePool<Decoder> base64DecoderPool;
	private final ThreadSafePool<Mac> hmacSHA256Pool;
	private final HttpServletRequest request;
	private final StringUtils stringUtils;

	public JWT(
			final ThreadSafePool<StringBuilder> stringBuilderPool,
			final ThreadSafePool<Encoder> base64EncoderPool,
			final ThreadSafePool<Decoder> base64DecoderPool,
			final ThreadSafePool<Mac> hmacSHA256Pool,
			final HttpServletRequest request,
			final StringUtils stringUtils) {
		this.stringBuilderPool = stringBuilderPool;
		this.base64EncoderPool = base64EncoderPool;
		this.base64DecoderPool = base64DecoderPool;
		this.hmacSHA256Pool = hmacSHA256Pool;
		this.stringUtils = stringUtils;
		this.request = request;
	}

	public String getToken() {
		return request.getHeader("Authorization");
	}

	public String getField(int index) {
		if (index < 0 || index > 2) {
			return null;
		}
		final String token = getToken();
		if (stringUtils.isEmpty(token)) {
			return null;
		}
		String[] arr = token.split("\\.");
		if (arr.length != 3) {
			return null;
		}
		return arr[index];
	}

	public Header getHeader() {
		final String headerBase64 = getField(0);
		if (headerBase64 == null) {
			return null;
		}
		final Decoder decoder = base64DecoderPool.getItem();
		final String headerStr = new String(decoder.decode(headerBase64));
		base64DecoderPool.putItem(decoder);
		return stringUtils.jsonToObj(headerStr, Header.class);
	}

	public UserInfo getPayload() {
		final String payloadBase64 = getField(1);
		if (payloadBase64 == null) {
			return null;
		}
		final Decoder decoder = base64DecoderPool.getItem();
		final String payloadStr = new String(decoder.decode(payloadBase64));
		base64DecoderPool.putItem(decoder);
		return stringUtils.jsonToObj(payloadStr, UserInfo.class);
	}

	public String generateToken(final Header header, final UserInfo payload, final String salt)
			throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		final String headerJson = stringUtils.toJsonString(header);
		final String payloadJson = stringUtils.toJsonString(payload);
		final Encoder encoder = base64EncoderPool.getItem();
		final String headerBase64 = encoder.encodeToString(headerJson.getBytes());
		final String payloadBase64 = encoder.encodeToString(payloadJson.getBytes());
		SecretKeySpec sks = new SecretKeySpec(salt.getBytes(), header.alg());
		Mac mac = null;
		if ("HmacSHA256".equals(header.alg())) {
			mac = hmacSHA256Pool.getItem();
		}
		if (mac == null) {
			return null;
		}
		mac.init(sks);
		final byte[] array = mac.doFinal((headerBase64 + '.' + payloadBase64).getBytes());
		final StringBuilder builder = stringBuilderPool.getItem();
		builder.setLength(0);
		for (final byte item : array) {
			builder.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
		}
		base64EncoderPool.putItem(encoder);
		if ("HmacSHA256".equals(header.alg())) {
			hmacSHA256Pool.putItem(mac);
		}
		final String signature = encoder.encodeToString(builder.toString().getBytes());
		stringBuilderPool.putItem(builder);
		return headerBase64 + '.' + payloadBase64 + '.' + signature;
	}
}
