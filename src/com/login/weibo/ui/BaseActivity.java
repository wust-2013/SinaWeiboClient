package com.login.weibo.ui;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.login.util.DBManager;
import com.login.weibo.bean.Oauth2AccessToken;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboParameters;

public abstract class BaseActivity extends Activity {
	private final String TAG = "BaseActivity";
	
	public DBManager mDBManager;
	
	public Weibo mWeibo = Weibo.getInstance();
	public static final String CONSUMER_KEY = "1140438529";
	public static final String REDIRECT_URL = "http://localhost:3030/Weibo/index.jsp";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDBManager = new DBManager(getApplicationContext());
	}

	@Override
	protected void onDestroy() {
		mDBManager.closeDB();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	public abstract int getLayout();
}
