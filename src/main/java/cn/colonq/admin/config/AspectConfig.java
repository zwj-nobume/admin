package cn.colonq.admin.config;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

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

import cn.colonq.admin.anno.CacheAble;
import cn.colonq.admin.anno.CacheEvict;
import cn.colonq.admin.anno.PermissionAnnotation;
import cn.colonq.admin.anno.RecordLog;
import cn.colonq.admin.entity.LogInfo;
import cn.colonq.admin.enumcfg.LogTypeEnum;
import cn.colonq.admin.service.ILogService;
import cn.colonq.admin.service.IUserService;
import cn.colonq.admin.utils.DateUtils;
import cn.colonq.admin.utils.StringUtils;

@Aspect
@Component
public class AspectConfig {
	private final DateUtils dateUtils;
	private final StringUtils stringUtils;
	private final IUserService userService;
	private final ILogService logService;
	private final BoundHashOperations<String, String, Object> cacheData;

	public AspectConfig(
			final DateUtils dateUtils,
			final StringUtils stringUtils,
			final ILogService logService,
			final IUserService userService,
			final RedisTemplate<String, Object> redisTemplate) {
		this.dateUtils = dateUtils;
		this.stringUtils = stringUtils;
		this.userService = userService;
		this.logService = logService;
		this.cacheData = redisTemplate.boundHashOps("cacheData");
	}

	@Pointcut("@annotation(cn.colonq.admin.anno.PermissionAnnotation)")
	private void permissionPointcut() {
	}

	@Pointcut("@annotation(cn.colonq.admin.anno.CacheAble)")
	private void cacheAblePointcut() {
	}

	@Pointcut("@annotation(cn.colonq.admin.anno.CacheEvict)")
	private void cacheEvictPointcut() {
	}

	@Pointcut("@annotation(cn.colonq.admin.anno.RecordLog)")
	private void recordLogPointcut() {
	}

	@Before("permissionPointcut()")
	public void check(final JoinPoint point) {
		final MethodSignature methodSignature = (MethodSignature) point.getSignature();
		final Method method = methodSignature.getMethod();
		if (method == null) {
			throw new ServiceException("Permission check methodSignature.getMethod null");
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
	public Object cacheAble(final ProceedingJoinPoint point) throws Throwable {
		final MethodSignature methodSignature = (MethodSignature) point.getSignature();
		final Method method = methodSignature.getMethod();
		if (method == null) {
			throw new ServiceException("CacheAble methodSignature.getMethod null");
		}
		final String controlName = point.getTarget().getClass().getName();
		final String cacheName = method.getAnnotation(CacheAble.class).cacheName();
		final String params = Arrays.toString(point.getArgs());
		final String cacheParam = controlName + ':' + cacheName + ':' + params;
		Object value = cacheData.get(cacheParam);
		if (value == null) {
			value = point.proceed(point.getArgs());
			cacheData.put(cacheParam, value);
		}
		return value;
	}

	@After("cacheEvictPointcut()")
	public void cacheEvict(final JoinPoint point) {
		final MethodSignature methodSignature = (MethodSignature) point.getSignature();
		final Method method = methodSignature.getMethod();
		if (method == null) {
			throw new ServiceException("CacheEvict methodSignature.getMethod null");
		}
		final String defaultControlName = point.getTarget().getClass().getName();
		final CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
		for (final String cacheName : cacheEvict.cacheName()) {
			if (cacheEvict.controlNames().length == 0) {
				final String cacheStart = defaultControlName + ':' + cacheName;
				deleteCache(cacheStart);
			} else {
				for (final String controlName : cacheEvict.controlNames()) {
					final String cacheStart = controlName + ':' + cacheName;
					deleteCache(cacheStart);
				}
			}
		}
	}

	@After("recordLogPointcut()")
	public void recordLog(final JoinPoint point) {
		final MethodSignature methodSignature = (MethodSignature) point.getSignature();
		final Method method = methodSignature.getMethod();
		if (method == null) {
			throw new ServiceException("RecordLog methodSignature.getMethod null");
		}
		final RecordLog recordLog = method.getAnnotation(RecordLog.class);
		final String nowDateFormat = dateUtils.format(dateUtils.getNowDate());
		final String controlName = point.getTarget().getClass().getName();
		final String methodName = method.getName();
		final LogTypeEnum logType = recordLog.type();
		final String apiParams = LogTypeEnum.LOGIN.equals(logType) ? null
				: stringUtils.toJsonString(point.getArgs()).replaceAll("\\\\", "\\\\\\\\");
		final String logIntro = String.format("%s -- Controller: %s; Method: %s; LogType: %s;", nowDateFormat,
				controlName, methodName, logType.toString());
		final LogInfo logInfo = new LogInfo(null, logType, apiParams, logIntro, null, null, null);
		logService.insert(logInfo);
	}

	/**
	 * 删除以字符串开头的缓存
	 * 
	 * @param cacheStart
	 */
	private void deleteCache(final String cacheStart) {
		final Set<String> keys = cacheData.keys();
		if (keys == null) {
			return;
		}
		final Stream<String> stream = keys.stream();
		Object[] arr = stream.filter(key -> key.startsWith(cacheStart)).toArray();
		if (arr.length != 0) {
			cacheData.delete(arr);
		}
		stream.close();
	}
}
