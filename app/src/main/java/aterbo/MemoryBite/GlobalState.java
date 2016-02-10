package aterbo.MemoryBite;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache;
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

        //Implimentation of Universal Image Loader
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

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .diskCacheSize(10 * 1024 * 1024)
                .build();

        ImageLoader.getInstance().init(config);
    }
}
