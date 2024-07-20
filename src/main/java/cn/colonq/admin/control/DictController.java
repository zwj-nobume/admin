package cn.colonq.admin.control;

import java.util.Set;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.colonq.admin.anno.PermissionAnnotation;
import cn.colonq.admin.entity.DictInfo;
import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.group.Insert;
import cn.colonq.admin.group.Query;
import cn.colonq.admin.group.Update;
import cn.colonq.admin.service.IDictService;

@RestController
@RequestMapping("/dict")
@PermissionAnnotation("system:dict")
public class DictController {
	private final IDictService dictService;

	public DictController(IDictService dictService) {
		this.dictService = dictService;
	}

	@GetMapping("/page")
	@PermissionAnnotation(":query")
	public PageList<String> page(@RequestParam(name = "key", defaultValue = "") String key,
			@RequestParam(name = "pageNum", defaultValue = "1") Long pageNum,
			@RequestParam(name = "pageSize", defaultValue = "20") Long pageSize) {
		return dictService.selectPage(key, pageNum, pageSize);
	}

	@GetMapping("/value")
	@PermissionAnnotation(":query")
	public Result value(@Validated(value = { Query.class }) DictInfo info) {
		return dictService.selectValue(info.key());
	}

	@PutMapping("/add")
	@PermissionAnnotation(":add")
	public Result insert(@Validated(value = { Insert.class }) @RequestBody DictInfo info) {
		return dictService.insertDictInfo(info);
	}

	@PostMapping("/edit")
	@PermissionAnnotation(":edit")
	public Result update(@Validated(value = { Update.class }) @RequestBody DictInfo info) {
		return dictService.updateDictInfo(info);
	}

	@DeleteMapping("/delete")
	@PermissionAnnotation(":delete")
	protected Result delete(@RequestBody Set<String> keys) {
		return dictService.deleteDictInfo(keys);
	}
}
