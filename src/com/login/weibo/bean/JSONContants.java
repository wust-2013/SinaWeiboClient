package com.login.weibo.bean;

public interface JSONContants {
	interface User {
		String id = "id";
		String screen_name = "screen_name";
		String name = "name";
		String province = "province";
		String city = "city";
		String location = "location";
		String description = "description";
		String profile_image_url = "profile_image_url";
		String gender = "gender";
		String status="status";
		String followers_count = "followers_count";
		String friends_count = "friends_count";
		String statuses_count = "statuses_count";
		String favourites_count = "favourites_count";
		String created_at = "created_at";
		String verified = "verified";
		String online_status = "online_status";
	}
	interface Status{
		 String id="id";
		 String created_at = "created_at";
		 String source="source";
		 String text="text";
		 String user="user";
		 String retweeted_status="retweeted_status";
		 String  reposts_count="reposts_count";
		 String  comments_count="comments_count";
		 String  attitudes_count="attitudes_count";
	}
	interface Statuses{
		String statuses="statuses";
	}
}
