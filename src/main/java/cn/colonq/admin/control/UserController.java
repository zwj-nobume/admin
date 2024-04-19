package cn.colonq.admin.control;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController<UserInfo, IUserService> {

    public UserController(
            final HttpServletRequest request,
            final IUserService userService) {
        super(request, userService, UserInfo.class);
    }

    @PostMapping("/link")
    public Result link(@RequestBody LinkInfo info) {
        return super.tService.linkUserRole(info);
    }
}
