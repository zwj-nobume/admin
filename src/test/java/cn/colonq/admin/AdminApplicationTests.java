package cn.colonq.admin;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.colonq.admin.entity.LinkInfo;
import cn.colonq.admin.service.IUserService;

@SpringBootTest
class AdminApplicationTests {
	@Autowired
	private IUserService userService;

	@Test
	void contextLoads() {
		String id = "d08ecc4d-fb30-11ee-9405-f0d41530a047";
		Set<String> ids = new HashSet<>();
		ids.add("710d2766-fc8f-11ee-9405-f0d41530a047");
		ids.add("bddc962c-fdec-11ee-9405-f0d41530a047");
		ids.add("c0eaa4f0-fdec-11ee-9405-f0d41530a047");
		ids.add("c36b748d-fdec-11ee-9405-f0d41530a047");

		LinkInfo info = new LinkInfo(id, ids);
		userService.linkUserRole(info);
	}
}
