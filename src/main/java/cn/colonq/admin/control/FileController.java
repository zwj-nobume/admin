package cn.colonq.admin.control;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.colonq.admin.config.PermissionAnnotation;
import cn.colonq.admin.config.ServiceException;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.service.IFileService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/file")
@PermissionAnnotation("system:file")
public class FileController {
	private final String basePath;
	private final IFileService fileService;

	public FileController(
			@Value("${server.file.dir}") final String basePath,
			final IFileService fileService) {
		this.basePath = basePath;
		this.fileService = fileService;
	}

	@GetMapping("/list/**")
	@PermissionAnnotation(":query")
	public Result list(HttpServletRequest request) {
		final String url = request.getRequestURI();
		final String tgtUrl = url.substring(10);
		if (tgtUrl.startsWith("..")) {
			throw new ServiceException("路径异常");
		}
		Path path = Path.of(basePath, tgtUrl);
		if (!Files.exists(path)) {
			throw new ServiceException("文件夹路径不存在");
		}
		if (!Files.isDirectory(path)) {
			throw new ServiceException("此路径并非文件夹");
		}
		if (!Files.isReadable(path)) {
			throw new ServiceException("此路径不可读");
		}
		return fileService.list(path);
	}

	@PutMapping("/mkdir/**")
	@PermissionAnnotation(":add")
	public Result mkdir(HttpServletRequest request) {
		final String url = request.getRequestURI();
		final String tgtUrl = url.substring(11);
		if (tgtUrl.startsWith("..")) {
			throw new ServiceException("路径异常");
		}
		Path path = Path.of(basePath, tgtUrl);
		if (Files.exists(path)) {
			throw new ServiceException("文件夹路径已存在");
		}
		return fileService.mkdir(path);
	}

	@PutMapping("/upload")
	@PermissionAnnotation(":add")
	public Result upload(@RequestParam(value = "files") MultipartFile[] files) {
		return fileService.uploadFile(basePath, files);
	}

	@GetMapping("/download/**")
	@PermissionAnnotation(":download")
	public ResponseEntity<Resource> download(HttpServletRequest request) {
		final String url = request.getRequestURI();
		final String tgtUrl = url.substring(14);
		if (tgtUrl.startsWith("..")) {
			throw new ServiceException("路径异常");
		}
		Path path = Path.of(basePath, tgtUrl);
		if (!Files.exists(path)) {
			throw new ServiceException("文件不存在");
		}
		if (!Files.isReadable(path)) {
			throw new ServiceException("文件不可读");
		}
		if (Files.isDirectory(path)) {
			throw new ServiceException("此路径是文件夹");
		}
		return fileService.download(path);
	}
}
