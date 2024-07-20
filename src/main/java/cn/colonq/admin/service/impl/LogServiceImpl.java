package cn.colonq.admin.service.impl;

import org.springframework.stereotype.Service;

import cn.colonq.admin.entity.LogInfo;
import cn.colonq.admin.mapper.LogMapper;
import cn.colonq.admin.service.ILogService;
import cn.colonq.admin.utils.JWT;
import cn.colonq.admin.utils.StringUtils;

@Service(value = "logService")
public class LogServiceImpl extends BaseServiceImpl<LogInfo, LogMapper> implements ILogService {

	public LogServiceImpl(
			final StringUtils stringUtils,
			final LogMapper tmapper,
			final JWT jwt) {
		super(LogInfo.class, stringUtils, tmapper, jwt);
	}
}
