package com.zncm.lovetuer.modules.setting;

import android.os.Bundle;

import com.zncm.lovetuer.R;
import com.zncm.lovetuer.modules.base.BaseHomeActivity;


public class SettingNormalActivity extends BaseHomeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_setting_normal);
        actionBar.setTitle("常规");
        initViews();
    }

    private void initViews() {
    }


}
