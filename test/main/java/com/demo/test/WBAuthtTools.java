

package com.demo.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.unity3d.player.UnityPlayer;

/**
 * 该类主要演示如何进行授权、SSO登陆。
 * 
 * @author SINA
 * @since 2013-09-29
 */
    public  class WBAuthtTools  {
    private static final String TAG = "weibosdk";

    private AuthInfo mAuthInfo;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    public  SsoHandler mSsoHandler;
    private Context context;
    public WBAuthtTools(Context context,Activity activity)
    {
        this.context=context;
        mAuthInfo = new AuthInfo(context, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(activity, mAuthInfo);
        mAccessToken = AccessTokenKeeper.readAccessToken(context);
        if (mAccessToken.isSessionValid()) {
            updateTokenView(true);
        }
    }

    //用法：

    //在MainActivity里声明一个全局的WBAuthtTools wbTools = new WBAuthtTools(this,MainActivity.this);

    // @Override
    // protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //     super.onActivityResult(requestCode, resultCode, data);
    //     if (wbTools != null) {
    //         wbTools.mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
    //     }

    // }
    public  void WBLogin()
   {
       //这个方法如果不是在ui线程调用，记得加在线程ui里
       if(!mSsoHandler.isWeiboAppInstalled())
       {
           Toast.makeText(context, "wei安装了WBBBBBBBBB。。。。。。。", Toast.LENGTH_SHORT).show();
           // map.put("WB","0");
           return;
       }
       Log.i("ALL IN ONE","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
       mSsoHandler.authorizeClientSso(new AuthListener());
   }
    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            UnityPlayer.UnitySendMessage("ThirdLoginGameobject","WeiboCallBack",values.toString());
            //从这里获取用户输入的 电话号码信息 
          //  String phoneNum =  mAccessToken.getUid();
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
                updateTokenView(false);

            } else {
                // 以下几种情况，您不会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
            }
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onWeiboException(WeiboException e) {

        }
    }

    /**
     * 显示当前 Token 信息。
     *
     * @param hasExisted 配置文件中是否已存在 token 信息并且合法
     */
    private void updateTokenView(boolean hasExisted) {

    }
}