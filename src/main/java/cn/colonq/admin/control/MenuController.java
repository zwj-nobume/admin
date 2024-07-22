package cn.colonq.admin.control;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.colonq.admin.anno.PermissionAnnotation;
import cn.colonq.admin.anno.RecordLog;
import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.MenuInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.enumcfg.LogTypeEnum;
import cn.colonq.admin.service.IMenuService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/menu")
@PermissionAnnotation("system:menu")
public class MenuController extends BaseController<MenuInfo, IMenuService> {

	public MenuController(final IMenuService menuService) {
		super(menuService);
	}

	@GetMapping("/roleIds")
	@PermissionAnnotation(":query")
	public Result roleIds(@NotBlank(message = "菜单ID不得为空") final String menuId) {
		return super.tService.selectRoleIds(menuId);
	}

	@PostMapping("/link")
	@PermissionAnnotation(":edit")
	@RecordLog(type = LogTypeEnum.EDIT)
	public Result link(@Valid @RequestBody final LinkInfo info) {
		return super.tService.linkRoleMenu(info);
	}
}
