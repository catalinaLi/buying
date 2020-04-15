package com.crawler.xiaomi.pojo;

import com.alibaba.fastjson.JSON;
import org.apache.http.cookie.Cookie;

import java.util.List;


/**
 * 用户基本信息
 * @author liwei
 * @date: 2018年6月8日 下午4:34:18
 *
 */
public class User {

	private List<Cookie> cookies;
	
	private String userName;
	
	private String password;

	public List<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public boolean equals(Object o) {
		if(o instanceof User){
			return ((User) o).getUserName().equals(userName)
			&&((User) o).getPassword().equals(password);
		}
		return false;
	}

	public User() {
		super();
	}

	public User(String userName, String password) throws Exception {
		if(userName==null||userName.length()==0){
			throw new Exception("账号不能为空");
		}
		if(password==null||password.length()==0){
			throw new Exception("密码不能为空");
		}
		this.userName = userName;
		this.password = password;
	}
	
	
}
