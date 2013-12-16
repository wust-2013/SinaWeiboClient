package com.login.weibo.ui;

import java.io.IOException;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.login.util.NewDataToast;
import com.login.util.UIHelper;
import com.login.weibo.adapter.ListViewAdapter;
import com.login.weibo.adapter.PullToRefreshListView;
import com.login.weibo.bean.Oauth2AccessToken;
import com.login.weibo.bean.Status;
import com.login.weibo.bean.util.StatusesUtil;
import com.login.weibo.ui.More;
import com.login.weibo.ui.R;
import com.login.weibo.ui.StatusDetail;
import com.login.weibo.ui.Tweet;
import com.login.weibo.ui.UserInfoActivity;
import com.weibo.exception.WeiboException;
import com.weibo.net.RequestListener;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TimeLine extends Activity {
	private static final long serialVersionUID = 1L;
	
	private final static String TAG = "BaseTimeLine";
	public final int REFRESH_LIST = 0;

	/**  头部工具栏     */
	public ImageView writeBtn = null;
	public ProgressBar mHeadProgress = null;
	public TextView titleTV = null;

	/**  中部ListView组件和适配器     */
	public PullToRefreshListView pullToRefreshListView;
	public View listView_footer;
	public TextView listView_foot_more;
	public ProgressBar listView_foot_progress;
	public static String jsonData;

	/**  底部导航栏     */
	public View friendTimeLine;

	public View userTimeLine;
	public View userNews;
	public View userInfo;
	public View more;
	public int mCurFooterTab = -1;

	private String mData = "";
	private int pageSum = 0;
	public Handler listViewHandler;
	private ListViewAdapter mAdapter = null;
	
	private List<Status> mStatusList = new ArrayList<Status>();
	private List<Status> friends_t=new ArrayList<Status>();
	private List<Status> public_t=new ArrayList<Status>();
	private List<Status> user_t=new ArrayList<Status>();
	
	private String access_token="";
	private String uid="";
	private Oauth2AccessToken auth=new Oauth2AccessToken();
	private StatusesAPI statues;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getLayout());

		Intent i = getIntent();
		int currentTag = i.getIntExtra("currentTag", 0);
		access_token=i.getStringExtra("access_token");
		/**用戶id */
		uid=i.getStringExtra("uid");
		
		auth.setToken(access_token);
		auth.setUid(uid);
		statues=new StatusesAPI(auth);
		
		initHeader();
		initCenter();
		initFooter();

		setSelectedFooterTab(currentTag);

		initSinaData();
	}

	public int getLayout() {
		return R.layout.weibo_timeline;
	}

	private void initHeader() {
		// -------头部工具栏----------------------------------
		writeBtn = (ImageView) findViewById(R.id.weibo_writeBtn);
		writeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("access_token", access_token);
				intent.setClass(TimeLine.this, Tweet.class);
				startActivityForResult(intent, REFRESH_LIST);
			}
		});

		titleTV = (TextView) findViewById(R.id.weibo_title_TV);
		mHeadProgress = (ProgressBar) findViewById(R.id.weibo_refreshBtn);
	}

	private void initCenter() {
		/** 中部ListView和适配器 */
		listView_footer = getLayoutInflater().inflate(R.layout.listview_footer,null);

		listView_foot_more = (TextView) listView_footer.findViewById(R.id.listview_foot_more);
		listView_foot_progress = (ProgressBar) listView_footer.findViewById(R.id.listview_foot_progress);

		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listview);
		pullToRefreshListView.addFooterView(listView_footer);
	}

	private void initFooter() {
		/** 底部导航栏  */
		friendTimeLine = findViewById(R.id.weibo_menu_friendTimeLine);
		friendTimeLine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setSelectedFooterTab(0);
				loadSinaLvData(0, "1", listViewHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});

		userTimeLine = findViewById(R.id.weibo_menu_userTimeLine);
		userTimeLine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setSelectedFooterTab(1);
				loadSinaLvData(1, "1", listViewHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});

		userNews = findViewById(R.id.weibo_menu_userNews);
		userNews.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v(TAG, "userNews");
				setSelectedFooterTab(2);
				loadSinaLvData(2, "1", listViewHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});

		userInfo = findViewById(R.id.weibo_menu_myInfo);
		userInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v(TAG, "userInfo");
				Intent i = new Intent();
				
				i.putExtra("currentTag", 3);
				i.putExtra("access_token", access_token);
				i.putExtra("uid", uid);
				
				i.setClass(getApplicationContext(), UserInfoActivity.class);
				startActivity(i);
				finish();
			}
		});

		more = findViewById(R.id.weibo_menu_more);
		more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.putExtra("currentTag", 4);
				i.setClass(getApplicationContext(), More.class);
				startActivity(i);
				finish();
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	protected void setSelectedFooterTab(int i) {
		mCurFooterTab = i;

		friendTimeLine.setBackgroundResource(0);
		userTimeLine.setBackgroundResource(0);
		userNews.setBackgroundResource(0);
		userInfo.setBackgroundResource(0);
		more.setBackgroundResource(0);

		if (i == 0) {
			friendTimeLine.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);
			titleTV.setText("微博主页");
		}
		if (i == 1) {
			userTimeLine.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);
			titleTV.setText("我的微博");
		}
		if (i == 2) {
			userNews.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);
			titleTV.setText("微博动态");
		}
		if (i == 3)
			userInfo.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);
		if (i == 4)
			more.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);
	}

    /**
     * 在首頁中加載數據,并顯示出來
     * @param token   授權后的用訪問api的access_token
     */
	private void initSinaData() {
//		listViewHandler=this.getLvHandler(pullToRefreshListView, null, listView_foot_more, listView_foot_progress);
		Handler initH=new Handler(){
			public void handleMessage(Message msg) {
				List<Status> list = (List<Status>) msg.obj;
				friends_t.addAll(0,list);
				mStatusList.addAll(0,list);
				
				/**首次加载完数据后*/
				mAdapter = new ListViewAdapter(TimeLine.this, mStatusList);
				listViewHandler=TimeLine.this.getLvHandler(pullToRefreshListView, mAdapter, listView_foot_more, listView_foot_progress);
				pullToRefreshListView.setAdapter(mAdapter);
				
				pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Log.v(TAG, "position: " + (position - 1));
						// 点击头部、底部栏无效
						if (position == 0 || view == listView_footer)
							return;

						// 跳转到微博详情
						Intent intent = new Intent();
						Bundle mBundle = new Bundle();
						mBundle.putSerializable("detail",mStatusList.get(position - 1));
						intent.putExtras(mBundle);
						intent.putExtra("access_token", access_token);
						intent.putExtra("uid", uid);
						intent.putExtra("currentTag", 0);
						intent.setClass(TimeLine.this, StatusDetail.class);
						startActivity(intent);
						finish();
					}
				});  
				
				pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
					@Override
					public void onScrollStateChanged(AbsListView view,
							int scrollState) {
						// 数据为空--不用继续下面代码了
						if (mStatusList.isEmpty())
							return;

						// 判断是否滚动到底部
						boolean scrollEnd = false;
						try {
							if (view.getPositionForView(listView_footer) == view.getLastVisiblePosition())
								scrollEnd = true;
						} catch (Exception e) {
							scrollEnd = false;
						}

						if (scrollEnd) {
							pullToRefreshListView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
							listView_foot_more.setText(R.string.load_ing);
							listView_foot_progress.setVisibility(View.VISIBLE);

							loadSinaLvData(mCurFooterTab,"1", listViewHandler,UIHelper.LISTVIEW_ACTION_SCROLL);
						}
					}

					@Override
					public void onScroll(AbsListView view,
							int firstVisibleItem, int visibleItemCount,
							int totalItemCount) {
					}
				});
				
				pullToRefreshListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						loadSinaLvData(mCurFooterTab, "1", listViewHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
					}
				}); 
			}
			
		};
		/**首次加载数据 */
		loadSinaLvData(mCurFooterTab,"1" , initH, UIHelper.LISTVIEW_ACTION_INIT);
	}
	
	/**
	 * 获取listview的初始化Handler
	 * 
	 * @param lv
	 * @param adapter
	 * @return
	 */
	private Handler getLvHandler(final PullToRefreshListView lv,
			final BaseAdapter adapter, final TextView more,
			final ProgressBar progress) {
		return new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what >= 0) {
					// listview数据处理
					handleSinaLvData(msg.what, msg.obj, msg.arg2, msg.arg1);
					if (msg.what < 15) {
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_full);// 已经加载完毕
					} else if (msg.what == 15) {
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_more);
					}
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					more.setText(R.string.load_error);
				}

				if (adapter.getCount() == 0) {
					lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					more.setText(R.string.load_empty);
				}
				
				progress.setVisibility(View.GONE);
				mHeadProgress.setVisibility(View.GONE);
				
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					lv.onRefreshComplete(getString(R.string.pull_to_refresh_update)
							+ new Date().toLocaleString());
					lv.setSelection(0);
				} else if (msg.arg1 == UIHelper.LISTVIEW_ACTION_SCROLL) {
					lv.onRefreshComplete();
				}
			}
		};
	}

	/**
	 * 在列表中加载微博信息
	 * @param what
	 *       获取的微博消息的数量
	 * @param obj
	 *       获取的微博数据
	 * @param dataType
	 *       通过哪类接口获取的微博
	 * @param actionType
	 *       哪种事件触发微博的加载
	 */
	private void handleSinaLvData(int what, Object obj, int dataType,int actionType){
		/**获取加载的数据 */
		List<Status> newList = (List<Status>) obj;
		Log.v("handleSinaLvData",newList.get(0).toString());
		int newData=0;
		mStatusList.clear();
		switch(actionType){
		case UIHelper.LISTVIEW_ACTION_REFRESH:
		case UIHelper.LISTVIEW_ACTION_SCROLL:
			
			/**获取的数据通过 friends_timeline 接口获取  */
			if(dataType==UIHelper.LISTVIEW_DATATYPE_FRIENDS_TIMElINE){
				/**记录当前登录用户关注的好友的微博刷新了多少条记录 */
					for (Status status : newList) {
						boolean b = false;
						for (Status status2 : friends_t) {
							if (status.getId().equals(status2.getId())) {
								b = true;
								break;
							}
						}
						if (!b){
							newData++;
							/**将关注好友微博添加到friends_t列表　  */
							friends_t.add(0,status);
						}
					}
					/**更新微博列表 */
//					mStatusList.clear();
					mStatusList.addAll(0,friends_t);
			}
			
			/**获取的数据通过 user_timeline 接口获取  */
			if(dataType==UIHelper.LISTVIEW_DATATYPE_USER_TIMElINE){
				if(user_t.size()==0){
					newData=newList.size();
					user_t.addAll(0, newList);
				}else
				for (Status status : newList) {
					boolean b = false;
					for (Status status2 : user_t) {
						if (status.getId().equals(status2.getId())) {
							b = true;
							break;
						}
					}
					if (!b) {
						/** 记录更新的数目 */
						newData++;
						user_t.add(0, status);
					}
				}
					/**更新微博列表 */
					mStatusList.addAll(0,user_t);
			}
			
			/**获取的数据通过 public_timeline 获取 */
			if(dataType==UIHelper.LISTVIEW_DATATYPE_PUBLIC_TIMElINE){
				if(public_t.size()==0){
					newData=newList.size();
					public_t.addAll(0, newList);
				}else
				for (Status status : newList) {
					boolean b = false;
					for (Status status2 : public_t) {
						if (status.getId().equals(status2.getId())) {
							b = true;
							break;
						}
					}
					if (!b){
						/**记录更新的数目 */
						newData++;
						public_t.add(0,status);
					}
				}
				/**更新微博列表 */
				mStatusList.addAll(0,public_t);
			}
			
			/**用于提示微博有无更新 */
			if(newData>0){
				/**显示更新的微博数目  */
				NewDataToast.makeText(this,getString(R.string.new_data_toast_message,newData), true).show();
			}else{
				/**没有更新 */
				NewDataToast.makeText(this,getString(R.string.new_data_toast_none), false).show();
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REFRESH_LIST) {
			if (resultCode == Tweet.TWEET_OK) {
				/**微博发布成功，刷新列表 */
				loadSinaLvData(mCurFooterTab, "1", listViewHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 通过接口获取微博消息
	 * @param currentTag
	 *            模块标识
	 * @param pageIndex
	 *            页码，默认为1
	 * @param handler
	 *            用于发送消息
	 * @param action
	 *            哪种事件触发微博的加载
	 */
	private void loadSinaLvData(final int currentTag, final String pageIndex,
			final Handler handler, final int actionType){
		switch(actionType){
		     /**加载 friendtimeline接口 */
		     case UIHelper.LISTVIEW_ACTION_INIT:
		     case UIHelper.LISTVIEW_ACTION_REFRESH:
		     case UIHelper.LISTVIEW_ACTION_SCROLL:
		    	 /**判断加载的接口类型 */
		    	 switch(currentTag){
		    	   case 0:
		    		   /**将获取的数据返回给handler处理 */
				    	 statues.friendsTimeline(0, 0, 5, 1, false, WeiboAPI.FEATURE.ALL, false, new RequestListener() {
							public void onIOException(IOException e) {}
							public void onError(WeiboException e) {}
							public void onComplete(String response) {
								sendDataMessage(response,handler,actionType,UIHelper.LISTVIEW_DATATYPE_FRIENDS_TIMElINE);
							}
						});
		    		   break;
		    	   case 1:
		    		   statues.userTimeline(0, 0, 5, Integer.parseInt(pageIndex), false, WeiboAPI.FEATURE.ALL, false, new RequestListener() {
		    			  public void onIOException(IOException e) {}
						  public void onError(WeiboException e) {}
						  public void onComplete(String response) {
							  sendDataMessage(response,handler,actionType,UIHelper.LISTVIEW_DATATYPE_USER_TIMElINE);
						}
					});
		    		   break;
		    	   case 2:
		    		   statues.publicTimeline(5, 1, false, new RequestListener() {
		    			   public void onIOException(IOException e) {}
						   public void onError(WeiboException e) {}
						   public void onComplete(String response) {
							   sendDataMessage(response,handler,actionType,UIHelper.LISTVIEW_DATATYPE_PUBLIC_TIMElINE);
						  }
					});
		    		   break;
		    	 }
		}
	}
	
	/**
	 * 将解析的数据，封装成Message,通过Handler发送
	 * @param response  要解析的数据String
	 * @param handler   发送消息的Handler
	 * @param actionType 启动加载数据的类型
	 */
	private void sendDataMessage(String response,Handler handler,int actionType,int dataType){
		Message msg=new Message();
		try {
			List<Status> newData=StatusesUtil.getStatuses(response).getStatuses();
			msg.what = newData.size();
			msg.obj = newData;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.v("TimeLine-RequestListener","onComplete");
			e.printStackTrace();
			msg.what = -1;
			msg.obj = e;
		}
		msg.arg1 = actionType;
		msg.arg2 = dataType;
		handler.sendMessage(msg);
	}
}