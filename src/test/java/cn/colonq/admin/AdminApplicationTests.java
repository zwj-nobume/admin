package cn.colonq.admin;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdminApplicationTests {

	@Test
	void contextLoads() {
		Path path = Path.of("/home/nobume/Software/upload/test1");
		File deleteFile = path.toFile();
		recursionDelete(deleteFile);
	}

	void recursionDelete(File file) {
		if (file == null || !file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				recursionDelete(child);
			}
		}
		file.delete();
	}
}
