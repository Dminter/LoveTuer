package com.zncm.lovetuer.modules.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.zncm.component.request.ReqService;
import com.zncm.component.request.ServiceParams;
import com.zncm.component.request.ServiceRequester;
import com.zncm.lovetuer.R;
import com.zncm.lovetuer.data.base.TokeData;
import com.zncm.lovetuer.global.GlobalConstants;
import com.zncm.lovetuer.utils.XUtil;
import com.zncm.utils.L;
import com.zncm.utils.StrUtil;
import com.zncm.utils.exception.CheckedExceptionHandler;
import com.zncm.utils.sp.StatedPerference;

import java.net.URLEncoder;

public class LoginActivity extends BaseHomeActivity {
    WebView wvLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_login);
        actionBar.setTitle("登陆授权");
        wvLogin = (WebView) findViewById(R.id.wvLogin);
        wvLogin.getSettings().setJavaScriptEnabled(false);
        wvLogin.loadUrl(GlobalConstants.OAUTH);
        wvLogin.setWebViewClient(new WebViewClient(
        ) {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url != null) {
                    L.i("url>" + url);
                    if (url.startsWith(GlobalConstants.CALLBACK_URL + "?code=")) {
                        String code = url.substring(url.lastIndexOf("=") + 1);
                        getToken(code);
                    } else if (url.equals(GlobalConstants.CALLBACK_ERROR_URL)) {
                        refreshDo();
                    }
                }
            }
        });

    }

    private void getToken(String code) {
        try {
            ServiceParams params = new ServiceParams();
            params.put("client_id", GlobalConstants.APP_KEY);
            params.put("client_secret", GlobalConstants.APP_SECRET);
            params.put("redirect_url", URLEncoder.encode(GlobalConstants.CALLBACK_URL, "UTF-8"));
            params.put("grant_type", "authorization_code");
            params.put("code", code);
            ReqService.postDataToServer(GlobalConstants.BASE_URL + "oauth/access_token", params,
                    new ServiceRequester() {

                        @Override
                        public void onResult(String resultEx) {
                            if (StrUtil.notEmptyOrNull(resultEx)) {
                                TokeData data = JSON.parseObject(resultEx, TokeData.class);
                                StatedPerference.setOauthToke(data.toString());
                                Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
            );
        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }

    }


    //icon do
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add("切换账号");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getTitle().equals("切换账号")) {
            refreshDo();
        }
        return false;

    }

    private void refreshDo() {
        XUtil.loginoutDo();
        if (wvLogin != null) {
            wvLogin.loadUrl(GlobalConstants.OAUTH_RENEW);
        }
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


}
