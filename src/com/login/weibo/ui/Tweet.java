package com.login.weibo.ui;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.login.util.FileUtils;
import com.login.util.ImageUtils;
import com.login.util.MediaUtils;
import com.login.util.StringUtils;
import com.login.weibo.adapter.GridViewAdapter;
import com.login.weibo.bean.Oauth2AccessToken;
import com.login.weibo.ui.R;
import com.weibo.exception.WeiboException;
import com.weibo.net.RequestListener;
import com.weibo.sdk.android.api.StatusesAPI;

/**
 * 发布一条微博
 */
public class Tweet extends Activity {
	public static final int TWEET_OK = -1;
	private final String TAG = "Tweet";
	private final int MAX_TEXT_LENGTH = 140;
	private final String PREFERENCE_TEMP_DATA = "PREFERENCE_TEMP_DATA";
	private final String TEMP_TEXT_KEY = "TEMP_TWEET_KEY";
	private final String TEMP_IMAGE_KEY = "TEMP_IMAGE_KEY";
	private SharedPreferences preferences = null;

	private EditText mEditText;
	private TextView mTextNum;
	private ImageView mBack;
	private Button mPublishBtn;
	private ImageView mFace;
	private ImageView mPick;
	private ImageView mImage;
	private FrameLayout mForm;
	private View progressBar;
	private View write_delword_ll;
	private GridView mGridView;
	private InputMethodManager imm;
	private GridViewAdapter mGVAdapter;

	private Bitmap mBitmap = null;
	private File imgFile;
	private String theLarge;
	private String theThumbnail;

	private String access_token = "";
	private Handler handler;
	
	public int getLayout() {
		return R.layout.weibo_tweet;
	}

	@Override
	public void onBackPressed() {
		String str = this.mEditText.getText().toString();
		preferences = getSharedPreferences(PREFERENCE_TEMP_DATA, 0);
		SharedPreferences.Editor localEditor = this.preferences.edit();
		localEditor.putString(TEMP_TEXT_KEY, str);
		localEditor.commit();
		finish();
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(getLayout());
		
		Intent i=getIntent();
		access_token=i.getStringExtra("access_token");
		
		handler=new Handler(){
			public void handleMessage(Message msg) {
				int what=msg.what;
				if(what==TWEET_OK){
					Editor ed = preferences.edit();
					ed.clear();
					ed.commit();

					Tweet.this.setResult(TWEET_OK);
					Tweet.this.finish();
				}
			}
		};
		
		// 软键盘管理类
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		initView();
		initGridView();
	}

