package com.zncm.lovetuer.modules.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.zncm.component.pullrefresh.PullToRefreshBase;
import com.zncm.component.pullrefresh.PullToRefreshListView;
import com.zncm.component.request.ReqService;
import com.zncm.component.request.ServiceParams;
import com.zncm.component.request.ServiceRequester;
import com.zncm.lovetuer.R;
import com.zncm.lovetuer.data.EnumData;
import com.zncm.lovetuer.data.base.TipData;
import com.zncm.lovetuer.global.GlobalConstants;
import com.zncm.lovetuer.modules.base.BaseHomeActivity;
import com.zncm.lovetuer.modules.note.adapter.TipsAdapter;
import com.zncm.lovetuer.utils.XUtil;
import com.zncm.utils.L;
import com.zncm.utils.StrUtil;
import com.zncm.utils.exception.CheckedExceptionHandler;

import java.util.ArrayList;
import java.util.List;

public class TipsAc extends BaseHomeActivity {
    protected PullToRefreshListView plListView;
    protected ListView lvBase;
    protected Activity ctx;
    protected List<TipData> datas = null;
    protected TipsAdapter mAdapter;
    protected int curPosition;
    protected int pageIndex = 1;


    public void initListView() {
        plListView.needEmptyView("暂无提醒");
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

    public void getData(final boolean bFirst) {
        try {
            ServiceParams params = new ServiceParams();
            ReqService.getDataFromServer(GlobalConstants.BASE_API_URL + "tips/all", params,
                    new ServiceRequester() {

                        @Override
                        public void onResult(String resultEx) {

                            if (StrUtil.notEmptyOrNull(resultEx)) {
                                List<TipData> list = JSON.parseArray(JSON.parseObject(resultEx).getString("data"), TipData.class);
                                if (bFirst) {
                                    datas = new ArrayList<TipData>();
                                }
                                if (StrUtil.listNotNull(list)) {
                                    pageIndex++;
                                    for (TipData data : list) {
                                        datas.add(data);
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

    private void copyTip(String content) {
        StrUtil.copyText(ctx, content);
    }

    private void opDialog(final TipData tipData) {
        DialogFragment newFragment = new XUtil.ContextMenuDialog(false, true, true) {


            @Override
            protected void doClick(int type) {
                if (type == EnumData.ContextMenuEnum.DEL.getValue()) {
                    delTip(tipData.getTip_id());
                } else if (type == EnumData.ContextMenuEnum.COPY.getValue()) {
                    copyTip(tipData.getContent());
                }
            }

        };
        newFragment.show(
                getSupportFragmentManager(),

                "dialog"
        );
    }

    public void initData() {
        getData(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getData(true);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        ctx = this;
        actionBar.setTitle("消息提醒");
        setContentView(R.layout.view_list_base);
        plListView = (PullToRefreshListView) findViewById(R.id.lvBase);
        lvBase = plListView.getRefreshableView();
        datas = new ArrayList<TipData>();
        mAdapter = new TipsAdapter(ctx) {
            @Override
            public void setData(int position, NoteViewHolder holder) {
                final TipData data = (TipData) datas.get(position);
                if (StrUtil.notEmptyOrNull(data.getContent())) {
                    holder.tvContent.setVisibility(View.VISIBLE);
                    holder.tvContent.setText(data.getContent());
                } else {
                    holder.tvContent.setVisibility(View.GONE);
                }
                if (StrUtil.notEmptyOrNull(data.getType()) && data.getType().equals("reply")) {
                    //user关注 reply 回复
                    holder.tvContent.setTextColor(getResources().getColor(R.color.author_color));
                } else {
                    holder.tvContent.setTextColor(getResources().getColor(R.color.black));
                }
            }
        };

        mAdapter.setItems(datas);
        lvBase.setAdapter(mAdapter);
        lvBase.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()

                                          {

                                              @Override
                                              public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                                                             long id) {
                                                  curPosition = position - lvBase.getHeaderViewsCount();
                                                  TipData tmp = datas.get(curPosition);
                                                  if (tmp == null) {
                                                      return false;
                                                  }

                                                  opDialog(tmp);

                                                  return false;
                                              }
                                          }
        );
        plListView.setOnItemClickListener(new AdapterView.OnItemClickListener()

                                          {

                                              @SuppressWarnings({"rawtypes", "unchecked"})
                                              @Override
                                              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                  curPosition = position - lvBase.getHeaderViewsCount();
                                                  if (StrUtil.notEmptyOrNull(datas.get(curPosition).getType()) && datas.get(curPosition).getType().equals("reply")) {

                                                      Intent intent = new Intent(ctx, NoteDetails.class);
                                                      intent.putExtra(GlobalConstants.KEY_ID, datas.get(curPosition).getId());
                                                      startActivity(intent);
                                                  }

                                              }
                                          }
        );

        initListView();

        initData();

    }


    public void delTip(String _id) {

        try {
            ServiceParams params = new ServiceParams();
            ReqService.postDataToServer(GlobalConstants.BASE_API_URL + "tips/del/" + _id, params,
                    new ServiceRequester() {

                        @Override
                        public void onResult(String resultEx) {

                            if (StrUtil.notEmptyOrNull(resultEx)) {
                                L.i("删除消息成功~");
                                try {
                                    datas.remove(curPosition);
                                    mAdapter.setItems(datas);
                                    loadComplete();
                                } catch (Exception e) {
                                    CheckedExceptionHandler.handleException(e);
                                }
                            }
                        }
                    }
            );
        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }
        getData(true);
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
