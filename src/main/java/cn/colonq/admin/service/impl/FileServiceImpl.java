package cn.colonq.admin.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cn.colonq.admin.config.ServiceException;
import cn.colonq.admin.entity.FileMsg;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.service.IFileService;
import cn.colonq.admin.utils.StringUtils;

@Service(value = "fileService")
public class FileServiceImpl implements IFileService {
	private final StringUtils stringUtils;

	public FileServiceImpl(StringUtils stringUtils) {
		this.stringUtils = stringUtils;
	}

	@Override
	public Result list(Path path) {
		final File[] files = path.toFile().listFiles();
		final List<FileMsg> data = new ArrayList<>(files.length);
		for (File file : files) {
			String fileName = file.getName();
			boolean isHide = file.isHidden();
			if (file.isDirectory()) {
				data.add(new FileMsg(fileName, 0, true, isHide));
			} else {
				data.add(new FileMsg(fileName, file.length(), false, isHide));
			}
		}
		return Result.ok(data);
	}

	@Override
	public Result mkdir(Path path) {
		final File dir = path.toFile();
		if (!dir.mkdirs()) {
			throw new ServiceException("创建文件夹失败");
		}
		return Result.ok("创建文件夹成功");
	}

	@Override
	public Result uploadFile(String path, MultipartFile[] files) {
		if (stringUtils.isEmpty(path)) {
			return Result.error("文件上传路径为空");
		}
		List<String> successList = new ArrayList<>();
		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			try {
				file.transferTo(Path.of(path, fileName));
			} catch (IllegalStateException | IOException e) {
				throw new ServiceException("文件: " + fileName + " 上传失败");
			}
			successList.add(fileName);
		}
		return Result.ok("文件上传成功", successList);
	}

	@Override
	public ResponseEntity<Resource> download(Path path) {
		return ResponseEntity.ok(new FileSystemResource(path));
	}
}
