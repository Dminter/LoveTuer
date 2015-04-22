package com.zncm.lovetuer.modules.note;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;
import com.zncm.component.request.ReqService;
import com.zncm.component.request.ServiceParams;
import com.zncm.component.request.ServiceRequester;
import com.zncm.lovetuer.R;
import com.zncm.lovetuer.data.base.NoteCommentData;
import com.zncm.lovetuer.global.GlobalConstants;
import com.zncm.lovetuer.modules.base.BaseHomeActivity;
import com.zncm.utils.L;
import com.zncm.utils.StrUtil;
import com.zncm.utils.exception.CheckedExceptionHandler;

import java.io.Serializable;

public class NoteComment extends BaseHomeActivity {

    private EditText etComment;
    private NoteCommentData data;
    private int _id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_note_comment);
        actionBar.setTitle("评论");
        Serializable dataParam = getIntent().getSerializableExtra(GlobalConstants.KEY_PARAM_DATA);
        data = (NoteCommentData) dataParam;
        _id = getIntent().getIntExtra(GlobalConstants.KEY_ID, 0);
        etComment = (EditText) findViewById(R.id.etComment);
        //data
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
            if (data != null) {
                replyDo(data.getRelated_id(), data.getUserid(), data.getNick());
            } else {
                replyDo(String.valueOf(_id), "", "");
            }
        }
        return false;
    }

    public void replyDo(String related_id, String _id, String nick) {
        try {
            String content = etComment.getText().toString().trim();
            if (StrUtil.isEmptyOrNull(content)) {
                L.toastShort("请输入评论内容~");
                return;
            }
            ServiceParams params = new ServiceParams();
            params.put("content", content);
            if (StrUtil.notEmptyOrNull(_id)) {
                params.put("replyid", _id);
                params.put("replyname", nick);
            }
            ReqService.postDataToServer(GlobalConstants.BASE_API_URL + "comment/save/" + related_id, params,
                    new ServiceRequester() {

                        @Override
                        public void onResult(String resultEx) {

                            if (StrUtil.notEmptyOrNull(resultEx)) {
                                L.toastShort("评论成功~");
                                backDo();
                            }
                        }

                    }
            );


        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    private void backDo() {
        Intent back_intent = new Intent();
        setResult(RESULT_OK, back_intent);
        finish();
    }


}