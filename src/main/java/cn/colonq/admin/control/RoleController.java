package cn.colonq.admin.control;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.colonq.admin.entity.RoleInfo;
import cn.colonq.admin.service.IRoleService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/role")
public class RoleController extends BaseController<RoleInfo, IRoleService> {

	public RoleController(
			final HttpServletRequest request,
			final IRoleService tService) {
		super(request, tService);
	}
}
