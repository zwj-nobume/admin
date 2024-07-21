package cn.colonq.admin.control;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.colonq.admin.anno.PermissionAnnotation;
import cn.colonq.admin.anno.RecordLog;
import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.enumcfg.LogTypeEnum;
import cn.colonq.admin.group.Login;
import cn.colonq.admin.service.IUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/user")
@PermissionAnnotation("system:user")
public class UserController extends BaseController<UserInfo, IUserService> {

	public UserController(final IUserService userService) {
		super(userService);
	}

	@PostMapping("/login")
	@RecordLog(type = LogTypeEnum.LOGIN)
	public Result login(@Validated(value = { Login.class }) @RequestBody UserInfo info) {
		return super.tService.login(info);
	}

	/**
	 * 重置当前登录用户的盐值
	 * 
	 * @return
	 */
	@PostMapping("/salt")
	@RecordLog(type = LogTypeEnum.EDIT)
	public Result salt() {
		return super.tService.regenerateSalt();
	}

	/**
	 * 获取当前登录用户的权限
	 * 
	 * @return
	 */
	@GetMapping("/permission")
	public Result permission() {
		return super.tService.permission();
	}

	@GetMapping("/roleIds")
	@PermissionAnnotation(":query")
	public Result roleIds(@NotBlank(message = "用户ID不得为空") String userId) {
		return super.tService.selectRoleIds(userId);
	}

	@PostMapping("/link")
	@PermissionAnnotation(":edit")
	@RecordLog(type = LogTypeEnum.EDIT)
	public Result link(@Valid @RequestBody LinkInfo info) {
		return super.tService.linkUserRole(info);
	}
}
