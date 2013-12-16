package com.login.util;

import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * 
 */
public class UIHelper {

	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

	public final static int LISTVIEW_DATA_MORE = 0x05;
	public final static int LISTVIEW_DATA_LOADING = 0x06;
	public final static int LISTVIEW_DATA_FULL = 0x07;
	public final static int LISTVIEW_DATA_EMPTY = 0x08;

	public final static int LISTVIEW_DATATYPE_FRIENDS_TIMElINE = 0x09;
	public final static int LISTVIEW_DATATYPE_PUBLIC_TIMElINE = 0x0a;
	public final static int LISTVIEW_DATATYPE_USER_TIMElINE = 0x0b;
	public final static int LISTVIEW_DATATYPE_TWEET = 0x0c;
	public final static int LISTVIEW_DATATYPE_ACTIVE = 0x0d;
	public final static int LISTVIEW_DATATYPE_MESSAGE = 0x0e;
	public final static int LISTVIEW_DATATYPE_COMMENT = 0x0f;

	public final static int REQUEST_CODE_FOR_RESULT = 0x10;
	public final static int REQUEST_CODE_FOR_REPLY = 0x11;

	/** 表情图片匹配 */
	private static Pattern facePattern = Pattern
			.compile("\\[{1}([0-9]\\d*)\\]{1}");

	/** 全局web样式 */
	public final static String WEB_STYLE = "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, int msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, int time) {
		Toast.makeText(cont, msg, time).show();
	}

	/**
	 * 显示图片对话框，显示小图片
	 * 
	 * @param context
	 * @param imgUrl
	 *            小图片地址
	 * @param medium_imgUrl
	 *            大图片地址
	 */
//	public static void showImageDialog(Context context, String imgUrl,
//			String medium_imgUrl) {
//		Intent intent = new Intent(context, ImageDialog.class);
//		intent.putExtra("img_url", imgUrl);
//		intent.putExtra("medium_imgUrl", medium_imgUrl);
//		context.startActivity(intent);
//	}

	/**
	 * 显示图片对话框，显示大图片
	 * 
	 * @param context
	 * @param sina_img
	 *            如果操作的是新浪微博，该值为新浪微博大图片路径，此时腾讯微博大图片地址为空
	 * @param tt_img
	 *            如果操作的是Tencent微博，该值为新浪微博大图片路径，此时Sina微博大图片地址为空
	 */
//	public static void showImageZoomDialog(Context context, String sina_img,
//			String tt_img) {
//		Intent intent = new Intent(context, ImageZoomDialog.class);
//		if (sina_img != null) {
//			intent.putExtra("img_url", sina_img);
//		}
//		context.startActivity(intent);
//	}
}
