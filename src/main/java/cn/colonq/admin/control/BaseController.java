package cn.colonq.admin.control;

import java.util.Set;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.colonq.admin.config.PermissionAnnotation;
import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.group.Insert;
import cn.colonq.admin.group.Query;
import cn.colonq.admin.group.Update;
import cn.colonq.admin.service.IBaseService;

public class BaseController<T, TService extends IBaseService<T>> {
	protected final TService tService;

	public BaseController(final TService tService) {
		this.tService = tService;
	}

	@GetMapping("/page")
	@PermissionAnnotation(":query")
	protected PageList<T> page(@Validated(value = { Query.class }) T param,
			@RequestParam(name = "pageNum", defaultValue = "1") Long pageNum,
			@RequestParam(name = "pageSize", defaultValue = "20") Long pageSize,
			@RequestParam(name = "sortFlag", defaultValue = "create_time") String sortFlag) {
		return tService.selectPage(param, pageNum, pageSize, sortFlag);
	}

	@PutMapping("/add")
	@PermissionAnnotation(":add")
	protected Result insert(@Validated(value = { Insert.class }) @RequestBody T param) {
		return tService.insert(param);
	}

	@PostMapping("/edit")
	@PermissionAnnotation(":edit")
	protected Result update(@Validated(value = { Update.class }) @RequestBody T param) {
		return tService.update(param);
	}

	@DeleteMapping("/delete")
	@PermissionAnnotation(":delete")
	protected Result delete(@RequestBody Set<String> ids) {
		return tService.delete(ids);
	}
}
