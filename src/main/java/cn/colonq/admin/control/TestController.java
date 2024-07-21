package cn.colonq.admin.control;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.colonq.admin.entity.Result;

@RestController
public class TestController {

    @GetMapping("/test")
    public Result test() {
        return Result.ok();
    }
}
