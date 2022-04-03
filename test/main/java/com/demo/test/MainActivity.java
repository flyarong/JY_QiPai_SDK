package com.demo.test;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gotye.api.GotyeAPI;
import com.opet.adventure.Bean.WXAccessTokenInfo;
import com.opet.adventure.wxapi.AppConst;
import com.opet.adventure.wxapi.WXEntryActivity;
import com.sina.weibo.sdk.utils.LogUtil;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class MainActivity extends UnityPlayerActivity {
    private IUiListener listener;
    private String APP_ID="你的ID";//QQ id
    private String Scope="get_user_info,get_simple_userinfo";
    private static final String WEIXIN_ACCESS_TOKEN_KEY = "wx_access_token_key";
    private static final String WEIXIN_OPENID_KEY = "wx_openid_key";
    private static final String WEIXIN_REFRESH_TOKEN_KEY = "wx_refresh_token_key";
    private Tencent mTencent;
    private BaseUiListener mBaseUiListener=new BaseUiListener();
    Context mContext = null;
    private boolean hasQQ;
    private boolean hasWX;
    public static  WBAuthtTools wbTools;
    private Map<String, String> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WBAuthtTools wbTools = new WBAuthtTools(this,MainActivity.this);
        mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
        LogUtil.sIsLogEnable = true;
        // 在这里完成亲加API的初始化
        GotyeAPI.getInstance().init(getApplicationContext(), " f5a57c29-19d5-41fe-bff1-c6069dac0f92", GotyeAPI.SCENE_UNITY3D);
        mContext=this;
        hasQQ=checkApkExist(this,"com.tencent.mobileqq");
        hasWX=checkApkExist(this,"com.tencent.mm");
        map = new HashMap<String, String>();
        if (!hasWX) {        //wei安装了wx的操作
            map.put("wechat","0");
        }
        else
        {
            map.put("wechat","1");
        }
        if (!hasQQ) {        //wei安装了qq的操作
            map.put("qq","0");
        }
        else
        {
            map.put("qq","1");
        }
        if (!wbTools.mSsoHandler.isWeiboAppInstalled()) {        //wei安装了weibo的操作
            map.put("sina","0");
        }
        else
        {
            map.put("sina","1");
        }
    }
    //判断是否安装某个应用方法
    public boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
