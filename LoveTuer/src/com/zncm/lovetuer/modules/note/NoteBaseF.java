package com.zncm.lovetuer.modules.note;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.alibaba.fastjson.JSON;
import com.zncm.component.request.ReqService;
import com.zncm.component.request.ServiceParams;
import com.zncm.component.request.ServiceRequester;
import com.zncm.lovetuer.R;
import com.zncm.lovetuer.data.EnumData;
import com.zncm.lovetuer.data.base.NoteData;
import com.zncm.lovetuer.data.base.TipData;
import com.zncm.lovetuer.global.GlobalConstants;
import com.zncm.lovetuer.modules.note.adapter.NoteAdapter;
import com.zncm.lovetuer.modules.note.zone.ZoneTabsPager;
import com.zncm.lovetuer.utils.XUtil;
import com.zncm.utils.L;
import com.zncm.utils.StrUtil;
import com.zncm.utils.TimeUtils;
import com.zncm.utils.exception.CheckedExceptionHandler;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NoteBaseF extends BaseFragment {
    protected Activity mActivity;
    protected List<NoteData> datas = null;
    protected NoteAdapter mAdapter;
    protected int curPosition;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add("bug").setIcon(R.drawable.bug).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("add").setIcon(R.drawable.add).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
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
        //3284
        return false;
    }

    private void eidtNote(NoteData noteData) {
        Intent newIntent = null;
        newIntent = new Intent(mActivity, NoteAddActivity.class);
        newIntent.putExtra(GlobalConstants.KEY_MODIFY, true);
        newIntent.putExtra(GlobalConstants.KEY_PARAM_DATA, noteData);
        startActivityForResult(newIntent, GlobalConstants.REQUESTCODE_ADD);
    }

    private void copyNote() {
        NoteData data = (NoteData) datas.get(curPosition);
        StrUtil.copyText(mActivity, data.getContent());
    }


    public void delNote(int _id) {

        try {
            final Message msg = new Message();
            ServiceParams params = new ServiceParams();
            params.put("id", String.valueOf(_id));
            ReqService.postDataToServer(GlobalConstants.BASE_API_URL + "diary/del", params,
                    new ServiceRequester() {

                        @Override
                        public void onResult(String resultEx) {

                            if (StrUtil.notEmptyOrNull(resultEx)) {
                                datas.remove(curPosition);
                                mAdapter.setItems(datas);
                                loadComplete();
                            }

                        }
                    }
            );
        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mActivity = (Activity) getActivity();


        datas = new ArrayList<NoteData>();
        mAdapter = new NoteAdapter(mActivity) {
            @Override
            public void setData(int position, NoteViewHolder holder) {
                final NoteData data = (NoteData) datas.get(position);
                if (data == null) {
                    return;
                }


                if (StrUtil.notEmptyOrNull(data.getUserid())) {
                    holder.ivIcon.setVisibility(View.VISIBLE);
                    String iconUrl = GlobalConstants.USER_AVATAR_URL + data.getUserid();
//                    holder.ivIcon.
                    imageLoader.displayImage(iconUrl, holder.ivIcon, options);
//                    imageLoader.displayImage(iconUrl, holder.ivIcon);
                } else {
                    holder.ivIcon.setVisibility(View.GONE);
                }
                holder.ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent newIntent = null;
                        newIntent = new Intent(mActivity, ZoneTabsPager.class);
                        newIntent.putExtra(GlobalConstants.KEY_ID, data.getUserid());
                        startActivityForResult(newIntent, GlobalConstants.REQUESTCODE_ADD);
                    }
                });

                //ZoneTabsPager
                if (StrUtil.notEmptyOrNull(data.getContent())) {
                    holder.tvContent.setVisibility(View.VISIBLE);
                    holder.tvContent.setText(Html.fromHtml(data.getContent()));
                } else {
                    holder.tvContent.setVisibility(View.GONE);
                }
                if (StrUtil.notEmptyOrNull(data.getCreated_user())) {
                    holder.tvAuthor.setVisibility(View.VISIBLE);
                    holder.tvAuthor.setText(data.getCreated_user());
                } else {
                    holder.tvAuthor.setVisibility(View.GONE);
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

                if (data.getPrivacy() == 1) {
                    sbTitle.append("[").append("仅自己可见").append("]").append("  ");
                }

                if (StrUtil.notEmptyOrNull(sbTitle.toString())) {
                    holder.tvTitle.setVisibility(View.VISIBLE);
                    holder.tvTitle.setText(sbTitle.toString());
                } else {
                    holder.tvTitle.setVisibility(View.GONE);
                }
                if (StrUtil.notEmptyOrNull(data.getCreated_at())) {
                    holder.tvTime.setVisibility(View.VISIBLE);
                    holder.tvTime.setText(TimeUtils.getTimeShow(data.getCreated_at(), true));
                } else {
                    holder.tvTime.setVisibility(View.GONE);
                }

                if (data.getCommentcount() > 0) {
                    holder.tvReply.setVisibility(View.VISIBLE);
                    holder.tvReply.setText(data.getCommentcount() + "回复");
                } else {
                    holder.tvReply.setVisibility(View.GONE);
                }
            }
        };
        mAdapter.setItems(datas);
        lvBase.setAdapter(mAdapter);
        lvBase.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                curPosition = position - lvBase.getHeaderViewsCount();
                NoteData tmp = datas.get(curPosition);
                if (tmp == null) {
                    return false;
                }

                opDialog(tmp);

                return false;
            }
        });
        plListView.setOnItemClickListener(new OnItemClickListener() {

            @SuppressWarnings({"rawtypes", "unchecked"})
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                curPosition = position - lvBase.getHeaderViewsCount();
                Intent intent = new Intent(mActivity, NoteDetails.class);
                intent.putExtra(GlobalConstants.KEY_PARAM_DATA, datas.get(curPosition));
                startActivityForResult(intent, GlobalConstants.REQUESTCODE_ADD);
            }
        });
        initListView();
        initData();
        return view;
    }


    Html.ImageGetter imageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            InputStream is = null;
            try {
                is = (InputStream) new URL(source).getContent();
                Drawable d = Drawable.createFromStream(is, "src");
                d.setBounds(0, 0, d.getIntrinsicWidth(),
                        d.getIntrinsicHeight());
                is.close();
                L.i("xxx:" + d);
                return d;
            } catch (Exception e) {
                return null;
            }
        }
    };


    private void opDialog(final NoteData noteData) {
        boolean isSelf = false;
        if (StrUtil.notEmptyOrNull(noteData.getIsSelf()) && noteData.getIsSelf().equals("true")) {
            isSelf = true;
        }

        DialogFragment newFragment = new XUtil.ContextMenuDialog(isSelf, isSelf, true) {


            @Override
            protected void doClick(int type) {
                if (type == EnumData.ContextMenuEnum.EDIT.getValue()) {
                    eidtNote(noteData);
                } else if (type == EnumData.ContextMenuEnum.DEL.getValue()) {
                    delNote(noteData.getId());
                } else if (type == EnumData.ContextMenuEnum.COPY.getValue()) {
                    copyNote();
                }
            }

        };
        newFragment.show(

                getSherlockActivity()

                        .

                                getSupportFragmentManager(),

                "dialog"
        );
    }

    public void initData() {

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void initListView() {

    }

    public void getData(final boolean bFirst) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.i("onActivityResult=main");
        super.onActivityResult(requestCode, resultCode, data);
    }


    public static void getTipsInfo() {
        try {
            ServiceParams params = new ServiceParams();
            ReqService.getDataFromServer(GlobalConstants.BASE_API_URL + "tips/all", params,
                    new ServiceRequester() {

                        @Override
                        public void onResult(String resultEx) {

                            if (StrUtil.notEmptyOrNull(resultEx)) {
                                List<TipData> list = JSON.parseArray(JSON.parseObject(resultEx).getString("data"), TipData.class);
                                if (StrUtil.listNotNull(list)) {
                                    L.toastShort("你有新消息,点击侧边栏查看~");
                                }
                            }
                        }

                    }
            );

        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }
    }


}
