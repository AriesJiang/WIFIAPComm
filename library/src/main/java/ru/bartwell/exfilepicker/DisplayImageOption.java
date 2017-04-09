package ru.bartwell.exfilepicker;


import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class DisplayImageOption {

	public static DisplayImageOptions getOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.efp__ic_file)
				.showImageForEmptyUri(R.drawable.efp__ic_file)
				.showImageOnFail(R.drawable.efp__ic_file).cacheInMemory()
				.cacheOnDisc()
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.displayer(new SimpleBitmapDisplayer())
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		return options;
	}

	public static DisplayImageOptions getDrawableOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.efp__ic_file)
				.showImageForEmptyUri(R.drawable.efp__ic_file)
				.showImageOnFail(R.drawable.efp__ic_file)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.displayer(new SimpleBitmapDisplayer())
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		return options;
	}

}
