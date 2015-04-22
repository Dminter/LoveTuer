package com.zncm.lovetuer.modules.note;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zncm.lovetuer.R;
import com.zncm.lovetuer.global.GlobalConstants;
import com.zncm.lovetuer.modules.base.BaseHomeActivity;

public class NoteContent extends BaseHomeActivity {

    private String content;
    private TextView tvContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_note_content);
        actionBar.setTitle("日记详情");
        content = getIntent().getExtras().getString(GlobalConstants.KEY_DATA);
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvContent.setText(Html.fromHtml(content));
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