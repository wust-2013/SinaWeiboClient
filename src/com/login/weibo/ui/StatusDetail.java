package com.login.weibo.ui;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.login.util.TimeUtil;
import com.login.weibo.bean.Status;
import com.login.weibo.bean.User;
import com.login.weibo.ui.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
/**
 * 新浪微博正文
 */
public class StatusDetail extends Activity {
	private final String TAG = "StatusDetail";
	ImageView back = null;
	ImageView avatar = null;
	TextView name = null;
	TextView text = null;
	ImageView image = null;
	TextView source = null;
	TextView created_at = null;
	TextView comments = null;
	TextView rt = null;
	ImageView verified = null;
	LinearLayout ll_btn = null;
	LinearLayout ll_lyt = null;
	TextView rt_text = null;
	ImageView rt_Image = null;
	Status mStatus = null;
	String num = null;
	
	View comm;
	View repost;
	
	private static final int STATUSDETAIL_COMMENT=0Xa1;
	private static final int STATUSDETAIL_COMMENT_SUCCESS=0Xa2;
	private static final int STATUSDETAIL_COMMENT_FAIL=0Xa3;
	
	private static final int STATUSDETAIL_REPOST=0xb1;
	private static final int STATUSDETAIL_REPOST_SUCCESS=0xb2;
	private static final int STATUSDETAIL_REPOST_FAIL=0xb3;

	// Universal Image Loader for Android 第三方框架组件
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	String access_token;
	String userid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weibo_statusdetail);
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.icon)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer(5))
		.build();
		
		Intent intent = getIntent();
		mStatus = (Status) intent.getSerializableExtra("detail");
		access_token=intent.getStringExtra("access_token");
		userid=intent.getStringExtra("uid");
		
		initViews();
		setData2Views(mStatus);
		
		comm=findViewById(R.id.relativelyt_commment);
		repost=findViewById(R.id.relativelyt_redirect);
		
		/**轉跳到評論界面 */
		comm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			   Intent intent=new Intent();
			   /**寫入微博id到intent中 */
			   String uid=mStatus.getId();
			   intent.putExtra("uid", uid);
			   /**寫入access_token到intent中 */
			   String access_token=StatusDetail.this.access_token;
			   intent.putExtra("access_token", access_token);
			   intent.setClass(StatusDetail.this, Comment.class);
			   startActivityForResult(intent, STATUSDETAIL_COMMENT);
			}
		});
		
		/** 轉跳到轉發界面*/
		repost.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				/**寫入微博到轉發界面 */
				intent.putExtra("detail", mStatus);
				/**寫入access_token到intent中 */
				String access_token=StatusDetail.this.access_token;
				intent.putExtra("access_token", access_token);
				intent.setClass(StatusDetail.this, Retweet.class);
				startActivityForResult(intent,STATUSDETAIL_REPOST);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==STATUSDETAIL_COMMENT){
			
			if(resultCode==STATUSDETAIL_COMMENT_SUCCESS){
				/**評論數加1 */
				int total=Integer.parseInt(comments.getText().toString())+1;
				comments.setText(total+"");
			}else if(resultCode==STATUSDETAIL_COMMENT_FAIL){
				
			}
			
		}
		if(requestCode==STATUSDETAIL_REPOST){
			if(resultCode==STATUSDETAIL_REPOST_SUCCESS){
				/**轉發數加1 */
				int rtotal=Integer.parseInt(rt.getText().toString())+1;
				rt.setText(rtotal+"");
			}else if(resultCode==STATUSDETAIL_REPOST_FAIL){
				
			}
		}
	}

	private void initViews() {
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.putExtra("uid", userid);
				intent.putExtra("access_token", access_token);
				intent.putExtra("currentTag", 0);
				intent.setClass(StatusDetail.this, TimeLine.class);
				startActivity(intent);
				finish();
			}
		});
		avatar = (ImageView) findViewById(R.id.status_profile_image);
		name = (TextView) findViewById(R.id.status_screen_name);
		text = (TextView) findViewById(R.id.status_text);
		image = (ImageView) findViewById(R.id.status_microBlogImage);
		source = (TextView) findViewById(R.id.status_from);
		created_at = (TextView) findViewById(R.id.status_created_at);
		verified = (ImageView) findViewById(R.id.status_vipImage);
		comments = (TextView) findViewById(R.id.status_commentsCount);
		rt = (TextView) findViewById(R.id.status_repostsCount);
		ll_lyt = (LinearLayout) findViewById(R.id.status_retweeted_status_ll);
		rt_Image = (ImageView) findViewById(R.id.status_retweeted_status_microBlogImage);
		rt_text = (TextView) findViewById(R.id.status_retweeted_status_text);
	}

	private void setData2Views(Status status) {
		Log.v(TAG, status.toString());
		User user = status.getUser();
		Status retweetedStatus = null;
		if (status.getRetweetedStatus() != null) retweetedStatus = status.getRetweetedStatus();
		String profile_url = user.getProfileImageUrl();
		String name_value = user.getName();
		String text_value = status.getText();
		String image_url = status.getBmiddlePic();
		String source_value = status.getSource().getName();
		Boolean verified_value = user.isVerified();
		
		/**評論數 */
		int statuses_value = status.getCommentsCount();
		/** 轉發數 */
		int followers_value = status.getRepostsCount();
		String created_at_value = TimeUtil.converTime(new Date(status.getCreatedAt()).getTime() / 1000);
		String rt_text_value = "";
		if (retweetedStatus != null) {
			rt_text_value = retweetedStatus.getText();
			Log.v(TAG, "rt_text:  " + rt_text_value);
		} else {
			// do nothing
		}
		String rt_Image_url = "";
		if (retweetedStatus != null) {
			rt_Image_url = retweetedStatus.getBmiddlePic();
			Log.v(TAG, "rt_Image:  " + rt_Image_url);
		} else {
			// do nothing
		}
		if (!profile_url.equals("")) imageLoader.displayImage(profile_url, avatar, options);
		name.setText(name_value);
		text.setText(text_value);
		if (!image_url.equals("")) imageLoader.displayImage(image_url, image, options);
		else image.setVisibility(View.GONE);
		source.setText(source_value);
		created_at.setText(created_at_value);
		/**評論數 */
		comments.setText(statuses_value + "");
		/**轉發數 */
		rt.setText(followers_value + "");
		if (verified_value) verified.setVisibility(View.VISIBLE); 
		else verified.setVisibility(View.GONE);

		if (!rt_Image_url.equals("")) {
			rt_Image.setVisibility(View.VISIBLE);
			imageLoader.displayImage(rt_Image_url, rt_Image, options);
		} else rt_Image.setVisibility(View.GONE);

		if (!rt_text_value.equals("")) {
			rt_text.setVisibility(View.VISIBLE);
			rt_text.setText(rt_text_value);
		} else rt_text.setVisibility(View.GONE);
		if (!rt_Image_url.equals("") || !rt_text_value.equals("")) ll_lyt.setVisibility(View.VISIBLE);
		else ll_lyt.setVisibility(View.GONE);

		Log.v(TAG, "verified_value:  " + verified_value);
		Log.v(TAG, "statuses_value:  " + statuses_value);
		Log.v(TAG, "followers__value:  " + followers_value);
		Log.v(TAG, "created_at_value:  " + created_at_value);
		Log.v(TAG, "profile_url:  " + profile_url);
		Log.v(TAG, "name_value:  " + name_value);
		Log.v(TAG, "text_value:  " + text_value);
		Log.v(TAG, "image_url:  " + image_url);
		Log.v(TAG, "source_value:  " + source_value);

	}
}
