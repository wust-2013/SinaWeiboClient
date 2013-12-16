package com.login.weibo.bean.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import com.login.weibo.bean.JSONContants;
import com.login.weibo.bean.Status;
import com.login.weibo.bean.Statuses;


public class StatusesUtil {
   public static Statuses getStatuses(JSONObject object) throws JSONException{
	   Statuses slist=new Statuses();
	   JSONArray jarray=object.getJSONArray(JSONContants.Statuses.statuses);
	   for(int i=0;i<jarray.length();i++){
		   JSONObject jo=jarray.getJSONObject(i);
		   slist.addStatus(StatusUtil.getStatus(jo));
	   }
	   return slist;
   }
   public static Statuses getStatuses(String object) throws JSONException{
	   JSONObject json = (JSONObject) new JSONTokener(object).nextValue();
       return getStatuses(json);
   }
   static class Test{
	   public static void main(String[] args) throws JSONException {
	        String path="res/statuses.txt";  
			String st;
			try {
				st = getString(path);
				JSONObject object = (JSONObject) new JSONTokener(st).nextValue();
				Statuses slist=StatusesUtil.getStatuses(object);
				List<Status> li=slist.getStatuses();
				for(int i=0;i<li.size();i++){
					System.out.println("------------------");
					 System.out.println(li.get(i));
					System.out.println("------------------");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    static String getString(String path) throws IOException{
	    	FileReader fr=new FileReader(new File(path));
	    	StringBuffer sb=new StringBuffer();
	    	char[] ch=new char[1024];
	    	int offset=0;
	    	int t;
	    	while((t=fr.read(ch, 0, ch.length))!=-1){
	    		sb.append(ch);
	    	}
	    	return sb.toString();
	    }
   }
}
