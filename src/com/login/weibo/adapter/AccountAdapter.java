package com.login.weibo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.login.util.DBManager;
import com.login.weibo.bean.Account;
import com.login.weibo.ui.BaseActivity;
import com.login.weibo.ui.R;
import com.login.weibo.ui.TimeLine;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.weibo.net.Weibo;

public class AccountAdapter extends BaseAdapter {
	private final String TAG = "AccountAdapter";
	
	private ArrayList<Account> mData;
	private ViewHolder holder;
	private Context ctx;
	private ArrayList<Boolean> deleteData ;
	private DBManager mDBManager ;
	
	//Universal Image Loader for Android 第三方框架组件
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	public AccountAdapter(Context ctx, ArrayList<Account> data,DBManager mDBManager) {
		this.ctx = ctx;
		this.mData = data;
		int size = data.size();
		this.deleteData = new ArrayList<Boolean>(size);
		this.mDBManager =mDBManager;

		for (int i = 0; i < size; i++) {
			deleteData.add(i, false);
		}
		
		 options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.icon)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer(10))
		.build();
	}

	@Override
	public int getCount() {
		return this.mData != null ? this.mData.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	    final Account mAccount = mData.get(position);
		String profile_image_url = mAccount.getUrl() ;
		String name = mAccount.getScreen_name();
		
		if (convertView == null) {
			holder = new ViewHolder();
			
			LayoutInflater mInflater = LayoutInflater.from(ctx);
			convertView = mInflater.inflate(R.layout.account_lv_item, null);
			
			holder.name = (TextView) convertView.findViewById(R.id.account_tv);
			holder.profile_image = (ImageView) convertView.findViewById(R.id.account_iv);
			holder.cb = (CheckBox) convertView.findViewById(R.id.cb);
			holder.ll = (LinearLayout) convertView.findViewById(R.id.account_rl);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!profile_image_url.equals("")) {
			imageLoader.displayImage(profile_image_url, holder.profile_image, options);
		} else{
			holder.profile_image.setImageResource(R.drawable.icon);
		}

		if (!name.equals("")){
			holder.name.setText(name);
		}else{
			holder.name.setText("空值");
		}
		
		holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.v(TAG, "checkBox selected: "+isChecked+" and position is:  "+ position);
				deleteData.set(position, isChecked);
			}
		});
		
		Integer[] mSelector = { android.R.color.transparent,R.drawable.listview_item_selector_bg, R.drawable.listview_item_selector_bg };
		ModifiedLinearLayout mLL = new ModifiedLinearLayout(ctx);
		
		holder.ll.setBackgroundDrawable(mLL.setbg(mSelector));
		holder.ll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String token = mAccount.getAccess_token();
//				final String expires_in = mAccount.getExpires_in();
				final String uid=mAccount.getUid();
				
				Intent i = new Intent();
				i.putExtra("currentTag", 0);
				i.putExtra("access_token",token);
				i.putExtra("uid", uid);
				i.setClass(ctx.getApplicationContext(), TimeLine.class);
				ctx.startActivity(i);
			}
		});
		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		int size = deleteData.size();
		Boolean isShow = true ;
		for (int i = 0; i < size; i++) {
			Boolean deletable = deleteData.get(i);
			if (deletable) {
				Account account = mData.get(i);
				mDBManager.deleteAccount(account);
				mData.remove(i);
				isShow = false ; 
			}
		}
		if (isShow)Toast.makeText(ctx, "未选中任何选项", Toast.LENGTH_SHORT).show();
		super.notifyDataSetChanged();
	}
	
	static class ViewHolder {
		private ImageView profile_image;
		private TextView name;
		private CheckBox cb = null;
		private LinearLayout ll = null;
	}

    class ModifiedLinearLayout extends View {
        public ModifiedLinearLayout(Context context) {
            super(context);
        }
        // 以下这个方法也可以把你的图片数组传过来，以StateListDrawable来设置图片状态，来表现button的各中状态。未选中，按下，选中效果。
        public StateListDrawable setbg(Integer[] mImageIds) {
            StateListDrawable bg = new StateListDrawable();
            Drawable normal = this.getResources().getDrawable(mImageIds[0]);
            Drawable selected = this.getResources().getDrawable(mImageIds[1]);
            Drawable pressed = this.getResources().getDrawable(mImageIds[2]);
            bg.addState(View.PRESSED_ENABLED_STATE_SET, pressed);
            bg.addState(View.ENABLED_FOCUSED_STATE_SET, selected);
            bg.addState(View.ENABLED_STATE_SET, normal);
            bg.addState(View.FOCUSED_STATE_SET, selected);
            bg.addState(View.EMPTY_STATE_SET, normal);
            return bg;
        }
    }
}
