package cn.colonq.admin.control;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.colonq.admin.config.PermissionAnnotation;
import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.service.IUserService;

@RestController
@RequestMapping("/user")
@PermissionAnnotation("system:user:")
public class UserController extends BaseController<UserInfo, IUserService> {

    public UserController(final IUserService userService) {
        super(userService, UserInfo.class);
    }

    @PostMapping("/login")
    public Result login(@RequestBody UserInfo info) {
        return super.tService.login(info);
    }

    @PostMapping("/salt")
    public Result salt() {
        return super.tService.regenerateSalt();
    }

    @PostMapping("/link")
    @PermissionAnnotation("edit")
    public Result link(@RequestBody LinkInfo info) {
        return super.tService.linkUserRole(info);
    }
}
