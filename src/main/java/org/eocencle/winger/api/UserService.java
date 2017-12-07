package org.eocencle.winger.api;

import org.eocencle.winger.mapping.ApiBranch;
import org.eocencle.winger.mapping.ApiNamespace;
import org.springframework.stereotype.Service;

@Service
@ApiNamespace("/winger")
public class UserService {
	
	@ApiBranch("/getUser")
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
