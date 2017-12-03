package org.eocencle.winger.gateway.test;

public class UserEntity {
	private int id;
	private String username;
	private String password;
	private int age;
	private boolean sex;
	private String address;
	public UserEntity() {
		
	}
	public UserEntity(int id, String username, String password, int age,
			boolean sex, String address) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.age = age;
		this.sex = sex;
		this.address = address;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public boolean isSex() {
		return sex;
	}
	public void setSex(boolean sex) {
		this.sex = sex;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Override
	public String toString() {
		return this.id + "," + this.username;
	}
}
