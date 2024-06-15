package cn.colonq.admin.service;

import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import cn.colonq.admin.entity.Result;

public interface IFileService {

	Result list(Path path);

	Result mkdir(Path path);

	Result uploadFile(String path, MultipartFile[] files);

	ResponseEntity<Resource> download(Path path);
}
