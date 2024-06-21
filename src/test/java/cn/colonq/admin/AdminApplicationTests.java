package cn.colonq.admin;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdminApplicationTests {

	@Test
	void contextLoads() {
		Path path = Path.of("/home/nobume/Software/upload/");
		System.out.println(path.getFileName());
	}
}
