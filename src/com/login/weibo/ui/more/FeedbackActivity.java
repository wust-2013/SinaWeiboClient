package com.login.weibo.ui.more;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.login.weibo.ui.R;
/**
 * 反馈
 */
public class FeedbackActivity extends Activity{

	private ImageView back=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_feedback);
		back=(ImageView)findViewById(R.id.back);
		back.setOnClickListener(new BackListener());

	}

	class BackListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	}

}
