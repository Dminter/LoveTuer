package com.zncm.lovetuer.modules.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.alibaba.fastjson.JSON;
import com.zncm.component.pullrefresh.PullToRefreshBase;
import com.zncm.component.request.ReqService;
import com.zncm.component.request.ServiceParams;
import com.zncm.component.request.ServiceRequester;
import com.zncm.lovetuer.R;
import com.zncm.lovetuer.data.base.FollowData;
import com.zncm.lovetuer.data.base.NoteData;
import com.zncm.lovetuer.global.GlobalConstants;
import com.zncm.lovetuer.utils.XUtil;
import com.zncm.utils.L;
import com.zncm.utils.StrUtil;
import com.zncm.utils.exception.CheckedExceptionHandler;

import java.util.ArrayList;
import java.util.List;

public class NoteIndexF extends NoteBaseF {

    protected int pageIndex = 1;
    private String uid;
    private boolean isSelf = true;


    public void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            uid = bundle.getString(GlobalConstants.KEY_ID);
        }
        if (StrUtil.isEmptyOrNull(uid)) {
            isSelf = true;
            uid = String.valueOf(tokeData.getTuer_uid());
        } else {
            isSelf = false;
        }
        getData(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isSelf) {
            menu.add("bug").setIcon(R.drawable.bug).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.add("add").setIcon(R.drawable.add).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } else {
            menu.add("关注此人").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (isSelf) {
            if (item.getTitle().equals("add")) {
                Intent newIntent = null;
                newIntent = new Intent(mActivity, NoteAddActivity.class);
                newIntent.putExtra(GlobalConstants.KEY_MODIFY, false);
                startActivityForResult(newIntent, GlobalConstants.REQUESTCODE_ADD);
            } else if (item.getTitle().equals("bug")) {
                Intent intent = new Intent(mActivity, NoteDetails.class);
                intent.putExtra(GlobalConstants.KEY_ID, GlobalConstants.BUG_ID);
                startActivity(intent);
            }
        } else {
            if (item.getTitle().equals("关注此人")) {
                attentionUser(true);
            }
        }
        return false;
    }


    public void attentionUser(boolean flag) {

        try {
            ServiceParams params = new ServiceParams();
            if (flag) {
                params.put("addid", uid);
            } else {
                params.put("removeid", uid);
            }
            ReqService.postDataToServer(GlobalConstants.BASE_API_URL + "user/attention/" + tokeData.getTuer_uid(), params,
                    new ServiceRequester() {

                        @Override
                        public void onResult(String resultEx) {

                            if (StrUtil.notEmptyOrNull(resultEx)) {
                                FollowData data = JSON.parseObject(resultEx, FollowData.class);
                                if (data != null && data.getStatus().equals("followed")) {
                                    DialogFragment newFragment = new XUtil.TwoAlertDialogFragment("关注此人?", "关注", "取消关注") {

                                        @Override
                                        public void doPositiveClick() {
                                            L.toastShort("关注成功~");
                                        }

                                        @Override
                                        public void doNegativeClick() {
                                            attentionUser(false);
                                        }
                                    };
                                    newFragment.show(getSherlockActivity().getSupportFragmentManager(), "dialog");
                                } else if (data != null && data.getStatus().equals("unfollowed")) {
                                    L.toastShort("取消关注成功~");
                                }
                            }

                        }
                    }
            );
        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }
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
            ReqService.getDataFromServer(GlobalConstants.BASE_API_URL + "diaries/user/" + uid, params,
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
                                    if (!isSelf) {
                                        for (NoteData data : list) {
                                            if (data.getPrivacy() == 0) {
                                                datas.add(data);
                                            }
                                        }
                                    } else {
                                        datas.addAll(list);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            pageIndex = 1;
            getData(true);
        }

    }
}
