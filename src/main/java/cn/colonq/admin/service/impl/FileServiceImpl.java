package cn.colonq.admin.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
	public Result list(final Path path) {
		final File[] files = path.toFile().listFiles();
		final List<FileMsg> data = new ArrayList<>(files.length);
		for (final File file : files) {
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
	public Result mkdir(final Path path) {
		final File dir = path.toFile();
		if (!dir.mkdirs()) {
			throw new ServiceException("创建文件夹失败");
		}
		return Result.ok("创建文件夹成功");
	}

	@Override
	public Result uploadFile(final String path, final MultipartFile[] files) {
		if (stringUtils.isEmpty(path)) {
			throw new ServiceException("文件上传路径为空");
		}
		final List<String> successList = new ArrayList<>();
		for (final MultipartFile file : files) {
			final String fileName = file.getOriginalFilename();
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
	public Result moveFile(final Set<String> fromUrlSet, final String targetUrl) {
		if (fromUrlSet == null || fromUrlSet.size() == 0) {
			throw new ServiceException("文件修改失败, 源路径错误");
		}
		if (fromUrlSet.size() == 1) {
			final String fromUrl = fromUrlSet.iterator().next();
			final Path source = Path.of(fromUrl);
			final Path target = Path.of(targetUrl);
			move(source, target);
		} else {
			for (final String fromUrl : fromUrlSet) {
				final Path targetPath = Path.of(targetUrl);
				if (!Files.exists(targetPath) && targetPath.toFile().mkdirs()) {
					throw new ServiceException("文件修改失败, 目标路径错误, 无法创建文件夹");
				}
				final Path source = Path.of(fromUrl);
				final Path target = Path.of(targetUrl, source.getFileName().toString());
				move(source, target);
			}
		}
		return Result.ok("移动文件成功");
	}

	@Override
	public Result deleteFile(final Path path) {
		recursionDelete(path.toFile());
		return Result.ok("删除成功");
	}

	@Override
	public ResponseEntity<Resource> download(final Path path) {
		return ResponseEntity.ok(new FileSystemResource(path));
	}

	private void recursionDelete(final File file) {
		if (file == null || !file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			for (final File child : file.listFiles()) {
				recursionDelete(child);
			}
		}
		file.delete();
	}

	private void move(final Path source, final Path target) {
		if (!Files.exists(source)) {
			throw new ServiceException("文件修改失败, 源路径错误");
		}
		if (Files.exists(target)) {
			throw new ServiceException("文件修改失败, 目标路径错误");
		}
		try {
			Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException e) {
			throw new ServiceException("文件移动失败");
		}
	}
}
