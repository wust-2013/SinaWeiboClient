package com.login.weibo.bean;

/**
 * 账号实体类
 */
public class Account {
	private int _id;// SQLite自动生成、维护的列名，自增长
	private String uid = "";
	private String screen_name = "";
	private String url = "";
	private String access_token = "";
	private String expires_in = ""; 
	
	/*
	 * @param uid  新浪用户uid
	 * @param screen_name 昵称
	 * @param url  头像地址
	 * @param access_token 新浪的access_token
	 * @param expires_in  过期时间
	 */
	public Account(String uid, String screen_name, String url, 
			String access_token, String expires_in) {
		this.screen_name =screen_name;
		this.uid = uid;
		this.url = url;
		this.access_token = access_token;
		this.expires_in = expires_in;
	}

	public Account(){}
	
	public String getScreen_name() {
		return screen_name;
	}

	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}

}
