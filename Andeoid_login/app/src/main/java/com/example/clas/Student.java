package com.example.clas;

import java.io.Serializable;

public class Student implements Serializable {
	private String user;
	private String password;
	private String sex;
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Student(String user, String password, String sex) {
		super();
		this.user = user;
		this.password = password;
		this.sex = sex;
	}

	public Student() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Student [user=" + user + ", password=" + password + ", sex="
				+ sex + "]";
	}
	

}
