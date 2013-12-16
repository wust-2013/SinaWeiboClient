package com.login.weibo.ui;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.login.weibo.bean.Oauth2AccessToken;
import com.login.weibo.bean.Status;
import com.login.weibo.ui.R;
import com.weibo.exception.WeiboException;
import com.weibo.net.RequestListener;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI.COMMENTS_TYPE;
/**
 * 转发
 */
public class Retweet extends Activity {
	private Button redirectBtn;
	private ImageButton backButton;
	// 要转发的微博的内容
	private String text = null;
	private EditText editText = null;
	private View delword_ll = null;
	private final int WEIBO_MAX_LENGTH = 140;
	private TextView mTextNum = null;
	
	Status mStatus;
	Handler handle;
	String mToken;
	
	private static final int STATUSDETAIL_REPOST_SUCCESS=0xb2;
	private static final int STATUSDETAIL_REPOST_FAIL=0xb3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weibo_retweet);
		
		Intent intent = getIntent();
	    mStatus = (Status) intent.getSerializableExtra("detail");
	    mToken=intent.getStringExtra("access_token");
		
		/**发布微博 的内容 */
		mTextNum = (TextView) findViewById(R.id.redirec_text_limit);
		
		/**清空微博的内容 */
		delword_ll = findViewById(R.id.delword_ll);
		delword_ll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editText.setText("");
			}
		});
		
		/**退出转发微博页面 */
		backButton = (ImageButton) findViewById(R.id.writeBackBtn);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		/**转发 */
		redirectBtn = (Button) findViewById(R.id.redirectBtn);
		redirectBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Oauth2AccessToken auth=new Oauth2AccessToken();
				auth.setToken(mToken);
				
				long id=Long.parseLong(mStatus.getId());
				String status=editText.getText().toString();
				
				StatusesAPI st=new StatusesAPI(auth);
				st.repost(id, status, COMMENTS_TYPE.NONE, new RequestListener() {
					@Override
					public void onIOException(IOException e) {}
					public void onError(WeiboException e) {}
					public void onComplete(String response) {
						JSONObject user;
						Message msg=new Message();
						try {
							user = (JSONObject) new JSONTokener(response).nextValue();
							if(user.isNull("error")){
						    	/**微博发布成功 */
						    	msg.what=STATUSDETAIL_REPOST_SUCCESS;
						    }else{
						    	/**微博发布失败 */
						    	msg.what=STATUSDETAIL_REPOST_FAIL;
						    }
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							msg.what=STATUSDETAIL_REPOST_FAIL;
						}finally{
							handle.sendMessage(msg);
						}
					}
				});
			}
		});
		
		/**要转发的微博内容 */
		editText = (EditText) findViewById(R.id.microBlog_ed);
		editText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String mText = editText.getText().toString();
				int len = mText.length();
				if (len <= WEIBO_MAX_LENGTH) {
					len = WEIBO_MAX_LENGTH - len;
					mTextNum.setTextColor(Color.GRAY);
					if (!redirectBtn.isEnabled())
						redirectBtn.setEnabled(true);
				} else {
					len = len - WEIBO_MAX_LENGTH;

					mTextNum.setTextColor(Color.RED);
					if (redirectBtn.isEnabled())
						redirectBtn.setEnabled(false);
				}
				mTextNum.setText(String.valueOf(len));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		
		/**在界面中显示转发微博 的内容 */
		text=mStatus.getText();
	    editText.setText(text);
	    
	    handle=new Handler(){
			public void handleMessage(Message msg) {
				int what=msg.what;
				/**发布微博成功 */
				if(what==STATUSDETAIL_REPOST_SUCCESS){
					Retweet.this.setResult(STATUSDETAIL_REPOST_SUCCESS);
					Toast.makeText(Retweet.this, "轉發成功", Toast.LENGTH_LONG).show();
				}
				/**发布微博失败 */
				if(what==STATUSDETAIL_REPOST_FAIL){
					Retweet.this.setResult(STATUSDETAIL_REPOST_FAIL);
					Toast.makeText(Retweet.this, "轉發失敗", Toast.LENGTH_LONG).show();
				}
				finish();
			}
	    };
	    
	}
	
}
