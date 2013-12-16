package com.login.weibo.ui;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.login.weibo.bean.Oauth2AccessToken;
import com.login.weibo.ui.R;
import com.weibo.exception.WeiboException;
import com.weibo.net.RequestListener;
import com.weibo.sdk.android.api.CommentsAPI;
public class Comment extends Activity {
	private ImageView writeBackBtn;
	private Button submit;

	private EditText editText;
	private TextView mTextNum;

	private String jsonData;
	private String sign = null;
	private JSONObject jsonObj = null;
	private final int WEIBO_MAX_LENGTH = 140;
	
	private CommentsAPI comm;
	private Handler handle;
	private String uid;
    private String token;
    
    private static final int STATUSDETAIL_COMMENT_SUCCESS=0Xa2;
	private static final int STATUSDETAIL_COMMENT_FAIL=0Xa3;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weibo_comment);
		
		Intent intent=getIntent();
		/**獲取微博uid */
		uid=intent.getStringExtra("uid");
		token=intent.getStringExtra("access_token");
		
		handle=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		mTextNum = (TextView) findViewById(R.id.comment_text_limit);
		
		/**返回 */
		writeBackBtn = (ImageView) findViewById(R.id.comment_writeBackBtn);
		writeBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		/**評論內容 */
		editText = (EditText) findViewById(R.id.microBlog_ed);
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String mText = editText.getText().toString();
				// String mStr;
				int len = mText.length();
				if (len <= WEIBO_MAX_LENGTH) {
					len = WEIBO_MAX_LENGTH - len;
					mTextNum.setTextColor(Color.GRAY);
					if (!submit.isEnabled())
						submit.setEnabled(true);
				} else {
					len = len - WEIBO_MAX_LENGTH;

					mTextNum.setTextColor(Color.RED);
					if (submit.isEnabled())
						submit.setEnabled(false);
				}
				mTextNum.setText(String.valueOf(len));
			}
		});
		submit = (Button) findViewById(R.id.sendBtn);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Oauth2AccessToken auth=new Oauth2AccessToken();
				auth.setToken(token);
				
				String comment=editText.getText().toString();
				comm=new CommentsAPI(auth);
				long id=Long.parseLong(uid);
				comm.create(comment, id, true, new RequestListener() {
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
						JSONObject comment;
						Message msg=new Message();
						try {
							comment= (JSONObject) new JSONTokener(response).nextValue();
							if(comment.isNull("error")){
								/**評論成功 */
								msg.what=STATUSDETAIL_COMMENT_SUCCESS;
							}else{
								/**評論失敗 */
								msg.what=STATUSDETAIL_COMMENT_FAIL;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							msg.what=STATUSDETAIL_COMMENT_FAIL;
						}finally{
							handle.sendMessage(msg);
						}
					}
				});
			}
		});
		
		handle=new Handler(){
			public void handleMessage(Message msg) {
				int what=msg.what;
				/**評論成功 */
				if(what==STATUSDETAIL_COMMENT_SUCCESS){
					Comment.this.setResult(STATUSDETAIL_COMMENT_SUCCESS);
					Toast.makeText(Comment.this, "評論成功", Toast.LENGTH_LONG).show();
				}
				/**評論失敗 */
				if(what==STATUSDETAIL_COMMENT_FAIL){
					Comment.this.setResult(STATUSDETAIL_COMMENT_FAIL);
					Toast.makeText(Comment.this, "評論失敗", Toast.LENGTH_LONG).show();
				}
				finish();
			}
	    };
	}
}