package cn.colonq.admin;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.colonq.admin.utils.DateUtils;

@SpringBootTest
class AdminApplicationTests {
	@Autowired
	private DateUtils dateUtils;

	@Test
	void contextLoads() {
		System.out.println(dateUtils.format(new Date()));
	}
}
