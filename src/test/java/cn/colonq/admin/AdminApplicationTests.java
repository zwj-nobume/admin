package cn.colonq.admin;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdminApplicationTests {

	@Test
	void contextLoads() {
		Path path = Path.of("/home/nobume/AdminDir", "VMware.vscode-boot-dev-pack-0.1.0.vsix");
		String absolutePath = path.toAbsolutePath().toString();
		System.out.println(absolutePath);
	}
}
