package com.login.weibo.bean;

import java.util.ArrayList;
import java.util.List;

public class Statuses {
   public ArrayList<Status> list=new ArrayList<Status>();
   public void addStatus(Status st){
	   list.add(st);
   }
   public List<Status> getStatuses(){
	   return list;
   }
   static class test{
	   public final int a=1;
	   public static void main(String[] args){
		   
		   System.out.println(new Statuses().list.size());
	   }
   }
}
