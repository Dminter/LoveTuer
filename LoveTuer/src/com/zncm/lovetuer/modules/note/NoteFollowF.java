package com.zncm.lovetuer.modules.note;

import android.os.Message;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.zncm.component.pullrefresh.PullToRefreshBase;
import com.zncm.component.request.ReqService;
import com.zncm.component.request.ServiceParams;
import com.zncm.component.request.ServiceRequester;
import com.zncm.lovetuer.data.base.NoteData;
import com.zncm.lovetuer.global.GlobalConstants;
import com.zncm.utils.StrUtil;
import com.zncm.utils.exception.CheckedExceptionHandler;

import java.util.ArrayList;
import java.util.List;

public class NoteFollowF extends NoteBaseF {

    protected int pageIndex = 1;

    public void initData() {
        getData(true);
    }


    @Override
    public void initListView() {
        plListView.needEmptyView("暂无数据");
        plListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                pageIndex = 1;
                getData(true);
            }
        });
        plListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                getData(false);

            }
        });
    }

    @Override
    public void getData(final boolean bFirst) {
        try {
            ServiceParams params = new ServiceParams();
            params.put("page", String.valueOf(pageIndex));
            params.put("count", GlobalConstants.PAGE_COUNT);
            ReqService.getDataFromServer(GlobalConstants.BASE_API_URL + "diaries/follow/" + tokeData.getTuer_uid(), params,
                    new ServiceRequester() {

                        @Override
                        public void onResult(String resultEx) {

                            if (StrUtil.notEmptyOrNull(resultEx)) {
                                List<NoteData> list = JSON.parseArray(JSON.parseObject(resultEx).getString("data"), NoteData.class);
                                if (bFirst) {
                                    datas = new ArrayList<NoteData>();
                                }
                                if (StrUtil.listNotNull(list)) {
                                    pageIndex++;
                                    for (NoteData data : list) {
                                        if (data.getPrivacy() == 0) {
                                            datas.add(data);
                                        }
                                    }
                                }
                            }

                            mAdapter.setItems(datas);
                            loadComplete();
                        }


                    }
            );


        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }
    }


}
