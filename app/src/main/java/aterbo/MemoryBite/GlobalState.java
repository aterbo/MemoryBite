package aterbo.MemoryBite;

import android.app.Application;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by ATerbo on 9/24/15.
 * This sets permanent settings for UIL
 * http://darrysea.tistory.com/m/post/64
 */
public class GlobalState extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //gets 8dp in pixels so that it can be fed to UIL for rounding corners of images
        //REMOVED ROUNDED BITMAPS DUE TO DISTORTED IMAGES
        // .displayer(new RoundedBitmapDisplayer(px))
        /*
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());
        */

        //Implimentation of Universal Image Loader
        // Create global configuration and initialize ImageLoader with this config
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.mbicon)
                .showImageForEmptyUri(R.drawable.mbicon)
                .showImageOnFail(R.drawable.mbicon)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .delayBeforeLoading(50)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .considerExifParams(true)
                .build();

        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .denyCacheImageMultipleSizesInMemory()
                .build();


        ImageLoader.getInstance().init(config);
    }
}
