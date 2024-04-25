package cn.colonq.admin.control;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.colonq.admin.config.PermissionAnnotation;
import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.MenuInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.service.IMenuService;

@RestController
@RequestMapping("/menu")
@PermissionAnnotation("system:menu")
public class MenuController extends BaseController<MenuInfo, IMenuService> {

    public MenuController(final IMenuService menuService) {
        super(menuService, MenuInfo.class);
    }

    @PostMapping("/link")
    @PermissionAnnotation(":edit")
    public Result link(@RequestBody LinkInfo info) {
        return super.tService.linkRoleMenu(info);
    }
}
