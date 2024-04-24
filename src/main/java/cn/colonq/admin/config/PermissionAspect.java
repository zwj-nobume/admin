package cn.colonq.admin.config;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import cn.colonq.admin.service.IUserService;

@Aspect
@Component
public class PermissionAspect {
    private final IUserService userService;

    public PermissionAspect(IUserService userService) {
        this.userService = userService;
    }

    @Pointcut("@annotation(cn.colonq.admin.config.PermissionAnnotation)")
    private void annotationPointcut() {
    }

    @Before("annotationPointcut()")
    public void check(JoinPoint point) {
        final MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
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
        if (!userService.checkPermission(permissionStr)) {
            throw new ServiceException("没有权限, 请重新登录或让管理员分配对应权限");
        }
    }
}
