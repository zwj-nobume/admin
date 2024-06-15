package cn.colonq.admin;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdminApplicationTests {

	@Test
	void contextLoads() {
		File file = new File("/home/nobume/AdminDir/test1/test2/test3");
		file.mkdirs();
	}
}
