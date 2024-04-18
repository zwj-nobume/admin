package cn.colonq.admin.control;

import java.util.Set;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.service.BaseService;
import jakarta.servlet.http.HttpServletRequest;

public class BaseController<T, TService extends BaseService<T>> {
    private final HttpServletRequest request;
    private final TService tService;
    private final Class<T> clsT;

    public BaseController(final HttpServletRequest request, final TService tService, final Class<T> clsT) {
        this.request = request;
        this.tService = tService;
        this.clsT = clsT;
    }

    protected String getToken() {
        return request.getHeader("Authorization");
    }

    @GetMapping("/page")
    protected PageList<T> page(T param,
            @RequestParam(name = "pageNum", defaultValue = "1") Long pageNum,
            @RequestParam(name = "pageSize", defaultValue = "20") Long pageSize) {
        return tService.selectPage(param, pageNum, pageSize);
    }

    @PutMapping("/add")
    protected Result insert(@RequestBody T param) {
        return tService.insert(param);
    }

    @PostMapping("/edit")
    protected Result update(@RequestBody T param) {
        return tService.update(param);
    }

    @DeleteMapping("/delete")
    protected Result delete(@RequestBody Set<String> ids) {
        return tService.delete(clsT, ids);
    }
}
