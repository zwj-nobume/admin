package cn.colonq.admin.control;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.colonq.admin.anno.PermissionAnnotation;
import cn.colonq.admin.anno.RecordLog;
import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.RoleInfo;
import cn.colonq.admin.enumcfg.LogTypeEnum;
import cn.colonq.admin.service.IRoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/role")
@PermissionAnnotation("system:role")
public class RoleController extends BaseController<RoleInfo, IRoleService> {

	public RoleController(final IRoleService roleService) {
		super(roleService);
	}

	@GetMapping("/userIds")
	@PermissionAnnotation(":query")
	public Result userIds(@NotBlank(message = "角色ID不得为空") final String roleId) {
		return super.tService.selectUserIds(roleId);
	}

	@GetMapping("/menuIds")
	@PermissionAnnotation(":query")
	public Result menuIds(@NotBlank(message = "角色ID不得为空") final String roleId) {
		return super.tService.selectMenuIds(roleId);
	}

	@PostMapping("/linkUR")
	@PermissionAnnotation(":edit")
	@RecordLog(type = LogTypeEnum.EDIT)
	public Result linkUR(@Valid @RequestBody final LinkInfo info) {
		return super.tService.linkUserRole(info);
	}

	@PostMapping("/linkRM")
	@PermissionAnnotation(":edit")
	@RecordLog(type = LogTypeEnum.EDIT)
	public Result linkRM(@Valid @RequestBody final LinkInfo info) {
		return super.tService.linkRoleMenu(info);
	}
}
