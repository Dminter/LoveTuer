

package com.zncm.lovetuer.modules.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.zncm.lovetuer.R;
import com.zncm.lovetuer.data.base.TokeData;
import com.zncm.lovetuer.global.SharedApplication;
import com.zncm.lovetuer.utils.XUtil;
import com.zncm.utils.DeviceUtil;
import com.zncm.utils.StrUtil;
import com.zncm.utils.ViewUtils;
import com.zncm.utils.sp.StatedPerference;

public class SplashActivity extends Activity {
    private Handler handler;
    private boolean bIncome = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_splash);
        ViewUtils.setTextView(this, R.id.tvAppInfo,
                getResources().getString(R.string.app_name) + " " + DeviceUtil.getVersionName(SplashActivity.this));
        String toke = StatedPerference.getOauthToke();
        if (StrUtil.notEmptyOrNull(toke)) {
            bIncome = true;
            TokeData tokeData = JSON.parseObject(StatedPerference.getOauthToke(), TokeData.class);
            SharedApplication.getInstance().setTokeData(tokeData);
            XUtil.getNoteBook();
        } else {
            bIncome = false;
        }
        handler = new Handler();
        handler.postDelayed(startAct, 1500);
    }


    Runnable startAct = new Runnable() {

        @Override
        public void run() {
            if (bIncome) {
                startActivity(new Intent(SplashActivity.this, MainTabsPager.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

    };


}