public void ThirdAppInstall()
{
    UnityPlayer.UnitySendMessage("ThirdLoginGameobject","ThirdAppInstallCallBack",JSON.toJSONString(map));
}
    //微博登录
    public void WBLogin()
    {
        wbTools.WBLogin();
    }
    //微信登录
    public void weiLogin() {
      //  Toast.makeText(MainActivity.this, "点到微信登录了。。。。", Toast.LENGTH_SHORT).show();
        if (!WXEntryActivity.api.isWXAppInstalled())
        {
            Toast.makeText(MainActivity.this, "请安装微信", Toast.LENGTH_LONG).show();
            return;
        }
        WXEntryActivity.loginWeixin(MainActivity.this, MyApplication.sApi, new WXEntryActivity.WeChatCode() {
            @Override
            public void getResponse(String code) {
                // 通过code获取授权口令access_token
                getAccessToken(code);
                Log.i("获取token成功",code.toString());
                // 从手机本地获取存储的授权口令信息，判断是否存在access_token，不存在请求获取，存在就判断是否过期
                String accessToken = (String) ShareUtils.getValue(MainActivity.this, WEIXIN_ACCESS_TOKEN_KEY,
                        "none");
                String openid = (String) ShareUtils.getValue(MainActivity.this, WEIXIN_OPENID_KEY, "");
                if (!"none".equals(accessToken)) {
                    // 有access_token，判断是否过期有效
                    isExpireAccessToken(accessToken, openid);
                } else {
                    // 没有access_token
                    getAccessToken(code);
                    Log.i("获取token成功",code.toString());
                }
            }
        });
    }

    /**
     * qq登录代码
     */
    public void QQlogin() {
        mTencent=Tencent.createInstance(APP_ID,this.getApplicationContext());
        listener=new BaseUiListener()
        {
            @Override
            protected void doComplete(JSONObject values)
            {
                Log.i("回调成功","aaaaaaaaaaaaaaaaa");
            }
        };
        //登录代码。。。
            if (!mTencent.isSessionValid())
            {
                mTencent.login(this,Scope, mBaseUiListener);
            }
    }

    /**
     * qq请求回调
     */
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            doComplete((JSONObject)o);
            JSONObject response=(JSONObject)o;
            UnityPlayer.UnitySendMessage("ThirdLoginGameobject","QQCallBack",o.toString());
        }
        protected void doComplete(JSONObject values)
        {

        }
        @Override
        public void onError(UiError uiError) {
        }

        @Override
        public void onCancel() {

        }
    }

    /**
     * qq登录页面返回处理
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode,resultCode,data,mBaseUiListener);
             super.onActivityResult(requestCode, resultCode, data);
             if (wbTools != null) {
                 wbTools.mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
             }
    }
    /**
     * 微信登录获取授权口令
     */
    private void getAccessToken(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=" + AppConst.WEIXIN_APP_ID +
                "&secret=" + AppConst.WEIXIN_APP_SECRET +
                "&code=" + code +
                "&grant_type=authorization_code";
        // 网络请求获取access_token
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                // 判断是否获取成功，成功则去获取用户信息，否则提示失败
                processGetAccessTokenResult(response);
            }
        });

    }

    /**
     * 微信登录处理获取的授权信息结果
     *
     * @param response 授权信息结果
     */
    public void processGetAccessTokenResult(String response) {
        // 验证获取授权口令返回的信息是否成功
        if (validateSuccess(response)) {
            // 使用Gson解析返回的授权口令信息
          //  WXAccessTokenInfo tokenInfo = mGson.fromJson(response, WXAccessTokenInfo.class);
            // 保存信息到手机本地
          //  saveAccessInfotoLocation(tokenInfo);
            // 获取用户信息
          //  getUserInfo(tokenInfo.getAccess_token(), tokenInfo.getOpenid());
        } else {
            // 授权口令获取失败，解析返回错误信息
          //  WXErrorInfo wxErrorInfo = mGson.fromJson(response, WXErrorInfo.class);

        }
    }

    /**
     *微信登录获取tokenInfo的WEIXIN_OPENID_KEY，WEIXIN_ACCESS_TOKEN_KEY，WEIXIN_REFRESH_TOKEN_KEY保存到shareprephence中
     * @param tokenInfo
     */
    private void saveAccessInfotoLocation(WXAccessTokenInfo tokenInfo) {
        ShareUtils.saveValue(MyApplication.mContext,WEIXIN_OPENID_KEY,tokenInfo.getOpenid());
        ShareUtils.saveValue(MyApplication.mContext,WEIXIN_ACCESS_TOKEN_KEY,tokenInfo.getAccess_token());
        ShareUtils.saveValue(MyApplication.mContext,WEIXIN_REFRESH_TOKEN_KEY,tokenInfo.getRefresh_token());
    }

    /**
     * 验证是否成功
     *
     * @param response 返回消息
     * @return 是否成功
     */
    private boolean validateSuccess(String response) {
        String errFlag = "errmsg";
        return (errFlag.contains(response) && !"ok".equals(response))
                || (!"errcode".contains(response) && !errFlag.contains(response));
    }


    /**
     * 微信登录判断accesstoken是过期
     *
     * @param accessToken token
     * @param openid      授权用户唯一标识
     */
    private void isExpireAccessToken(final String accessToken, final String openid) {
        String url = "https://api.weixin.qq.com/sns/auth?" +
                "access_token=" + accessToken +
                "&openid=" + openid;
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                if (validateSuccess(response)) {
                    // accessToken没有过期，获取用户信息
                    getUserInfo(accessToken, openid);
                    UnityPlayer.UnitySendMessage("ThirdLoginGameobject","WeChatCallBack",response.toString());
                    Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();

                } else {
                    // 过期了，使用refresh_token来刷新accesstoken
                    refreshAccessToken();
                }
            }
        });

    }
    /**
     * 微信登录刷新获取新的access_token
     */
    private void refreshAccessToken() {
        // 从本地获取以存储的refresh_token
        final String refreshToken = (String) ShareUtils.getValue(this, WEIXIN_REFRESH_TOKEN_KEY,
                "");
        if (TextUtils.isEmpty(refreshToken)) {
            return;
        }
        // 拼装刷新access_token的url请求地址
        String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?" +
                "appid=" + AppConst.WEIXIN_APP_ID +
                "&grant_type=refresh_token" +
                "&refresh_token=" + refreshToken;
        // 请求执行
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                System.out.println("刷新获取新的access_token信息失败！！！");
                // 重新请求授权
                weiLogin();
            }

            @Override
            public void onResponse(String response, int id) {
                // 判断是否获取成功，成功则去获取用户信息，否则提示失败
                processGetAccessTokenResult(response);
            }
        });

    }
    /**
     * 微信token验证成功后，联网获取用户信息
     * @param access_token
     * @param openid
     */
    private void getUserInfo(String access_token, String openid) {
        String url = "https://api.weixin.qq.com/sns/userinfo?" +
                "access_token=" + access_token +
                "&openid=" + openid;
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                System.out.println("联网获取用户信息失败！！！");
            }

            @Override
            public void onResponse(String response, int id) {
                // 解析获取的用户信息
               // WXUserInfo userInfo = mGson.fromJson(response, WXUserInfo.class);
                System.out.println("获取用户信息String是：：：：：："+response);

            }
        });
    }

    //Unity中会调用这个方法，用于区分打开摄像机 开始本地相册
    public void TakePhoto(String str)
    {
        Intent intent = new Intent(mContext,WebViewActivity.class);
        intent.putExtra("type", str);
        this.startActivity(intent);
    }

}
