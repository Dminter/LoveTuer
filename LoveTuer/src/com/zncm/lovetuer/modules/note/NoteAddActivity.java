package com.zncm.lovetuer.modules.note;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.zncm.component.request.ReqService;
import com.zncm.component.request.ServiceParams;
import com.zncm.component.request.ServiceRequester;
import com.zncm.lovetuer.R;
import com.zncm.lovetuer.data.base.NoteBookData;
import com.zncm.lovetuer.data.base.NoteData;
import com.zncm.lovetuer.data.base.TokeData;
import com.zncm.lovetuer.global.GlobalConstants;
import com.zncm.lovetuer.global.SharedApplication;
import com.zncm.lovetuer.modules.base.BaseHomeActivity;
import com.zncm.lovetuer.utils.XUtil;
import com.zncm.utils.L;
import com.zncm.utils.StrUtil;
import com.zncm.utils.ViewUtils;
import com.zncm.utils.exception.CheckedExceptionHandler;
import com.zncm.utils.sp.StatedPerference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class NoteAddActivity extends BaseHomeActivity implements OnClickListener,
        OnNavigationListener {
    private EditText etContent;
    private EditText etLocation;
    private TextView tvXi;
    private TextView tvNu;
    private TextView tvAi;
    private TextView tvLe;
    private TextView tvYin;
    private TextView tvQing;
    private TextView tvXue;
    private TextView tvYu;
    private Checkable cbPrivacy;
    private Checkable cbReplyable;
    //---------------
    private ArrayList<String> tags;
    private NoteData noteData;
    private boolean bModify = false;
    private InputMethodManager imm;
    private List<NoteBookData> noteBookDatas;
    private int curPosition = 0;
    private int mood = 0;
    private int weather = 0;
    //----------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_note_add);
        actionBar.setDisplayShowTitleEnabled(false);
        initView();
        initOtherAppDatas();
        initData();
    }


    private void initOtherAppDatas() {
        Intent intent = getIntent();
        if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND)) {
            Bundle extras = intent.getExtras();
            String content = String.valueOf(extras.getString(Intent.EXTRA_TEXT));
            tokeData = SharedApplication.getInstance().getTokeData();
            if (tokeData == null) {
                String toke = StatedPerference.getOauthToke();
                if (StrUtil.notEmptyOrNull(toke)) {
                    tokeData = JSON.parseObject(StatedPerference.getOauthToke(), TokeData.class);
                    SharedApplication.getInstance().setTokeData(tokeData);
                }
            }
            if (etContent != null) {
                etContent.setText(content);
            }
        }
    }

    private void initData() {
        bModify = getIntent().getBooleanExtra(GlobalConstants.KEY_MODIFY, false);
        if (bModify) {
            Serializable dataParam = getIntent().getSerializableExtra(GlobalConstants.KEY_PARAM_DATA);
            noteData = (NoteData) dataParam;
            if (noteData != null) {
                if (StrUtil.notEmptyOrNull(noteData.getLocation())) {
                    etLocation.setText(noteData.getLocation());
                }
                if (StrUtil.notEmptyOrNull(noteData.getContent())) {
                    etContent.setText(noteData.getContent());
                }
                if (noteData.getPrivacy() == 0) {
                    cbPrivacy.setChecked(false);
                } else {
                    cbPrivacy.setChecked(true);
                }

                if (noteData.getWeather().equals("晴")) {
                    weatherDo(0);
                } else if (noteData.getWeather().equals("阴")) {
                    weatherDo(1);
                } else if (noteData.getWeather().equals("雨")) {
                    weatherDo(2);
                } else if (noteData.getWeather().equals("雪")) {
                    weatherDo(3);
                }
                if (noteData.getMood().equals("喜")) {
                    moodDo(0);
                } else if (noteData.getMood().equals("怒")) {
                    moodDo(1);
                } else if (noteData.getMood().equals("哀")) {
                    moodDo(2);
                } else if (noteData.getMood().equals("乐")) {
                    moodDo(3);
                }

            }
        }
        initTags();
        ArrayAdapter<String> list = new ArrayAdapter<String>(this, R.layout.sherlock_spinner_item, tags);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(list, this);
    }

    private void initTags() {
        try {
            noteBookDatas = new ArrayList<NoteBookData>();
            tags = new ArrayList<String>();
            noteBookDatas = SharedApplication.getInstance().getNoteBookDatas();
            if (noteBookDatas == null) {
                String noteBookInfo = StatedPerference.getNoteBookData();
                noteBookDatas = JSON.parseArray(noteBookInfo, NoteBookData.class);
            }
            if (StrUtil.listNotNull(noteBookDatas)) {
                for (NoteBookData data : noteBookDatas) {
                    tags.add(data.getName());
                }
            }
        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }
    }

    private void initView() {
        imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        etContent = (EditText) findViewById(R.id.etContent);
        etContent.addTextChangedListener(mTextWatcher);
        etLocation = (EditText) findViewById(R.id.etLocation);
        tvXi = (TextView) findViewById(R.id.tvXi);
        tvNu = (TextView) findViewById(R.id.tvNu);
        tvAi = (TextView) findViewById(R.id.tvAi);
        tvLe = (TextView) findViewById(R.id.tvLe);
        tvYin = (TextView) findViewById(R.id.tvYin);
        tvQing = (TextView) findViewById(R.id.tvQing);
        tvXue = (TextView) findViewById(R.id.tvXue);
        tvYu = (TextView) findViewById(R.id.tvYu);
        cbPrivacy = (CheckBox) findViewById(R.id.cbPrivacy);
        cbReplyable = (CheckBox) findViewById(R.id.cbReplyable);
        tvXi.setOnClickListener(this);
        tvNu.setOnClickListener(this);
        tvAi.setOnClickListener(this);
        tvLe.setOnClickListener(this);
        tvYin.setOnClickListener(this);
        tvQing.setOnClickListener(this);
        tvXue.setOnClickListener(this);
        tvYu.setOnClickListener(this);

    }

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            ViewUtils.setTextView(NoteAddActivity.this, R.id.tvCount,
                    String.valueOf(temp.length()) + "字");
        }

    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tvXi:
                moodDo(0);
                break;
            case R.id.tvNu:
                moodDo(1);

                break;
            case R.id.tvAi:
                moodDo(2);
                break;
            case R.id.tvLe:
                moodDo(3);
                break;
            case R.id.tvYin:
                weatherDo(1);
                break;
            case R.id.tvQing:
                weatherDo(0);
                break;
            case R.id.tvXue:
                weatherDo(3);
                break;
            case R.id.tvYu:
                weatherDo(2);
                break;

        }

    }

    private void moodDo(int type) {
        mood = type;
        switch (type) {
            case 0:
                tvXi.setBackgroundResource(R.drawable.tag_sel);
                tvNu.setBackgroundResource(R.drawable.tag_nor);
                tvAi.setBackgroundResource(R.drawable.tag_nor);
                tvLe.setBackgroundResource(R.drawable.tag_nor);
                break;
            case 1:
                tvXi.setBackgroundResource(R.drawable.tag_nor);
                tvNu.setBackgroundResource(R.drawable.tag_sel);
                tvAi.setBackgroundResource(R.drawable.tag_nor);
                tvLe.setBackgroundResource(R.drawable.tag_nor);
                break;
            case 2:
                tvXi.setBackgroundResource(R.drawable.tag_nor);
                tvNu.setBackgroundResource(R.drawable.tag_nor);
                tvAi.setBackgroundResource(R.drawable.tag_sel);
                tvLe.setBackgroundResource(R.drawable.tag_nor);
                break;
            case 3:
                tvXi.setBackgroundResource(R.drawable.tag_nor);
                tvNu.setBackgroundResource(R.drawable.tag_nor);
                tvAi.setBackgroundResource(R.drawable.tag_nor);
                tvLe.setBackgroundResource(R.drawable.tag_sel);
                break;
        }

    }

    private void weatherDo(int type) {
        weather = type;
        switch (type) {
            case 0:
                tvQing.setBackgroundResource(R.drawable.tag_sel);
                tvYin.setBackgroundResource(R.drawable.tag_nor);
                tvYu.setBackgroundResource(R.drawable.tag_nor);
                tvXue.setBackgroundResource(R.drawable.tag_nor);
                break;
            case 1:
                tvQing.setBackgroundResource(R.drawable.tag_nor);
                tvYin.setBackgroundResource(R.drawable.tag_sel);
                tvYu.setBackgroundResource(R.drawable.tag_nor);
                tvXue.setBackgroundResource(R.drawable.tag_nor);
                break;
            case 2:
                tvQing.setBackgroundResource(R.drawable.tag_nor);
                tvYin.setBackgroundResource(R.drawable.tag_nor);
                tvYu.setBackgroundResource(R.drawable.tag_sel);
                tvXue.setBackgroundResource(R.drawable.tag_nor);
                break;
            case 3:
                tvQing.setBackgroundResource(R.drawable.tag_nor);
                tvYin.setBackgroundResource(R.drawable.tag_nor);
                tvYu.setBackgroundResource(R.drawable.tag_nor);
                tvXue.setBackgroundResource(R.drawable.tag_sel);
                break;
        }

    }


    private void saveExit() {
        if (bModify) {
            editNote(noteData.getId());
        } else {
            saveNote();
        }
        backDo();
    }


    private void editNote(int _id) {
        try {

            String content = etContent.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            if (StrUtil.isEmptyOrNull(location)) {
                location = "未知";
            }
            ServiceParams params = new ServiceParams();
            params.put("content", content);
            params.put("bookid", String.valueOf(noteBookDatas.get(curPosition).getId()));
            params.put("privacy", cbPrivacy.isChecked() ? "1" : "0");
            params.put("forbid", cbReplyable.isChecked() ? "1" : "0");
            params.put("location", location);
            params.put("mood", String.valueOf(mood));
            params.put("weather", String.valueOf(weather));
            ReqService.postDataToServer(GlobalConstants.BASE_API_URL + "diary/edit/" + _id, params,
                    new ServiceRequester() {

                        @Override
                        public void onResult(String resultEx) {
                            if (StrUtil.notEmptyOrNull(resultEx)) {
                                L.toastShort("日记修改成功~");
                            }
                        }
                    }
            );
        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }

    }

    private void backDo() {
        Intent back_intent = new Intent();
        back_intent.putExtra(GlobalConstants.KEY_PARAM_DATA, noteData);
        setResult(RESULT_OK, back_intent);
        imm.hideSoftInputFromWindow(etContent.getWindowToken(), 0);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        }, 100);
    }


    private void saveNote() {
        try {
            String content = etContent.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            if (StrUtil.isEmptyOrNull(location)) {
                location = "未知";
            }
            final Message msg = new Message();
            ServiceParams params = new ServiceParams();
            params.put("content", content);
            params.put("bookid", String.valueOf(noteBookDatas.get(curPosition).getId()));
            params.put("privacy", cbPrivacy.isChecked() ? "1" : "0");
            params.put("forbid", cbReplyable.isChecked() ? "1" : "0");
            params.put("location", location);
            params.put("mood", String.valueOf(mood));
            params.put("weather", String.valueOf(weather));
            ReqService.postDataToServer(GlobalConstants.BASE_API_URL + "diary/save", params,
                    new ServiceRequester() {

                        @Override
                        public void onResult(String resultEx) {
                            if (StrUtil.notEmptyOrNull(resultEx)) {
                                L.toastShort("日记发布成功~");
                            }
                        }
                    }
            );
        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backAsk();
        }
        return false;
    }

    private void backAsk() {

        if (StrUtil.isEmptyOrNull(etContent.getText().toString())) {
            finish();
            return;
        }

        DialogFragment newFragment = new XUtil.TwoAlertDialogFragment("日记尚未保存,确定要退出吗?") {

            @Override
            public void doPositiveClick() {
                imm.hideSoftInputFromWindow(etContent.getWindowToken(), 0);
                finish();
            }

            @Override
            public void doNegativeClick() {

            }
        };
        newFragment.show(getSupportFragmentManager(), "dialog");

    }

    // umeng

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add("save");
        item.setIcon(R.drawable.save);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getTitle().equals("save")) {
            if (StrUtil.notEmptyOrNull(etContent.getText().toString())) {
                saveExit();
            } else {
                L.toastShort("先写点什么吧~");
            }
        }

        return false;

    }


    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        curPosition = itemPosition;
        return false;
    }


}
