package com.login.weibo.ui.more;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.login.weibo.ui.R;
/**
 * 主题
 * 
 */
public class ThemeDialog extends Activity{

	private RadioGroup radioGroup=null;
	private RadioButton theme1=null;
	private RadioButton theme2=null;
	private RadioButton theme3=null;
	private Button button=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_theme);
		radioGroup=(RadioGroup)findViewById(R.id.radioGroupTheme);
		theme1=(RadioButton)findViewById(R.id.radioThemeOne);
		theme2=(RadioButton)findViewById(R.id.radioThemeTwo);
		theme3=(RadioButton)findViewById(R.id.radioThemeThree);
		
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(theme1.getId()==checkedId){
					System.out.println("theme1");
				}
				else if(theme2.getId()==checkedId){
					System.out.println("theme2");
				}
				else if(theme3.getId()==checkedId){
					System.out.println("theme3");
				}
			}
		});
		
		button=(Button)findViewById(R.id.surebutton);
		button.setOnClickListener(new ButtonListener());

	}

	class ButtonListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			finish();
		}

	}
}
