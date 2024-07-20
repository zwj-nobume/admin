package cn.colonq.admin.control;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.colonq.admin.anno.PermissionAnnotation;
import cn.colonq.admin.entity.LogInfo;
import cn.colonq.admin.service.ILogService;

@RestController
@RequestMapping("/log")
@PermissionAnnotation("system:log")
public class LogController extends BaseController<LogInfo, ILogService> {

	public LogController(final ILogService tService) {
		super(tService);
	}
}
