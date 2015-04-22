package com.zncm.lovetuer.modules.note;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.zncm.component.pullrefresh.PullToRefreshBase;
import com.zncm.component.pullrefresh.PullToRefreshListView;
import com.zncm.component.request.ReqService;
import com.zncm.component.request.ServiceParams;
import com.zncm.component.request.ServiceRequester;
import com.zncm.lovetuer.R;
import com.zncm.lovetuer.data.EnumData;
import com.zncm.lovetuer.data.base.NoteCommentData;
import com.zncm.lovetuer.data.base.NoteData;
import com.zncm.lovetuer.global.GlobalConstants;
import com.zncm.lovetuer.modules.base.BaseHomeActivity;
import com.zncm.lovetuer.modules.note.adapter.CommentAdapter;
import com.zncm.lovetuer.utils.XUtil;
import com.zncm.utils.L;
import com.zncm.utils.StrUtil;
import com.zncm.utils.TimeUtils;
import com.zncm.utils.exception.CheckedExceptionHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NoteDetails extends BaseHomeActivity {
    public TextView tvContent;
    public TextView tvTime;
    public TextView tvTitle;
    public TextView tvReply;
    public TextView tvMore;
    protected PullToRefreshListView plListView;
    protected ListView lvBase;
    private NoteData data;
    protected List<NoteCommentData> datas = null;
    protected CommentAdapter mAdapter;
    protected int curPosition;
    protected int pageIndex = 1;

    private void getNoteDetails(int _id) {
        try {
            final Message msg = new Message();
            ServiceParams params = new ServiceParams();
            ReqService.getDataFromServer(GlobalConstants.BASE_API_URL + "diary/info/" + _id, params,
                    new ServiceRequester() {
                        @Override
                        public void onResult(String resultEx) {
                            if (StrUtil.notEmptyOrNull(resultEx)) {
                                data = JSON.parseObject(resultEx, NoteData.class);
                                initNoteData();
                            }
                        }
                    }
            );
        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_note_detail);
        initViews();
        initData();
    }

    private void initViews() {
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvReply = (TextView) findViewById(R.id.Reply);
        tvMore = (TextView) findViewById(R.id.tvMore);
        plListView = (PullToRefreshListView) findViewById(R.id.lvBase);
        lvBase = plListView.getRefreshableView();
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

    public void initListView() {
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

    private void initData() {
        Serializable dataParam = getIntent().getSerializableExtra(GlobalConstants.KEY_PARAM_DATA);
        int _id = getIntent().getIntExtra(GlobalConstants.KEY_ID, 0);
        data = (NoteData) dataParam;
        datas = new ArrayList<NoteCommentData>();
        mAdapter = new CommentAdapter(this) {
            @Override
            public void setData(int position, NoteViewHolder holder) {
                final NoteCommentData data = (NoteCommentData) datas.get(position);
                if (data == null) {
                    return;
                }
                if (StrUtil.notEmptyOrNull(data.getContent())) {
                    holder.tvContent.setVisibility(View.VISIBLE);
                    holder.tvContent.setText(Html.fromHtml(data.getContent()));
                } else {
                    holder.tvContent.setVisibility(View.GONE);
                }
                if (StrUtil.notEmptyOrNull(data.getNick())) {
                    holder.tvAuthor.setVisibility(View.VISIBLE);
                    holder.tvAuthor.setText(data.getNick());
                } else {
                    holder.tvAuthor.setVisibility(View.GONE);
                }
                if (StrUtil.notEmptyOrNull(data.getCreated_at())) {
                    holder.tvTime.setVisibility(View.VISIBLE);
                    holder.tvTime.setText(TimeUtils.getTimeShow(data.getCreated_at(), true));
                } else {
                    holder.tvTime.setVisibility(View.GONE);
                }
            }
        };
        mAdapter.setItems(datas);
        lvBase.setAdapter(mAdapter);
        lvBase.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                curPosition = position - lvBase.getHeaderViewsCount();
                return false;
            }
        });
        plListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @SuppressWarnings({"rawtypes", "unchecked"})
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                curPosition = position - lvBase.getHeaderViewsCount();
                NoteCommentData tmp = datas.get(curPosition);
                if (tmp == null) {
                    return;
                }
                Intent intent = new Intent(NoteDetails.this, NoteComment.class);
                intent.putExtra(GlobalConstants.KEY_PARAM_DATA, tmp);
                startActivityForResult(intent, GlobalConstants.REQUESTCODE_ADD);
            }
        });

        lvBase.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                curPosition = position - lvBase.getHeaderViewsCount();
                NoteCommentData tmp = datas.get(curPosition);
                if (tmp == null) {
                    return false;
                }
                opDialog(tmp);
                return false;
            }
        });
        initListView();
        if (data == null) {
            getNoteDetails(_id);
        } else {
            initNoteData();
        }
    }

    private void initNoteData() {
        tvMore.setText("[查看全文]");
        if (StrUtil.notEmptyOrNull(data.getContent())) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(Html.fromHtml(data.getContent()));
        } else {
            tvContent.setVisibility(View.GONE);
        }
        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteDetails.this, NoteContent.class);
                intent.putExtra(GlobalConstants.KEY_DATA, data.getContent());
                startActivity(intent);
            }
        });
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteDetails.this, NoteContent.class);
                intent.putExtra(GlobalConstants.KEY_DATA, data.getContent());
                startActivity(intent);
            }
        });
        if (StrUtil.notEmptyOrNull(data.getCreated_user())) {
            actionBar.setTitle(data.getCreated_user() + " 的日记");
        } else {
            actionBar.setTitle("日记详情");
        }
        StringBuffer sbTitle = new StringBuffer();
        if (StrUtil.notEmptyOrNull(data.getBookname())) {
            sbTitle.append("[").append(data.getBookname()).append("]").append("  ");
        }
        if (StrUtil.notEmptyOrNull(data.getMood())) {
            sbTitle.append("[心情:").append(data.getMood()).append("]").append("  ");
        }
        if (StrUtil.notEmptyOrNull(data.getWeather())) {
            sbTitle.append("[天气:").append(data.getWeather()).append("]").append("  ");
        }
        if (StrUtil.notEmptyOrNull(data.getLocation())) {
            sbTitle.append("[地点:").append(data.getLocation()).append("]").append("  ");
        }
        if (StrUtil.notEmptyOrNull(sbTitle.toString())) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(sbTitle.toString());
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        if (StrUtil.notEmptyOrNull(data.getCreated_at())) {
            tvTime.setVisibility(View.VISIBLE);
            tvTime.setText(TimeUtils.getTimeShow(data.getCreated_at(), true));
        } else {
            tvTime.setVisibility(View.GONE);
        }
        if (data.getCommentcount() > 0) {
            tvReply.setVisibility(View.VISIBLE);
            tvReply.setText(data.getCommentcount() + "回复");
        } else {
            tvReply.setVisibility(View.GONE);
        }
        getData(true);
    }


    public void getData(final boolean bFirst) {
        try {
            final Message msg = new Message();
            ServiceParams params = new ServiceParams();
            params.put("page", String.valueOf(pageIndex));
            params.put("count", GlobalConstants.PAGE_COUNT);
            ReqService.getDataFromServer(GlobalConstants.BASE_API_URL + "comment/info/" + data.getId(), params,
                    new ServiceRequester() {

                        @Override
                        public void onResult(String resultEx) {

                            if (StrUtil.notEmptyOrNull(resultEx)) {
                                List<NoteCommentData> list = JSON.parseArray(JSON.parseObject(resultEx).getString("data"), NoteCommentData.class);
                                if (bFirst) {
                                    datas = new ArrayList<NoteCommentData>();
                                }
                                if (StrUtil.listNotNull(list)) {
                                    pageIndex++;
                                    datas.addAll(list);
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

    private void opDialog(final NoteCommentData commentData) {
        //noteData.isSelf()
        boolean isSelf = false;
        if (StrUtil.notEmptyOrNull(commentData.getIsSelf()) && commentData.getIsSelf().equals("true")) {
            isSelf = true;
        }
        DialogFragment newFragment = new XUtil.ContextMenuDialog(false, isSelf, true) {

            @Override
            protected void doClick(int type) {
                if (type == EnumData.ContextMenuEnum.DEL.getValue()) {
                    delReplyDo(commentData);
                } else if (type == EnumData.ContextMenuEnum.COPY.getValue()) {
                    copyComment(commentData);
                }
            }

        };
        newFragment.show(

                getSupportFragmentManager(),

                "dialog"
        );
    }

    private void copyComment(NoteCommentData commentData) {
        StrUtil.copyText(this, commentData.getContent());
    }

    public void delReplyDo(NoteCommentData commentData) {
        try {
            final Message msg = new Message();

            ServiceParams params = new ServiceParams();
            params.put("diaryid", String.valueOf(commentData.getRelated_id()));
            ReqService.postDataToServer(GlobalConstants.BASE_API_URL + "comment/del/" + commentData.getComment_id(), params,
                    new ServiceRequester() {

                        @Override
                        public void onResult(String resultEx) {

                            if (StrUtil.notEmptyOrNull(resultEx)) {
                                L.toastShort("评论删除成功");
                                pageIndex = 1;
                                getData(true);
                            }

                        }

                    }
            );

        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add("comment");
        item.setIcon(R.drawable.comment);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getTitle().equals("comment")) {
//            replyDialog(String.valueOf(data.getId()), "", "");
            Intent intent = new Intent(NoteDetails.this, NoteComment.class);
            intent.putExtra(GlobalConstants.KEY_ID, data.getId());
            startActivityForResult(intent, GlobalConstants.REQUESTCODE_ADD);
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pageIndex = 1;
        getData(true);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


}