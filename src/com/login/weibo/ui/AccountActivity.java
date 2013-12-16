package com.login.weibo.ui;

import greendroid.widget.MyQuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.login.util.DBManager;
import com.login.weibo.adapter.AccountAdapter;
import com.login.weibo.bean.Account;
/**
 * 账号列表
 */
public class AccountActivity extends Activity {
	private final String TAG = "AccountActivity";
	
	public DBManager mDBManager;
	
	private ArrayList<Account> listData = null;
	private ListView lv;
	private AccountAdapter adapter = null;
	
	private TextView title = null;
	private ProgressBar refreshBtn = null;
	private ImageView write = null;
	
	private View mAddBtn = null;
	private View mDelBtn = null;
	private View mExitBtn = null;
	
	private QuickActionWidget mGrid;//快捷栏控件

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		
		mDBManager = new DBManager(getApplicationContext());
		initQuickActionGrid();
		
		title = (TextView) findViewById(R.id.weibo_title_TV);
		title.setText(R.string.account_list);
		title.setTextSize(25);
		
		refreshBtn = (ProgressBar) findViewById(R.id.weibo_refreshBtn);
		refreshBtn.setVisibility(View.INVISIBLE);
		
		write = (ImageView) findViewById(R.id.weibo_writeBtn);
		write.setVisibility(View.INVISIBLE);
		
		lv = (ListView) findViewById(R.id.account_lv);
		listData = (ArrayList<Account>) mDBManager.getAccounts();
		adapter = new AccountAdapter(this, listData,mDBManager);
		lv.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		mAddBtn = findViewById(R.id.account_add);
		mAddBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mGrid.show(v);	
			}
		});
		
		 mDelBtn=findViewById(R.id.account_delete);
		 mDelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder delete = new AlertDialog.Builder(AccountActivity.this);
				delete.setTitle("提醒：");
				delete.setIcon(R.drawable.icon);
				delete.setMessage("将删除选中的账户信息，确定吗？");
				delete.setPositiveButton("删除", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialoginterface, int which) {
								adapter.notifyDataSetChanged();
							}
						});
				delete.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialoginterface, int which) {}
						});
				delete.create().show();
				
			}
		});
		 
		mExitBtn = findViewById(R.id.account_exit);
		mExitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder exit = new AlertDialog.Builder(AccountActivity.this);
				exit.setTitle("提醒：");
				exit.setIcon(R.drawable.icon);
				exit.setMessage("确定要退出吗？");
				exit.setPositiveButton("退出", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialoginterface, int which) {
								System.exit(0);
							}
						});
				exit.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialoginterface, int which) {}
						});
				exit.create().show();
			}
		});
	}
	
	 /**
     * 初始化快捷栏
     */
    private void initQuickActionGrid() {
        mGrid = new QuickActionGrid(this);
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.sina3, R.string.sina));
        mGrid.setOnQuickActionClickListener(mActionListener);
    }
    
    public static final int QUICKACTION_SINA = 0;
    public static final int QUICKACTION_TENCENT = 1;
    
    /**
     * 快捷栏item点击事件
     */
    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        @Override
		public void onQuickActionClicked(QuickActionWidget widget, int position) {
    		Intent intent = null;
    		switch (position) {
    		case QUICKACTION_SINA:
    			intent = new Intent(AccountActivity.this, AuthorizeActivity.class);
				startActivity(intent);
    			break;
    		}
        }
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		mDBManager.closeDB();// this mDBManager serves for sina 
		super.onDestroy();
	}
	
	public int getLayout() {
		return R.layout.account_activity;
	}
}