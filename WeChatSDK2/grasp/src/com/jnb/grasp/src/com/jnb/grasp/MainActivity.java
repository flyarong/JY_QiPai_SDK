package com.jnb.grasp;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.unity3d.player.UnityPlayerActivity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends UnityPlayerActivity{
	// IWXAPI：第三方APP和微信通信的接口
	private IWXAPI api;
	// Activity启动项
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// 根据应用ID创建新的微信API用于与微信交互
			api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
			api.registerApp(Constants.APP_ID);
		}
		@Override
		protected void onNewIntent(Intent intent) {
			super.onNewIntent(intent);
		}
	// 登录授权
	public void LoginWeiXin() {
		final SendAuth.Req req 	= new SendAuth.Req();
		req.scope 				= "snsapi_userinfo";
		req.state 				= "wechat_sdk_demo_test";
		api.sendReq(req);
	}
}
