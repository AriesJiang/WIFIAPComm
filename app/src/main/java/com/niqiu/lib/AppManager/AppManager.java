package com.niqiu.lib.AppManager;

import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * @author LRQ
 * @version 1.0
 */
public class AppManager {
	
	private static Stack<Activity> activityStack;
	private static Stack<Class<?>> classStack;
	private static AppManager instance;
	private static String TAG = "AppManager";
	
	private AppManager(){}
	/**
	 * 单一实例
	 */
	public static AppManager getAppManager(){
		if(instance==null){
			instance=new AppManager();
		}
		return instance;
	}
	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity){
		if(activityStack==null){
			activityStack=new Stack<Activity>();
		}
		if(classStack==null){
			classStack=new Stack<Class<?>>();
		}
//		if (activityStack.size()==0
//				&&!TextUtils.equals(activity.getClass().getName(),
//						Start.class.getName())) {
////			AppExit(App.getCurrent());
//		}else{
//			classStack.add(activity.getClass());
//			activityStack.add(activity);
//			showAllActivity();
//		}
		classStack.add(activity.getClass());
		activityStack.add(activity);
		showAllActivity();
	}
	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity(){
		Activity activity=activityStack.lastElement();
		return activity;
	}
	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity(){
		if(activityStack!=null){
			Activity activity=activityStack.lastElement();
			finishActivity(activity);
		}
		
	}
	
	/**
	 * 展示栈内现有的activity
	 */
	public void showAllActivity() {
		for (int i = 0; i < classStack.size(); i++) {
			Log.d(TAG, "classStack_show---------"+classStack.get(i).getName());
		}
		for (int i = 0; i < activityStack.size(); i++) {
			Log.d(TAG, "activityStack_show---------" + activityStack.get(i).getClass().getName());
		}
 	}
	/**
	 * 出栈到指定的Activity
	 */
	public boolean popActivity(Class<?> cls){
		if(cls!=null){
			Log.e(TAG, "popActivity---------"+cls.getName());
			Log.e(TAG, "popActivity---------"+cls);
			if (classStack != null && activityStack != null) {
				for (int i = 0; i < classStack.size(); i++) {
					Log.e(TAG, "classStack-111--------"+classStack.get(i).getName());
					Log.e(TAG, "classStack-222--------"+classStack.get(i));
				}
				for (int i = 0; i < activityStack.size(); i++) {
					Log.e(TAG, "activityStack---------"+activityStack.get(i).getClass().getName());
				}
				if(classStack.contains(cls)){
					Log.e(TAG, "classStack.contains(cls)-------true--");
					while(activityStack.size()>0){
						Log.e(TAG, "activityStack.size()>0");
						Activity myActivityStack = activityStack.peek();
						Log.e(TAG, "activityStack---222222------"+myActivityStack.getClass().getName());
						if(!myActivityStack.getClass().getName().equals(cls.getName())){
							// 方案
							finishActivity(myActivityStack);
							
							// 方案2
//							activityStack.remove(myActivityStack);
//							classStack.remove(myActivityStack.getClass());
//							myActivityStack.finish();
//							myActivityStack=null;
						}else {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	
	
	
	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity){
		if(activity!=null){
			Log.e(TAG, "finishActivity---------"+activity.getClass().getName());
			classStack.remove(activity.getClass());
			activityStack.remove(activity);
			activity.finish();
			activity=null;
		}
	}
	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls){
		if(activityStack!=null){
			Log.e(TAG, "finishActivity---------"+cls.getName());
			for (Activity activity : activityStack) {
				if(activity.getClass().equals(cls) ){
					finishActivity(activity);
					classStack.remove(cls);
				}
			}
		}
		
	}
	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity(){
		if(activityStack!=null){
			for (int i = 0, size = activityStack.size(); i < size; i++){
				Log.e(TAG, "finishAllActivity---------"+activityStack.get(i).getClass().getName());
	            if (null != activityStack.get(i)){
	            	activityStack.get(i).finish();
	            	classStack.remove(activityStack.get(i).getClass());
	            }
		    }
			activityStack.clear();
			classStack.clear();
		}
	}
	
	/**
	 * 退出应用程序
	 */
	public void AppExit(Context context) {
		try {
//			MobclickAgent.onKillProcess(context);
			finishAllActivity();
			System.exit(0);
		} catch (Exception e) {	
			Log.e(TAG, e.toString());
		}
	}
	
	public void AppReStart(Class<?> cls) {
		popActivity(cls);
//		activityStack.peek().st
	}
	
	public boolean isNull(){
		return activityStack == null ? true : false;
		
	}
	
}