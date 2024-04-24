package cn.colonq.admin.control;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.RoleInfo;
import cn.colonq.admin.service.IRoleService;

@RestController
@RequestMapping("/role")
public class RoleController extends BaseController<RoleInfo, IRoleService> {

	public RoleController(final IRoleService roleService) {
		super(roleService, RoleInfo.class);
	}

	@PostMapping("/linkRM")
	public Result linkRM(@RequestBody LinkInfo info) {
		return super.tService.linkRoleMenu(info);
	}

	@PostMapping("/linkUR")
	public Result linkUR(@RequestBody LinkInfo info) {
		return super.tService.linkUserRole(info);
	}
}
