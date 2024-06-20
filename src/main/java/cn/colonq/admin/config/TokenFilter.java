package cn.colonq.admin.config;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import cn.colonq.admin.entity.Header;
import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.mapper.UserMapper;
import cn.colonq.admin.utils.JWT;
import cn.colonq.admin.utils.StringUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@WebFilter(urlPatterns = { "/**" }, filterName = "tokenFilter")
public class TokenFilter implements Filter {
	private final GlobalExceptionHandler handler;
	private final StringUtils stringUtils;
	private final UserMapper userMapper;
	private final Set<String> openPath;
	private final JWT jwt;

	public TokenFilter(
			final GlobalExceptionHandler handler,
			final StringUtils stringUtils,
			final UserMapper userMapper,
			final JWT jwt) {
		this.handler = handler;
		this.userMapper = userMapper;
		this.stringUtils = stringUtils;
		this.jwt = jwt;
		this.openPath = new HashSet<>();
		this.openPath.add("/user/login");
		this.openPath.add("^/file/.*");
	}

	@Override
	public void doFilter(
			final ServletRequest req,
			final ServletResponse res,
			final FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;
		String uri = request.getRequestURI();
		if (!stringUtils.matches(uri, this.openPath)) {
			Header header = jwt.getHeader();
			ServiceException err;
			if (header == null) {
				err = new ServiceException(HttpStatus.UNAUTHORIZED, "登录已过期, 请重新登录");
				handler.handleServiceError(err, response);
				return;
			}
			UserInfo payload = jwt.getPayload();
			if (payload == null) {
				err = new ServiceException(HttpStatus.UNAUTHORIZED, "登录已过期, 请重新登录");
				handler.handleServiceError(err, response);
				return;
			}
			UserInfo user = this.userMapper.selectOne("user_name", payload.userName());
			if (user == null) {
				err = new ServiceException(HttpStatus.UNAUTHORIZED, "用户信息错误, 请重新登录");
				handler.handleServiceError(err, response);
				return;
			}
			try {
				String token = jwt.generateToken(header, payload, user.salt());
				if (token == null) {
					err = new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "生成Token异常");
					handler.handleServiceError(err, response);
					return;
				}
				if (!token.equals(jwt.getToken())) {
					err = new ServiceException(HttpStatus.UNAUTHORIZED, "登录已过期, 请重新登录");
					handler.handleServiceError(err, response);
					return;
				}
			} catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
				err = new ServiceException(e.getMessage());
				handler.handleServiceError(err, response);
				return;
			}
		}
		chain.doFilter(request, response);
	}
}
