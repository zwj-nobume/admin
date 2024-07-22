package cn.colonq.admin.control;

import java.util.Set;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.colonq.admin.anno.PermissionAnnotation;
import cn.colonq.admin.anno.RecordLog;
import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.enumcfg.LogTypeEnum;
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
	protected PageList<T> page(@Validated(value = { Query.class }) final T param,
			@RequestParam(name = "pageNum", defaultValue = "1") final Long pageNum,
			@RequestParam(name = "pageSize", defaultValue = "20") final Long pageSize,
			@RequestParam(name = "sortFlag", defaultValue = "create_time") final String sortFlag) {
		return tService.selectPage(param, pageNum, pageSize, sortFlag);
	}

	@PutMapping("/add")
	@PermissionAnnotation(":add")
	@RecordLog(type = LogTypeEnum.ADD)
	protected Result insert(@Validated(value = { Insert.class }) @RequestBody final T param) {
		return tService.insert(param);
	}

	@PostMapping("/edit")
	@PermissionAnnotation(":edit")
	@RecordLog(type = LogTypeEnum.EDIT)
	protected Result update(@Validated(value = { Update.class }) @RequestBody final T param) {
		return tService.update(param);
	}

	@DeleteMapping("/delete")
	@PermissionAnnotation(":delete")
	@RecordLog(type = LogTypeEnum.DELETE)
	protected Result delete(@RequestBody final Set<String> ids) {
		return tService.delete(ids);
	}
}
