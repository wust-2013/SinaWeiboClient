package com.login.weibo.ui;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.login.weibo.bean.Oauth2AccessToken;
import com.login.weibo.bean.User;
import com.login.weibo.bean.util.UserUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.weibo.exception.WeiboException;
import com.weibo.net.RequestListener;
import com.weibo.sdk.android.api.UsersAPI;
/**
 * 微博用户信息
 */
public class UserInfoActivity extends Activity {
	private final String TAG = "UserInfoActivity";
	
	// ----------头部工具栏-----------------------
	private ImageView writeBtn = null;
	private TextView titleTV = null;

	// ----------底部导航栏------------------------
	private View friendTimeLine;
	private View userTimeLine;
	private View userNews;
	private View userInfo;
	private View more;
	private FooterClickListener listener;

	private ImageView userHead = null;
	private TextView userName = null;
	private TextView genderOfUser = null;
	private TextView locationOfUser = null;
	private TextView descriptionOfUser = null;

	private Button sta_count_Btn = null;
	private Button fow_count_Btn = null;
	private Button fri_count_Btn = null;
	
	private String access_token="";
	private String uid="";
	private Handler handler;
	
	// Universal Image Loader for Android 第三方框架组件
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		
		Intent i = getIntent();
		int currentTag = i.getIntExtra("currentTag", 3);
		access_token=i.getStringExtra("access_token");
		uid=i.getStringExtra("uid");
		
		Log.v("UserInfoActivity",access_token);
		Log.v("UserInfoActivity",uid);
		
		handler=new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what==1){
					User my=(User)msg.obj;
					
					Log.v("UserInfoActivity",my.toString());
					
					userName.setText(my.getScreenName());
					userName.setTextColor(Color.BLACK);
					userName.setTextSize(20);
					
					locationOfUser.setText(my.getLocation());
					locationOfUser.setTextColor(Color.BLACK);
					
					descriptionOfUser.setText(my.getDescription());
			
					String gender=my.getGender();
					if (gender.equals("m")) genderOfUser.setText("男");
					else if (gender.equals("f")) genderOfUser.setText("女");
					else genderOfUser.setText("未设置"); 
					genderOfUser.setTextColor(Color.BLACK);
					
					
					//setViewImage(userHead, headerImageUrl);
					imageLoader.displayImage(my.getProfileImageUrl(), userHead);
			
					String statuses_count_temp = my.getStatusesCount() + "</font><br><font size='10px' color='#A7A7A7'>微博";
					Spanned localSpanned1 = Html.fromHtml(statuses_count_temp);
					sta_count_Btn.setText(localSpanned1);
					
					String followers_count_temp = my.getFollowersCount() + "</font><br><font size='10px' color='#A7A7A7'>粉丝";
					Spanned localSpanned2 = Html.fromHtml(followers_count_temp);
					fow_count_Btn.setText(localSpanned2);
					
					String friends_count_temp = my.getFriendsCount() + "</font><br><font size='10px' color='#A7A7A7'>关注";
					Spanned localSpanned3 = Html.fromHtml(friends_count_temp);
					fri_count_Btn.setText(localSpanned3);
				}
			}
		};
		
		initComponents();
		initFooter();
		
		/**通过Intent 获取 access_token,uid */
		handleData(access_token,uid);
		
		setSelectedFooterTab(currentTag);
		
		 options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.icon)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
	}

	private void initComponents() {
		writeBtn = (ImageView) findViewById(R.id.weibo_writeBtn);
		writeBtn.setVisibility(View.INVISIBLE);
		
		titleTV = (TextView) findViewById(R.id.weibo_title_TV);
		titleTV.setText("个人资料");
		userHead = (ImageView) findViewById(R.id.userHead);
		userName = (TextView) findViewById(R.id.userName);
		genderOfUser = (TextView) findViewById(R.id.sexOfUser);
		locationOfUser = (TextView) findViewById(R.id.locationOfUser);
		descriptionOfUser = (TextView) findViewById(R.id.introductionTV);
		
		sta_count_Btn = (Button) findViewById(R.id.mblogNumBtn);
		fow_count_Btn = (Button) findViewById(R.id.fansNumBtn);
		fri_count_Btn = (Button) findViewById(R.id.guanzhuNumBtn);

	}
	
	/**
	 * 底部导航栏
	 */
	private void initFooter(){
		listener = new FooterClickListener();
		
		friendTimeLine = findViewById(R.id.weibo_menu_friendTimeLine);
		userTimeLine = findViewById(R.id.weibo_menu_userTimeLine);
		userNews = findViewById(R.id.weibo_menu_userNews);
		userInfo = findViewById(R.id.weibo_menu_myInfo);
		more = findViewById(R.id.weibo_menu_more);
		
		friendTimeLine.setId(0);
		userTimeLine.setId(1);
		userNews.setId(2);
		userInfo.setId(3);
		more.setId(4);
		
		friendTimeLine.setOnClickListener(listener);
		userTimeLine.setOnClickListener(listener);
		userNews.setOnClickListener(listener);
		userInfo.setOnClickListener(listener);
		more.setOnClickListener(listener);
	}
	
	/**
	 * 底部导航栏
	 */
	private class FooterClickListener implements android.view.View.OnClickListener{

		@Override
		public void onClick(View v) {
			Intent i = new Intent();
			i.putExtra("access_token", access_token);
			i.putExtra("uid", uid);
			i.putExtra("currentTag", v.getId());
			i.setClass(getApplicationContext(), TimeLine.class);
			startActivity(i);
			finish();
		}
		
	}
	
	/**
	 *初始化控件
	 */
	private void handleData(String access_token,String uid) {
		Oauth2AccessToken auth=new Oauth2AccessToken();
		auth.setToken(access_token);
		auth.setUid(uid);
		bindData(auth);
	}

	/**
	 * 设置
	 * @param i
	 */
	protected void setSelectedFooterTab(int i) {
		//mCurFooterTab = i;
		friendTimeLine.setBackgroundResource(0);
		userTimeLine.setBackgroundResource(0);
		userNews.setBackgroundResource(0);
		userInfo.setBackgroundResource(0);
		more.setBackgroundResource(0);
		if (i == 0) {friendTimeLine.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);titleTV.setText("微博主页");}
		if (i == 1) {userTimeLine.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);titleTV.setText("我的微博");}
		if (i == 2) {userNews.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);titleTV.setText("微博动态");}
		if (i == 3) userInfo.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);
		if (i == 4) more.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);
	}

	private void bindData(Oauth2AccessToken auth) {
		UsersAPI user=new UsersAPI(auth);
		Long uid=Long.parseLong(auth.getUid());
		user.show(uid, new RequestListener() {
			public void onIOException(IOException e) {}
			public void onError(WeiboException e) {}
			public void onComplete(String response) {
				Message msg=new Message();
				try {
					User mUser=UserUtil.getUser(response);
					msg.what=1;
					msg.obj=mUser;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d(TAG,"回调 onComplete出错");
					e.printStackTrace();
					msg.what=-1;
					msg.obj=new User();
				}
				handler.sendMessage(msg);
			}
		});
	}

	public int getLayout() {
		return R.layout.weibo_userinfo;
	}
}
