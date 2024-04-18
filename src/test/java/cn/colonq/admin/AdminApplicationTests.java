package cn.colonq.admin;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import cn.colonq.admin.entity.MenuInfo;

@SpringBootTest
class AdminApplicationTests {

	@Test
	void contextLoads() {
		Class<MenuInfo> cls = MenuInfo.class;
		for (Field field : cls.getDeclaredFields()) {
			System.out.println(field.getName().equals("createName"));
		}
	}
}
