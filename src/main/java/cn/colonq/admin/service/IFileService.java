package cn.colonq.admin.service;

import java.nio.file.Path;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import cn.colonq.admin.entity.Result;

public interface IFileService {

	Result list(final Path path);

	Result mkdir(final Path path);

	Result uploadFile(final String path, final MultipartFile[] files);

	Result moveFile(final Set<String> fromUrlSet, final String targetUrl);

    Result deleteFile(final Path path);

	ResponseEntity<Resource> download(final Path path);
}
