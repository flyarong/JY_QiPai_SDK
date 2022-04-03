package com.demo.test;

import android.app.Application;
import android.content.Context;

import com.opet.adventure.wxapi.AppConst;
import com.opet.adventure.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Star_Spark on 2016/11/17.
 */

public class MyApplication extends Application {
    public static IWXAPI sApi;
    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        //初始化微信
        sApi = WXEntryActivity.initWeiXin(this, AppConst.WEIXIN_APP_ID);
        initOkHttp();
    }
    //封装okhttp框架的初始化配置
    private void initOkHttp() {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("TAG"))
                .cookieJar(cookieJar)
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L,TimeUnit.MILLISECONDS)
                .writeTimeout(20000L,TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }
}
