package com.niqiu.lib.crash;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

public class DeviceInfo {
	
	final static String TAG = "DeviceInfo";
	
	public static DetailInfo getDeviceInfo(Context mContext) {
		DetailInfo mDetailInfo = new DetailInfo();
		ArrayList<String> mFieldNames = new ArrayList<String>();
		Field[] fieldsCrash = mDetailInfo.getClass().getDeclaredFields();

		// 先通过反射，将需要提交的字段从自己定义的类中拿出来
		for (Field field : fieldsCrash) {
			try {
				field.setAccessible(true);
				// Log.d(TAG, field.getName() + " : " + field.get(mDetailInfo));
				mFieldNames.add(field.getName());
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info");
				Log.e(TAG, e.toString());
			}
		}
		
//		Log.e(TAG, "Product Model: " + android.os.Build.MODEL + ",\n" 
//                + android.os.Build.VERSION.SDK_INT + ",\n" 
//                + android.os.Build.VERSION.RELEASE); 
		
		Log.e(TAG, "---------------------------------");

		// 为成员相同的字段赋值
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				// infos.put(field.getName(), field.get(null).toString());
//				Log.e(TAG, field.getName() + " : " + field.get(null));

				for (int i = 0; i < mFieldNames.size(); i++) {
//					Log.e(TAG, "---------"+mFieldNames.get(i) + "-------");
					if (TextUtils.equals(mFieldNames.get(i), field.getName())) {
						Method method = mDetailInfo.getClass()
								.getDeclaredMethod("set" + field.getName(),
										new Class[] { String.class });
						method.invoke(mDetailInfo, (String) field.get(null));
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect DEVICE info");
				Log.e(TAG, e.getStackTrace().toString());
				Log.e(TAG, e.toString());
			}
		}
		
		mDetailInfo.setPRODUCTMODEL(Build.MODEL);
		mDetailInfo.setSDK_INT(Integer.toString(Build.VERSION.SDK_INT));
		mDetailInfo.setPRODUCTMODEL(Build.VERSION.RELEASE);
		
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);

		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
//		Log.e(TAG, "width----"+width);
		@SuppressWarnings("deprecation")
		int height = wm.getDefaultDisplay().getHeight();
//		Log.e(TAG, "height----"+height);
		
		mDetailInfo.setDISPLAYMETRICS(height+"x"+width);
		return mDetailInfo;
	}

}
