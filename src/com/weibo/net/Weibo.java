package com.weibo.net;

import com.login.util.Utility;
import com.login.weibo.bean.Oauth2AccessToken;
import com.weibo.exception.WeiboDialogError;
import com.weibo.exception.WeiboException;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;
import com.weibo.net.WeiboAuthListener;

public class Weibo {
	public static final String SERVER = "https://api.weibo.com/2/";
	public static final String URL_OAUTH2_ACCESS_AUTHORIZE = "https://api.weibo.com/oauth2/authorize";

	private static Weibo mWeiboInstance = null;

	public static String app_key = "";//第三方应用的appkey
	public static String redirecturl = "";// 重定向url

	public Oauth2AccessToken accessToken = null;//AccessToken实例

	public static final String KEY_TOKEN = "access_token";
	public static final String KEY_EXPIRES = "expires_in";
	public static final String KEY_UID = "uid";

	/**
	 * @return Weibo的实例
	 */
	public synchronized static Weibo getInstance() {
		if (mWeiboInstance == null) {
			mWeiboInstance = new Weibo();
		}
		return mWeiboInstance;
	}
	
	/**
	 * 设定第三方使用者的appkey和重定向url
	 * @param appKey 第三方应用的appkey
	 * @param redirectUrl 第三方应用的回调页
	 */
	public void setupConsumerConfig(String appKey,String redirectUrl) {
		app_key = appKey;
		redirecturl = redirectUrl;
	}
	
	public Oauth2AccessToken getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(Oauth2AccessToken accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * 
	 * 进行微博认证
	 * @param activity 调用认证功能的Context实例
	 * @param listener WeiboAuthListener 微博认证的回调接口
	 */
	public void authorize(Context context, WeiboAuthListener listener) {
		startAuthDialog(context, listener);
	}

	public void startAuthDialog(Context context, final WeiboAuthListener listener) {
		WeiboParameters params = new WeiboParameters();
		startDialog(context, params, new WeiboAuthListener() {
			@Override
			public void onComplete(Bundle values) {
				// ensure any cookies set by the dialog are saved
				CookieSyncManager.getInstance().sync();
				if (null == accessToken) {
					accessToken = new Oauth2AccessToken();
				}
				accessToken.setToken(values.getString(KEY_TOKEN));
				accessToken.setExpiresIn(values.getString(KEY_EXPIRES));
				accessToken.setUid(values.getString(KEY_UID));
				
				if (accessToken.isSessionValid()) {
					Log.d("Weibo-authorize",
							"Login Success! access_token=" + accessToken.getToken() + " expires="
									+ accessToken.getExpiresTime());
					listener.onComplete(values);
				} else {
					Log.d("Weibo-authorize", "Failed to receive access token");
					listener.onWeiboException(new WeiboException("Failed to receive access token."));
				}
			}

			@Override
			public void onError(WeiboDialogError error) {
				Log.d("Weibo-authorize", "Login failed: " + error);
				listener.onError(error);
			}

			@Override
			public void onWeiboException(WeiboException error) {
				Log.d("Weibo-authorize", "Login failed: " + error);
				listener.onWeiboException(error);
			}

			@Override
			public void onCancel() {
				Log.d("Weibo-authorize", "Login canceled");
				listener.onCancel();
			}
		});
	}

	public void startDialog(Context context, WeiboParameters parameters,
			final WeiboAuthListener listener) {
		parameters.add("client_id", app_key);
		parameters.add("response_type", "token");
		parameters.add("redirect_uri", redirecturl);
		parameters.add("display", "mobile");

		String url = URL_OAUTH2_ACCESS_AUTHORIZE + "?" + Utility.encodeUrl(parameters);
		if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			Utility.showAlert(context, "Error",
					"Application requires permission to access the Internet");
		} else {
			new WeiboDialog(context, url, listener).show();
		}
	}

}
