package cn.colonq.admin;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.sun.jna.platform.win32.Netapi32Util.UserInfo;

@SpringBootTest
class AdminApplicationTests {

	@Test
	void contextLoads() {
		Class<UserInfo> cls = UserInfo.class;
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			System.out.println(field.getGenericType());
		}
	}
}
