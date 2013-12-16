package com.login.weibo.bean.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.login.weibo.bean.JSONContants;
import com.login.weibo.bean.Source;
import com.login.weibo.bean.Status;
import com.login.weibo.bean.User;
import com.weibo.exception.WeiboException;
/**
 * 
 * @author Administrator
 * 
 */
public class StatusUtil {
     public static Status getStatus(JSONObject json) throws JSONException{
    	 Status mStatus = new Status();
 		try {
 			if(!json.isNull("created_at")) mStatus.setCreatedAt(json.getString("created_at"));
 			if(!json.isNull("id")) mStatus.setId(json.getString("id"));
 			if(!json.isNull("mid")) mStatus.setMid(json.getString("mid"));
 			if(!json.isNull("idstr")) mStatus.setIdstr(json.getLong("idstr"));
 			if(!json.isNull("text")) mStatus.setText(json.getString("text"));
 			if (!json.isNull("source")) mStatus.setSource(new Source(json.getString("source")));
 			if(!json.isNull("thumbnail_pic")) mStatus.setThumbnailPic(json.getString("thumbnail_pic"));
 			if(!json.isNull("bmiddle_pic")) mStatus.setBmiddlePic(json.getString("bmiddle_pic")) ;
 			if(!json.isNull("original_pic")) mStatus.setOriginalPic(json.getString("original_pic"));
 			if(!json.isNull("reposts_count")) mStatus.setRepostsCount(json.getInt("reposts_count"));
 			if(!json.isNull("comments_count")) mStatus.setCommentsCount(json.getInt("comments_count"));
 			if (!json.isNull("user")) mStatus.setUser(UserUtil.getUser(json.getJSONObject("user")));
 			if (!json.isNull("retweeted_status")) mStatus.setRetweetedStatus(getStatus(json.getJSONObject("retweeted_status")));
 			if(!json.isNull("mlevel")) mStatus.setMlevel(json.getInt("mlevel"));
 		} catch (JSONException je) {
 			    Log.v("StatusUtil", "getStatus()");
				je.printStackTrace();
 		}
 		return mStatus;
     }
}
