package com.jnb.grasp.wxapi;

import com.alibaba.fastjson.JSON;
import com.jnb.grasp.Constants;
import com.jnb.grasp.WeiXinInfo;
import com.jnb.grasp.WeiXinToken;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth.Resp;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.unity3d.player.UnityPlayer;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import okhttp3.Call;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	// IWXAPI：第三方APP和微信通信的接口
	private IWXAPI api;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
		// 如果分享的时候，该界面没有开启，那么微信开始这个activity时，会调用onCreate，所以这里要处理微信的返回结果
		api.handleIntent(getIntent(), this);
	}

	// 如果分享的时候，该已经开启，那么微信开始这个activity时，会调用onNewIntent，所以这里要处理微信的返回结果
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq arg0) {
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	// 比如：在微信完成文本分享操作后，回调第三方APP
	@Override
	public void onResp(BaseResp resp) {
		String result;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			// 处理登录授权成功回调,获取token（注意：新版的SDK，可以直接获取Token）

			Bundle bundle = new Bundle();
			resp.toBundle(bundle);
			Resp sp = new Resp(bundle);
			String token = sp.token;
			requestOpenId(token);
			Toast.makeText(WXEntryActivity.this, token, Toast.LENGTH_SHORT).show();

			// 特别说明：新版SDK貌似不支持获取OpenId，官方文档也没有更新，也没有获取OpenID的具体说明

			result = "授权成功!";
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = "发送取消";
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "发送拒绝";
			break;
		default:
			result = "发送返回";
			break;
		}

		Toast.makeText(this, result, Toast.LENGTH_LONG).show();

		this.finish();
	}

	private void requestOpenId(final String token) {
		String urlstr = "https://api.weixin.qq.com/sns/oauth2/access_token?" + "appid=" + Constants.APP_ID + "&secret="
				+ Constants.APP_Secret + "&code=" + token + "&grant_type=authorization_code";

		OkHttpUtils.get().url(urlstr).build().execute(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int i) {
				Toast.makeText(WXEntryActivity.this, "获取登陆信息失败", Toast.LENGTH_LONG).show();
			}

			// 获取登陆信息成功后,发送获取用户信息GET请求
			@Override
			public void onResponse(String resp, int i) {
				GetUserInfo(resp);
			}
		});
	}

	// 获取用户信息
	void GetUserInfo(String resp) {
		WeiXinToken tokenInfo = JSON.parseObject(resp, WeiXinToken.class);
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + tokenInfo.getAccess_token() + "&openid="
				+ tokenInfo.getOpenid();
		OkHttpUtils.get().url(url).build().execute(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int i) {
				Toast.makeText(WXEntryActivity.this.getApplicationContext(), "获取用户信息失败", Toast.LENGTH_LONG).show();
			}

			// 获取用户信息成功后回调unity函数
			@Override
			public void onResponse(String resp, int i) {
				Toast.makeText(WXEntryActivity.this, "获取登陆信息", Toast.LENGTH_SHORT).show();
				WeiXinInfo info = JSON.parseObject(resp, WeiXinInfo.class);
//				Toast.makeText(WXEntryActivity.this.getApplicationContext(), info.getNickname(), Toast.LENGTH_LONG)
//						.show();
//				Toast.makeText(WXEntryActivity.this.getApplicationContext(), "性别" + info.getSex(), Toast.LENGTH_LONG)
//						.show();
//				Toast.makeText(WXEntryActivity.this.getApplicationContext(), "Openid" + info.getOpenid(),
//						Toast.LENGTH_LONG).show();
				//回调unity函数
				//三个参数分别是gameobject名，脚本内的方法名，以及回调的参数
				UnityPlayer.UnitySendMessage("GameMgr","RespStartParam",resp.toString()); 
			}
		});
	}

}
