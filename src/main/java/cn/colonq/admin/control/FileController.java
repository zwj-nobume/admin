package cn.colonq.admin.control;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
	private final HttpServletRequest request;

	public FileController(
			@Value("${server.file.dir}") final String basePath,
			final IFileService fileService,
			final HttpServletRequest request) {
		this.basePath = basePath;
		this.fileService = fileService;
		this.request = request;
	}

	@GetMapping("/list/**")
	@PermissionAnnotation(":query")
	public Result list() throws UnsupportedEncodingException {
		final String targetUrl = getTargetUrl();
		Path path = Path.of(this.basePath, targetUrl);
		if (!Files.exists(path)) {
			throw new ServiceException("文件夹路径不存在");
		}
		if (!Files.isDirectory(path)) {
			throw new ServiceException("此路径并非文件夹");
		}
		if (!Files.isReadable(path)) {
			throw new ServiceException("此路径不可读");
		}
		return this.fileService.list(path);
	}

	@PutMapping("/mkdir/**")
	@PermissionAnnotation(":add")
	public Result mkdir() throws UnsupportedEncodingException {
		final String targetUrl = getTargetUrl();
		Path path = Path.of(this.basePath, targetUrl);
		if (Files.exists(path)) {
			throw new ServiceException("文件夹路径已存在");
		}
		return this.fileService.mkdir(path);
	}

	@PutMapping("/upload/**")
	@PermissionAnnotation(":add")
	public Result upload(MultipartFile[] files) throws UnsupportedEncodingException {
		final String targetUrl = getTargetUrl();
		Path path = Path.of(this.basePath, targetUrl);
		if (!Files.exists(path)) {
			throw new ServiceException("文件夹路径不存在");
		}
		return this.fileService.uploadFile(path.toAbsolutePath().toString(), files);
	}

	@PostMapping("/move/**")
	@PermissionAnnotation(":edit")
	public Result move(@RequestBody Set<String> fromUrlSet) throws UnsupportedEncodingException {
		final String targetUrl = getTargetUrl();
		return this.fileService.moveFile(fromUrlSet, targetUrl);
	}

	@DeleteMapping("/delete/**")
	@PermissionAnnotation(":delete")
	public Result delete() throws UnsupportedEncodingException {
		final String targetUrl = getTargetUrl();
		Path path = Path.of(this.basePath, targetUrl);
		if (!Files.exists(path)) {
			throw new ServiceException("文件路径不存在");
		}
		return this.fileService.deleteFile(path);
	}

	@DeleteMapping("/deleteBatch/**")
	@PermissionAnnotation(":delete")
	public Result deleteBatch(@RequestBody Set<String> names) throws UnsupportedEncodingException {
		final String targetUrl = getTargetUrl();
		for (String name : names) {
			Path path = Path.of(this.basePath, targetUrl, name);
			if (!Files.exists(path)) {
				throw new ServiceException("文件路径不存在");
			}
			this.fileService.deleteFile(path);
		}
		return Result.ok("批量删除成功");
	}

	@GetMapping("/download/**")
	@PermissionAnnotation(":download")
	public ResponseEntity<Resource> download() throws UnsupportedEncodingException {
		final String targetUrl = getTargetUrl();
		Path path = Path.of(this.basePath, targetUrl);
		if (!Files.exists(path)) {
			throw new ServiceException("文件不存在");
		}
		if (!Files.isReadable(path)) {
			throw new ServiceException("文件不可读");
		}
		if (Files.isDirectory(path)) {
			throw new ServiceException("此路径是文件夹");
		}
		return this.fileService.download(path);
	}

	private String getTargetUrl() throws UnsupportedEncodingException {
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		final String url = this.request.getRequestURI();
		final String targetUrl = url.substring(methodName.length() + 6);
		if (targetUrl.indexOf("/../") != -1) {
			throw new ServiceException("路径异常");
		}
		return URLDecoder.decode(targetUrl, "UTF-8");
	}
}
