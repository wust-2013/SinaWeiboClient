package com.login.weibo.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
/**
 * 收藏微博
 */
public class Favorite extends Activity {
	String httpMethod = "POST";
	String url;
	String jsonData;
	String sign = null;
	JSONObject jsonObj = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SharedPreferences pres = this.getSharedPreferences("parameters",
				Context.MODE_PRIVATE);
		String sina_access_token = pres.getString("sina_access_token", "");
		String sina_access_secret = pres.getString("sina_access_secret", "");
		Intent intent = getIntent();
		String weiBoID = intent.getStringExtra("weiBoID");

		url = "http://api.t.sina.com.cn/favorites/create.json";
		try {
			jsonObj = new JSONObject(jsonData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			if (jsonObj.getString("id").equals(weiBoID)) {
				sign = "收藏成功！";
			} else {
				sign = "收藏失败！";
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(Favorite.this, sign, Toast.LENGTH_SHORT).show();
		finish();

	}
}
