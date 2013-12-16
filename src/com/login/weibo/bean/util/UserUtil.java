package com.login.weibo.bean.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.login.weibo.bean.JSONContants;
import com.login.weibo.bean.User;

/**
 * @author Administrator
 */
public class UserUtil {
    public static User getUser(JSONObject json) throws JSONException{
    	if(!json.isNull("error"))return null;
    	User user=new User();
    	user.setId(String.valueOf(json.getLong("id")));
    	user.setScreenName(json.getString("screen_name"));
    	user.setLocation(json.getString("location"));
    	user.setGender(json.getString("gender"));
    	user.setProfileImageUrl(json.getString("profile_image_url"));
    	user.setDescription(json.getString("description"));
    	user.setStatusesCount(json.getInt("statuses_count"));
    	user.setFollowersCount(json.getInt("followers_count"));
    	user.setFriendsCount(json.getInt("friends_count"));
    	return user;
    }
    public static User getUser(String object) throws JSONException{
    	JSONObject user=(JSONObject) new JSONTokener(object).nextValue();
        return getUser(user);
    }
}
