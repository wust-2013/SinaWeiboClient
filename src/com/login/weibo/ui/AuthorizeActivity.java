package com.login.weibo.ui;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.login.util.DBManager;
import com.login.weibo.bean.Account;
import com.login.weibo.bean.Oauth2AccessToken;
import com.login.weibo.bean.User;
import com.login.weibo.bean.util.UserUtil;
import com.weibo.exception.WeiboDialogError;
import com.weibo.exception.WeiboException;
import com.weibo.net.RequestListener;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboAuthListener;
import com.weibo.sdk.android.api.UsersAPI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class AuthorizeActivity extends BaseActivity {
	private final String TAG = "AuthorizeActivity";
	private static final int SUCCESS=1;
	
	private String url = "";
	private String screen_name = "";
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
            if(msg.what==SUCCESS){
            	/** 解析用户信息,提取出url,screen_name */
            	try {
            		String response=(String) msg.obj;
					Log.v(TAG,response);
					
					User mUser=UserUtil.getUser(response);
					url=mUser.getProfileImageUrl();
					screen_name=mUser.getScreenName();
					
					Log.v(TAG,url);
					Log.v(TAG,screen_name);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d(TAG,"回调 onComplete出错");
					e.printStackTrace();
					hasData=false;
				}
            	
            	if (hasData){
    				Log.v(TAG,"addAccount()");
    				addAccount(mWeibo.getAccessToken().getUid(),screen_name,url,
    						mWeibo.getAccessToken().getToken(),String.valueOf(mWeibo.getAccessToken().getExpiresTime()));
    			}else{
    				finish();
    			}
                /**授權成功后進入AccountActivity */
    			Intent intent = new Intent();
    			intent.setClass(AuthorizeActivity.this, AccountActivity.class);
    			startActivity(intent);
    			finish();
            }
		}
		
		/**
		 * @param uid  新浪用户uid
	     * @param screen_name 昵称
	     * @param url  头像地址
	     * @param access_token 新浪的access_token
	     * @param expires_in  过期时间
		 */
		public void addAccount(String uid, String screen_name, String url, 
				String access_token, String expires_in) {
			Account account = new Account(uid,screen_name,url,access_token,expires_in);
			mDBManager.add(account);
		}
		
	};
	private boolean hasData=true;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWeibo.setupConsumerConfig(CONSUMER_KEY, REDIRECT_URL);
		mWeibo.authorize(AuthorizeActivity.this, new AuthDialogListener());
	}

	/**
	 * 内部类，回调类,用于进行Oauth2认证
	 * @author Administrator
	 *
	 */
	class AuthDialogListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			getData();
		}
		
		@Override
		public void onError(WeiboDialogError e) {
//			Toast.makeText(getApplicationContext(), "授权出错 : " + e.getMessage(),
//					Toast.LENGTH_SHORT).show();
//			finish();
		}

		@Override
		public void onCancel() {
//			Toast.makeText(getApplicationContext(), "用户取消授权！",
//					Toast.LENGTH_SHORT).show();
//			finish();
		}

		@Override
		public void onWeiboException(WeiboException e) {
//			Toast.makeText(getApplicationContext(),
//					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
//					.show();
//			finish();
		}
		
		/**
		 * 获取图像url，用户昵称screen_name
		 * @return Boolean 若为true则获取数据正确，若为false则为没有获取到数据
		 */
		private void getData() {
			//调用  user/show 接口，获取用户信息  
			UsersAPI user=new UsersAPI(mWeibo.getAccessToken());
			Log.v(TAG, "getData()"+mWeibo.getAccessToken().getUid());
			Log.v(TAG, "getData()"+mWeibo.getAccessToken().getToken());
			long id=Long.parseLong(mWeibo.getAccessToken().getUid());
			user.show(id, new RequestListener() {
				
				@Override
				public void onIOException(IOException e) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void onError(WeiboException e) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void onComplete(String response) {
					// TODO Auto-generated method stub
					/**将response 发送给UI线程 */
					Message msg=new Message();
					msg.what=SUCCESS;
					msg.obj=response;
					handler.sendMessage(msg);
				}
			});
		}
		


	}

	@Override
	public void onResume() {
		super.onResume();
	}


	@Override
	public int getLayout() {
//		return R.layout.account_authorize;
		return 0;
	}

}