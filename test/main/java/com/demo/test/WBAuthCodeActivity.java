/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.demo.test;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;

import java.text.SimpleDateFormat;

/**
 * 该类主要演示如何使用 Code 手动进行授权。
 * 
 * <b>请注意：</b>基于以下几点原因，您可能不想使用前面的两种授权方式：
 * <li>出于安全性的考虑，您不信任目前使用应用的包名和签名来获取 Token 的方式</li>
 * <li>您不想安装微博客户端</li></br>
 * 您可以自行通过 Code 来获取 Token。通过这种方式需要使用到 APP_SECRET，请务必妥善保管好
 * 自己的 APP_SECRET，<b>泄露有风险</b>。
 * 更多细节请查看：</b><a href="http://t.cn/zldm9tt">验证授权步骤</a>
 * 
 * @author SINA
 * @since 2013-10-18
 */
@SuppressWarnings("unused")
public class WBAuthCodeActivity extends Activity {

    private static final String TAG = "WBAuthCodeActivity";

    /**
     * WeiboSDKDemo 程序的 APP_SECRET。
     * 请注意：请务必妥善保管好自己的 APP_SECRET，不要直接暴露在程序中，此处仅作为一个DEMO来演示。
     */
    
    /** 通过 code 获取 Token 的 URL */
    private static final String OAUTH2_ACCESS_TOKEN_URL = "https://open.weibo.cn/oauth2/access_token";
    
    /** 微博 Web 授权接口类，提供登陆等功能  */
    private AuthInfo mAuthInfo;
    /** 获取到的 Code */
    private String mCode;
    /** 获取到的 Token */
    private Oauth2AccessToken mAccessToken;

    /**
     * @see {@link Activity#onCreate}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_auth_code);

        // 初始化微博对象
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
    }

    /**
     * 微博认证授权回调类。
     */
    class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            if (null == values) {
                return;
            }
            String code = values.getString("code");
            if (TextUtils.isEmpty(code)) {
                return;
            }
            mCode = code;
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onWeiboException(WeiboException e) {

        }
    }
    
    /**
     * 异步获取 Token。
     * 
     * @param authCode  授权 Code，该 Code 是一次性的，只能被获取一次 Token
     * @param appSecret 应用程序的 APP_SECRET，请务必妥善保管好自己的 APP_SECRET，
     *                  不要直接暴露在程序中，此处仅作为一个DEMO来演示。
     */
    public void fetchTokenAsync(String authCode, String appSecret) {
        
        WeiboParameters requestParams = new WeiboParameters(Constants.APP_KEY);
        requestParams.put(WBConstants.AUTH_PARAMS_CLIENT_ID,     Constants.APP_KEY);
        requestParams.put(WBConstants.AUTH_PARAMS_CLIENT_SECRET, appSecret);
        requestParams.put(WBConstants.AUTH_PARAMS_GRANT_TYPE,    "authorization_code");
        requestParams.put(WBConstants.AUTH_PARAMS_CODE,          authCode);
        requestParams.put(WBConstants.AUTH_PARAMS_REDIRECT_URL,  Constants.REDIRECT_URL);
        
        // 异步请求，获取 Token
        new AsyncWeiboRunner(getApplicationContext()).requestAsync(OAUTH2_ACCESS_TOKEN_URL, requestParams, "POST", new RequestListener() {
            @Override
            public void onComplete(String response) {
                
                // 获取 Token 成功
                Oauth2AccessToken token = Oauth2AccessToken.parseAccessToken(response);
                if (token != null && token.isSessionValid()) {
                    
                    mAccessToken = token;
                    String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                            new java.util.Date(mAccessToken.getExpiresTime()));
                    String format = token.toString();
                } else {

                }
            }

            @Override
            public void onWeiboException(WeiboException e) {

			}
        });
    }
}

