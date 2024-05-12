package cn.colonq.admin.config;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import cn.colonq.admin.service.IUserService;

@Aspect
@Component
public class AspectConfig {
	private final IUserService userService;
	private final BoundHashOperations<String, String, Object> cacheData;

	public AspectConfig(
			final IUserService userService,
			final RedisTemplate<String, Object> redisTemplate) {
		this.userService = userService;
		this.cacheData = redisTemplate.boundHashOps("cacheData");
	}

	@Pointcut("@annotation(cn.colonq.admin.config.PermissionAnnotation)")
	private void permissionPointcut() {
	}

	@Pointcut("@annotation(cn.colonq.admin.config.CacheAble)")
	private void cacheAblePointcut() {
	}

	@Pointcut("@annotation(cn.colonq.admin.config.CacheEvict)")
	private void cacheEvictPointcut() {
	}

	@Before("permissionPointcut()")
	public void check(JoinPoint point) {
		final MethodSignature methodSignature = (MethodSignature) point.getSignature();
		final Method method = methodSignature.getMethod();
		if (method == null) {
			throw new ServiceException("PermissionAspect check methodSignature.getMethod null");
		}
		final Class<? extends Object> controlCls = point.getTarget().getClass();
		final PermissionAnnotation controlAnno = controlCls.getAnnotation(PermissionAnnotation.class);
		final PermissionAnnotation methodAnno = method.getAnnotation(PermissionAnnotation.class);
		String permissionStr = methodAnno.value();
		if (controlAnno != null) {
			permissionStr = controlAnno.value() + permissionStr;
		}
		if (!this.userService.checkPermission(permissionStr)) {
			throw new ServiceException("没有权限, 请重新登录或让管理员分配对应权限");
		}
	}

	@Around("cacheAblePointcut()")
	public Object cacheAble(ProceedingJoinPoint point) throws Throwable {
		final MethodSignature methodSignature = (MethodSignature) point.getSignature();
		final Method method = methodSignature.getMethod();
		final String controlName = point.getTarget().getClass().getName();
		final String mapperCacheName = method.getAnnotation(CacheAble.class).cacheName();
		final String params = Arrays.toString(point.getArgs());
		final String cacheParam = controlName + ':' + mapperCacheName + ':' + params;
		Object value = cacheData.get(cacheParam);
		if (value == null) {
			value = point.proceed(point.getArgs());
			cacheData.put(cacheParam, value);
		}
		return value;
	}

	@After("cacheEvictPointcut()")
	public Object cacheEvict(JoinPoint point) {
		return null;
	}
}
