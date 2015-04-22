package com.zncm.lovetuer.modules.base;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.alibaba.fastjson.JSON;
import com.zncm.lovetuer.data.base.TokeData;
import com.zncm.lovetuer.global.SharedApplication;
import com.zncm.utils.StrUtil;
import com.zncm.utils.sp.StatedPerference;

public class BaseHomeActivity extends SherlockFragmentActivity {
    protected ActionBar actionBar;
    protected TokeData tokeData;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tokeData = SharedApplication.getInstance().getTokeData();
        if (tokeData == null) {
            String toke = StatedPerference.getOauthToke();
            if (StrUtil.notEmptyOrNull(toke)) {
                tokeData = JSON.parseObject(StatedPerference.getOauthToke(), TokeData.class);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }
}
