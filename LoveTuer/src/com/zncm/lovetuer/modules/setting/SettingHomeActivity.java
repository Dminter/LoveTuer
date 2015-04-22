package com.zncm.lovetuer.modules.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableRow;

import com.umeng.analytics.MobclickAgent;
import com.zncm.lovetuer.R;
import com.zncm.lovetuer.modules.other.About;
import com.zncm.lovetuer.modules.base.BaseHomeActivity;

public class SettingHomeActivity extends BaseHomeActivity implements OnClickListener {
    private TableRow normal;
    private TableRow aboutapp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_setting_home);
        actionBar.setTitle("设置");
        initViews();
    }

    private void initViews() {
        normal = (TableRow) findViewById(R.id.tablerow_normal);
        aboutapp = (TableRow) findViewById(R.id.tablerow_aboutapp);
        normal.setOnClickListener(this);
        aboutapp.setOnClickListener(this);
    }

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
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.tablerow_normal:
                intent = new Intent(this, SettingNormalActivity.class);
                startActivity(intent);
                break;
            case R.id.tablerow_aboutapp:
                intent = new Intent(this, About.class);
                startActivity(intent);
                break;
        }
    }

}
