package cn.colonq.admin.control;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.colonq.admin.entity.UserInfo;
import cn.colonq.admin.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController<UserInfo, IUserService> {

    public UserController(
            final HttpServletRequest request,
            final IUserService tService) {
        super(request, tService);
    }
}
