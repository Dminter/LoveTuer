package com.zncm.lovetuer.global;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.zncm.lovetuer.data.base.NoteBookData;
import com.zncm.lovetuer.data.base.TokeData;
import com.zncm.utils.file.PathUtil;

import java.io.File;
import java.util.List;


//全局Application
public class SharedApplication extends Application {
    public Context ctx;
    private String storagePath = null;// 数据存储路径
    public static SharedApplication instance;
    private TokeData tokeData;
    private List<NoteBookData> noteBookDatas;

    @Override
    public void onCreate() {
        super.onCreate();
        this.ctx = this.getApplicationContext();
        instance = this;
        initImageLoader();
    }

    public void initImageLoader() {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx)
//                .threadPriority(Thread.NORM_PRIORITY - 2)
//                .denyCacheImageMultipleSizesInMemory()
//                .discCacheFileNameGenerator(new Md5FileNameGenerator())
//                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                .enableLogging() // Not necessary in common
//                .build();
        // Initialize ImageLoader with configuration.


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 1) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .discCache(new UnlimitedDiscCache(new File(PathUtil.getCachePath()))) // default
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileCount(100)
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(ctx)) // default
                .imageDecoder(new BaseImageDecoder()) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .build();
        ImageLoader.getInstance().init(config);
    }

    // 全局对象是单例的
    public static SharedApplication getInstance() {
        return instance;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public SharedPreferences getPreferences(String fileString) {
        return this.getPreferences(fileString, Context.MODE_PRIVATE);
    }

    public SharedPreferences getPreferences(String fileString, int mod) {
        return getSharedPreferences(fileString, mod);
    }

    public TokeData getTokeData() {
        return tokeData;
    }

    public void setTokeData(TokeData tokeData) {
        this.tokeData = tokeData;
    }

    public List<NoteBookData> getNoteBookDatas() {
        return noteBookDatas;
    }

    public void setNoteBookDatas(List<NoteBookData> noteBookDatas) {
        this.noteBookDatas = noteBookDatas;
    }


}
