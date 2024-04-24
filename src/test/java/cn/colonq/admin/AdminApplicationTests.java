package cn.colonq.admin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdminApplicationTests {

	@Test
	void contextLoads() {
		final String a = "/user/login";
		final String b = "/user/login";
		final String c = "^/file/.*";
		final String d = "/file/download";
		System.out.println(a.matches(b));
		System.out.println(d.matches(c));
	}
}
