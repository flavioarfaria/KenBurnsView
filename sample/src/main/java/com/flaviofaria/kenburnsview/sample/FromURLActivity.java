package com.flaviofaria.kenburnsview.sample;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;


public class FromURLActivity extends KenBurnsActivity implements ImageLoadingListener {

    private KenBurnsView mImg;
    private ProgressBar mProgressBar;

    private ImageLoaderConfiguration config;
    private File cacheDir;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.from_url);

        mImg = (KenBurnsView) findViewById(R.id.img);
        mProgressBar = (ProgressBar) findViewById(android.R.id.progress);

        loadImage();
    }


    private void loadImage() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String cacheDirName = "." + getString(R.string.app_name);
            cacheDir = new File(Environment.getExternalStorageDirectory(), cacheDirName);
        } else {
            cacheDir = getCacheDir();
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        config = new ImageLoaderConfiguration.Builder(this)
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

        imageLoader.displayImage("http://i.imgur.com/gysR4Ee.jpg", mImg, options, this);
    }


    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }


    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        Toast.makeText(getApplicationContext(), "Failed to load image.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        mProgressBar.setVisibility(View.GONE);
        mImg.setVisibility(View.VISIBLE);
    }


    @Override
    public void onLoadingCancelled(String imageUri, View view) {

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
