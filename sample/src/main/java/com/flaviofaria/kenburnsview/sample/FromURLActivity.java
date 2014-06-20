package com.flaviofaria.kenburnsview.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;

/**
 * Created by piracyde25 on 2014-20-06.
 */
public class FromURLActivity extends KenBurnsActivity {

    private KenBurnsView mImg;
    private Context context;

    private ImageLoaderConfiguration config;
    private File cacheDir;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.from_url);

        context = getApplicationContext();

        mImg = (KenBurnsView) findViewById(R.id.img);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String cacheDirName = "." + context.getResources().getString(R.string.app_name);
            cacheDir = new File(Environment.getExternalStorageDirectory(), cacheDirName);
        } else {
            cacheDir = context.getCacheDir();
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        config = new ImageLoaderConfiguration.Builder(context)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .threadPoolSize(5)
                .build();

        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();

        imageLoader.init(config);

        imageLoader.displayImage("http://i.imgur.com/gysR4Ee.jpg", mImg, options);
    }


    @Override
    protected void onPlayClick() {
        mImg.resume();
    }


    @Override
    protected void onPauseClick() {
        mImg.pause();
    }
}
