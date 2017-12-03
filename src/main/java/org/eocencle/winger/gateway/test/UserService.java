package org.eocencle.winger.gateway.test;

import org.eocencle.winger.gateway.ApiMapping;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	@ApiMapping("/winger/getUser")
	public UserEntity getUser(Integer id, String name, UserEntity user) {
		System.out.println("id:" + id + ",name:" + name);
		System.out.println(user.toString());
		return new UserEntity(105, "王金龙", "123456", 25, true, "陕西省宝鸡市凤翔县");
	}

	@Override
	public String toString() {
		return "UserService";
	}
	
}
