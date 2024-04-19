package cn.colonq.admin.control;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.MenuInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.service.IMenuService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/menu")
public class MenuController extends BaseController<MenuInfo, IMenuService> {

    public MenuController(
            final HttpServletRequest request,
            final IMenuService menuService) {
        super(request, menuService, MenuInfo.class);
    }

    @PostMapping("/link")
    public Result link(@RequestBody LinkInfo info) {
        return super.tService.linkRoleMenu(info);
    }
}
