package cn.colonq.admin.control;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.colonq.admin.entity.MenuInfo;
import cn.colonq.admin.service.IMenuService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/menu")
public class MenuController extends BaseController<MenuInfo, IMenuService> {

    public MenuController(
            final HttpServletRequest request,
            final IMenuService tService) {
        super(request, tService, MenuInfo.class);
    }
}
