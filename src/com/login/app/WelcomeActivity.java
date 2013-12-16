package com.login.app;

import com.login.weibo.ui.AccountActivity;
import com.login.weibo.ui.AuthorizeActivity;
import com.login.weibo.ui.BaseActivity;
import com.login.weibo.ui.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class WelcomeActivity extends BaseActivity {
	private Boolean isDBAvaliable = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(getLayout());
		
		ImageView iv = (ImageView) this.findViewById(R.id.logo_bg);
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(1000);
		iv.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				if (mDBManager.getAccounts().size() != 0) {
					isDBAvaliable = true;
				}
				if (isDBAvaliable) {
					Intent intent = new Intent();
					intent.setClass(WelcomeActivity.this, AccountActivity.class);
					startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent();
					intent.setClass(WelcomeActivity.this, AuthorizeActivity.class);
					startActivity(intent);
					finish();
				}
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public int getLayout() {
		return R.layout.welcome_activity;
	}

}