package com.demo.test.wxapi;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.opet.adventure.MyApplication;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String WEIXIN_ACCESS_TOKEN_KEY = "wx_access_token_key";
    private static final String WEIXIN_OPENID_KEY = "wx_openid_key";
    private static final String WEIXIN_REFRESH_TOKEN_KEY = "wx_refresh_token_key";
    public static WeChatCode mWeChatCode;
    public static IWXAPI api;
    public WeChatCode getWeChatCode() {
        return mWeChatCode;
    }

    public void setWeChatCode(WeChatCode weChatCode) {
        mWeChatCode = weChatCode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 微信事件回调接口注册
        MyApplication.sApi.handleIntent(getIntent(), this);
    }

    //微信组件注册初始化

     public static IWXAPI initWeiXin(Context context, @NonNull String weixin_app_id) {
     if (TextUtils.isEmpty(weixin_app_id)) {
     Toast.makeText(context.getApplicationContext(), "app_id 不能为空", Toast.LENGTH_SHORT).show();
     }
         api = WXAPIFactory.createWXAPI(context, weixin_app_id, true);
     api.registerApp(weixin_app_id);
     return api;
     }

    /**
     * 登录微信
     */
    public static void loginWeixin(Context context, IWXAPI api, WeChatCode wechatCode) {
        if (api.isWXAppInstalled())
        {
            Toast.makeText(context.getApplicationContext(), "请安装微信", Toast.LENGTH_LONG).show();
            return;
        }
        mWeChatCode=wechatCode;
        // 发送授权登录信息，来获取code
        SendAuth.Req req = new SendAuth.Req();
        // 应用的作用域，获取个人信息
        req.scope = "snsapi_userinfo";
        /**
         * 用于保持请求和回调的状态，授权请求后原样带回给第三方
         * 为了防止csrf攻击（跨站请求伪造攻击），后期改为随机数加session来校验
         */
        req.state = "app_wechat";
        api.sendReq(req);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            // 发送成功
            case BaseResp.ErrCode.ERR_OK:
                // 获取code
                String code = ((SendAuth.Resp) resp).code;
                mWeChatCode.getResponse(code);
                break;
        }
    }

    /**
     * 返回code的回调接口
     */
    public interface WeChatCode {
        void getResponse(String code);
    }
}