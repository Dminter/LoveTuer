package com.zncm.lovetuer.modules.note;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zncm.component.pullrefresh.PullToRefreshListView;
import com.zncm.lovetuer.R;
import com.zncm.lovetuer.data.base.TokeData;
import com.zncm.lovetuer.global.SharedApplication;
import com.zncm.utils.StrUtil;
import com.zncm.utils.sp.StatedPerference;

public abstract class BaseFragment extends SherlockFragment {
    protected PullToRefreshListView plListView;
    protected ListView lvBase;
    protected View view;
    protected LayoutInflater mInflater;
    protected TokeData tokeData;
    protected ImageLoader imageLoader;
    protected DisplayImageOptions options;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        view = inflater.inflate(R.layout.view_list_base, null);
        plListView = (PullToRefreshListView) view.findViewById(R.id.lvBase);
        lvBase = plListView.getRefreshableView();
        tokeData = SharedApplication.getInstance().getTokeData();
        if (tokeData == null) {
            String toke = StatedPerference.getOauthToke();
            if (StrUtil.notEmptyOrNull(toke)) {
                tokeData = JSON.parseObject(StatedPerference.getOauthToke(), TokeData.class);
            }
        }
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.icon)
                .showImageOnFail(R.drawable.icon)
                .resetViewBeforeLoading()
                .cacheOnDisc()
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public void loadComplete() {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                plListView.onRefreshComplete();
                plListView.onLoadMoreComplete();
            }
        });
    }

}