	// 初始化视图控件
	private void initView() {
		preferences = getSharedPreferences(PREFERENCE_TEMP_DATA, 0);

		mForm = (FrameLayout) findViewById(R.id.tweet_form);
		mEditText = ((EditText) findViewById(R.id.tweet_et));
		mTextNum = ((TextView) findViewById(R.id.tweet_no));
		mFace = ((ImageView) findViewById(R.id.tweet_face));
		mPick = ((ImageView) findViewById(R.id.tweet_pic));
		mImage = ((ImageView) findViewById(R.id.tweet_preview));
		mPublishBtn = ((Button) findViewById(R.id.tweet_publish));
		mBack = ((ImageButton) findViewById(R.id.tweet_back));
		progressBar = findViewById(R.id.tweet_progressbar);
		write_delword_ll = findViewById(R.id.tweet_del);

		write_delword_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View paramView) {
				Tweet.this.mEditText.setText("");
				mTextNum.setText("140");
			}
		});

		// 编辑器添加文本监听
		mEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable paramEditable) {
			}

			@Override
			public void beforeTextChanged(CharSequence paramCharSequence,
					int paramInt1, int paramInt2, int paramInt3) {
			}

			@Override
			public void onTextChanged(CharSequence paramCharSequence,
					int paramInt1, int paramInt2, int paramInt3) {
				// 显示剩余可输入的字数
				mTextNum.setText((MAX_TEXT_LENGTH - Tweet.this.mEditText
						.getText().length()) + "");
			}
		});

		// 编辑器点击事件
		mEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 显示软键盘
				showIMM();
			}
		});

		// 设置最大输入字数
		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter.LengthFilter(MAX_TEXT_LENGTH);
		mEditText.setFilters(filters);

		if (preferences != null) {
			mEditText.setText(preferences.getString(TEMP_TEXT_KEY, ""));
			// 设置光标位置
			mEditText.setSelection(preferences.getString(TEMP_TEXT_KEY, "")
					.length());
			// 显示临时保存图片
			String tempImage = getSharedPreferences(PREFERENCE_TEMP_DATA, 0)
					.getString(TEMP_IMAGE_KEY, null);
			if (!StringUtils.isEmpty(tempImage)) {
				Bitmap bitmap = ImageUtils
						.loadImgThumbnail(tempImage, 100, 100);
				if (bitmap != null) {
					imgFile = new File(tempImage);
					mImage.setImageBitmap(bitmap);
					mImage.setVisibility(View.VISIBLE);
				}
			}
		}

		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View paramView) {
				String str = mEditText.getText().toString();
				preferences = getSharedPreferences(PREFERENCE_TEMP_DATA, 0);
				SharedPreferences.Editor localEditor = preferences.edit();
				localEditor.putString(TEMP_TEXT_KEY, str);
				localEditor.commit();
				finish();
			}
		});

		mPublishBtn.setOnClickListener(publishClickListener);
		mImage.setOnLongClickListener(imageLongClickListener);
		mFace.setOnClickListener(faceClickListener);
		mPick.setOnClickListener(pickClickListener);
	}

	// 初始化表情控件
	private void initGridView() {
		mGridView = ((GridView) findViewById(R.id.gridView_faces));
		mGVAdapter = new GridViewAdapter(this);
		mGridView.setAdapter(mGVAdapter);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 插入的表情
				SpannableString ss = new SpannableString(view.getTag()
						.toString());
				Drawable d = getResources().getDrawable(
						(int) mGVAdapter.getItemId(position));
				d.setBounds(0, 0, 35, 35);// 设置表情图片的显示大小
				ImageSpan span = new ImageSpan(d,
						DynamicDrawableSpan.ALIGN_BOTTOM);
				ss.setSpan(span, 0, view.getTag().toString().length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				// 在光标所在处插入表情
				mEditText.getText().insert(mEditText.getSelectionStart(), ss);
			}
		});
	}

	private void showIMM() {
		mFace.setTag(1);
		showOrHideIMM();
	}

	private void showOrHideIMM() {
		if (mFace.getTag() == null) {
			// 隐藏软键盘
			imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
			// 显示表情
			showFace();
		} else {
			// 显示软键盘
			imm.showSoftInput(mEditText, 0);
			// 隐藏表情
			hideFace();
		}
	}

	private void showFace() {
		mFace.setImageResource(R.drawable.write_insert_face_sel);
		mFace.setTag(1);
		mGridView.setVisibility(View.VISIBLE);
	}

	private void hideFace() {
		mFace.setImageResource(R.drawable.write_insert_face_sel);
		mFace.setTag(null);
		mGridView.setVisibility(View.GONE);
	}

	private View.OnClickListener faceClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showOrHideIMM();
		}
	};

	private View.OnClickListener pickClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// 隐藏软键盘
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			// 隐藏表情
			hideFace();

			CharSequence[] items = { "本地上传", "拍照上传"
			// this.getString(R.string.img_from_album),
			// this.getString(R.string.img_from_camera)
			};
			imageChooseItem(items);
		}
	};

	private View.OnClickListener publishClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// 隐藏软键盘
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			String text = Tweet.this.mEditText.getText().toString();
			mForm.setVisibility(View.GONE);

			Tweet.this.progressBar.setVisibility(View.VISIBLE);

			/** 发布一条微博 */
			Oauth2AccessToken auth = new Oauth2AccessToken();
			auth.setToken(access_token);

			StatusesAPI publish = new StatusesAPI(auth);
			publish.update(text, null, null, new RequestListener() {
				public void onIOException(IOException e) {}
				public void onError(WeiboException e) {}
				public void onComplete(String response) {
					Message msg=new Message();
					msg.what=TWEET_OK;
					handler.sendMessage(msg);
				}
			});
		}
	};

	private View.OnLongClickListener imageLongClickListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			// 隐藏软键盘
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			new AlertDialog.Builder(v.getContext())
					.setIcon(android.R.drawable.ic_dialog_info)
					.setTitle("确定删除该图片？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 清除之前保存的编辑图片
									// ((AppContext)getApplication()).removeProperty(tempTweetImageKey);
									//
									// imgFile = null;
									mImage.setVisibility(View.GONE);
									dialog.dismiss();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create().show();
			return true;
		}

	};

	@Override
	protected void onResume() {
		super.onResume();
		if (mGridView.getVisibility() == View.VISIBLE) {
			// 隐藏表情
			hideFace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mGridView.getVisibility() == View.VISIBLE) {
				// 隐藏表情
				hideFace();
			} else {
				return super.onKeyDown(keyCode, event);
			}
		}
		return true;
	}

	/**
	 * 操作选择
	 * 
	 * @param items
	 */
	public void imageChooseItem(CharSequence[] items) {
		AlertDialog imageDialog = new AlertDialog.Builder(this)
				.setTitle("插入图片").setIcon(android.R.drawable.btn_star)
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						// 手机选图
						if (item == 0) {
							Intent intent = new Intent(
									Intent.ACTION_GET_CONTENT);
							intent.addCategory(Intent.CATEGORY_OPENABLE);
							intent.setType("image/*");
							startActivityForResult(
									Intent.createChooser(intent, "选择图片"),
									ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
						}
						// 拍照
						else if (item == 1) {
							String savePath = "";
							// 判断是否挂载了SD卡
							String storageState = Environment
									.getExternalStorageState();
							if (storageState.equals(Environment.MEDIA_MOUNTED)) {
								savePath = Environment
										.getExternalStorageDirectory()
										.getAbsolutePath()
										+ "/JustSharePro/Camera/";// 存放照片的文件夹
								File savedir = new File(savePath);
								if (!savedir.exists()) {
									savedir.mkdirs();
								}
							}

							// 没有挂载SD卡，无法保存文件
							if (StringUtils.isEmpty(savePath)) {
								Toast.makeText(Tweet.this, "无法保存照片，请检查SD卡是否挂载",
										Toast.LENGTH_SHORT).show();
								return;
							}

							String timeStamp = new SimpleDateFormat(
									"yyyyMMddHHmmss").format(new Date());
							String fileName = "jsp_" + timeStamp + ".jpg";// 照片命名
							File out = new File(savePath, fileName);
							Uri uri = Uri.fromFile(out);

							theLarge = savePath + fileName;// 该照片的绝对路径

							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							startActivityForResult(intent,
									ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
						}
					}
				}).create();

		imageDialog.show();
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (resultCode != RESULT_OK)
			return;

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1 && msg.obj != null) {
					// 显示图片
					mImage.setImageBitmap((Bitmap) msg.obj);
					mImage.setVisibility(View.VISIBLE);
				}
			}
		};

		new Thread() {
			@Override
			public void run() {
				Bitmap bitmap = null;

				if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD) {
					if (data == null)
						return;

					Uri thisUri = data.getData();
					String thePath = ImageUtils
							.getAbsolutePathFromNoStandardUri(thisUri);

					// 如果是标准Uri
					if (StringUtils.isEmpty(thePath)) {
						theLarge = ImageUtils.getAbsoluteImagePath(Tweet.this,
								thisUri);
					} else {
						theLarge = thePath;
					}

					String attFormat = FileUtils.getFileFormat(theLarge);
					if (!"photo".equals(MediaUtils.getContentType(attFormat))) {
						Toast.makeText(Tweet.this, "选择相片", Toast.LENGTH_SHORT)
								.show();
						return;
					}

					// 获取图片缩略图 只有Android2.1以上版本支持
					if (isMethodsCompat(android.os.Build.VERSION_CODES.ECLAIR_MR1)) {
						String imgName = FileUtils.getFileName(theLarge);
						bitmap = ImageUtils.loadImgThumbnail(Tweet.this,
								imgName,
								MediaStore.Images.Thumbnails.MICRO_KIND);
					}

					if (bitmap == null && !StringUtils.isEmpty(theLarge)) {
						bitmap = ImageUtils
								.loadImgThumbnail(theLarge, 100, 100);
					}
				}
				// 拍摄图片
				else if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA) {
					if (bitmap == null && !StringUtils.isEmpty(theLarge)) {
						bitmap = ImageUtils
								.loadImgThumbnail(theLarge, 100, 100);
					}
				}

				if (bitmap != null) {
					// 存放照片的文件夹
					String savePath = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/JustSharePro/Camera/";
					File savedir = new File(savePath);
					if (!savedir.exists()) {
						savedir.mkdirs();
					}

					String largeFileName = FileUtils.getFileName(theLarge);
					String largeFilePath = savePath + largeFileName;
					// 判断是否已存在缩略图
					if (largeFileName.startsWith("thumb_")
							&& new File(largeFilePath).exists()) {
						theThumbnail = largeFilePath;
						imgFile = new File(theThumbnail);
					} else {
						// 生成上传的800宽度图片
						String thumbFileName = "thumb_" + largeFileName;
						theThumbnail = savePath + thumbFileName;
						if (new File(theThumbnail).exists()) {
							imgFile = new File(theThumbnail);
						} else {
							try {
								// 压缩上传的图片，并保存到SD卡
								ImageUtils.createImageThumbnail(Tweet.this,
										theLarge, theThumbnail, 800, 80);
								imgFile = new File(theThumbnail);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					// 保存临时图片
					preferences = getSharedPreferences(PREFERENCE_TEMP_DATA, 0);
					SharedPreferences.Editor localEditor = preferences.edit();
					localEditor.putString(TEMP_IMAGE_KEY, theThumbnail);
					localEditor.commit();

					Message msg = new Message();
					msg.what = 1;
					msg.obj = bitmap;
					handler.sendMessage(msg);
				}
			};
		}.start();
	}

	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

}