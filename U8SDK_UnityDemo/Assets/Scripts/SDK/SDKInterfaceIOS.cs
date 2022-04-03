#if UNITY_IPHONE
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;

public class SDKInterfaceIOS : U8SDKInterface
{
	[DllImport("__Internal")]
	private static extern void U8SDK_Setup();

	[DllImport("__Internal")]
	private static extern void U8SDK_Login();

	[DllImport("__Internal")]
	private static extern void U8SDK_LoginCustom(string customData);

	[DllImport("__Internal")]
	private static extern void U8SDK_SwitchLogin();

	[DllImport("__Internal")]
	private static extern bool U8SDK_Logout();

	[DllImport("__Internal")]
	private static extern bool U8SDK_ShowAccountCenter();

	[DllImport("__Internal")]
	private static extern bool U8SDK_SubmitGameData(string extraData);

	[DllImport("__Internal")]
	private static extern bool U8SDK_SDKExit();

	[DllImport("__Internal")]
	private static extern void U8SDK_Pay (string payParams);

	[DllImport("__Internal")]
	private static extern bool U8SDK_IsSupportExit();

	[DllImport("__Internal")]
	private static extern bool U8SDK_IsSupportAccountCenter ();

	[DllImport("__Internal")]
	private static extern bool U8SDK_IsSupportLogout ();

	// move init to application didFinishLaunchingWithOptions
    public override void Init()
    {
    }

	public void Setup()
	{
		U8SDK_Setup();
	}

    public override void Login()
    {
		U8SDK_Login();
    }

    public override void LoginCustom(string customData)
    {
		U8SDK_LoginCustom(customData);
    }

    public override void SwitchLogin()
    {
		U8SDK_SwitchLogin();
    }

    public override bool Logout()
    {
		return U8SDK_Logout();
    }

    public override bool ShowAccountCenter()
    {
		return U8SDK_ShowAccountCenter();
    }

    public override void SubmitGameData(U8ExtraGameData data)
	{
		string json = encodeGameData(data);
		U8SDK_SubmitGameData(json);
    }

    public override bool SDKExit()
    {
		return U8SDK_SDKExit();
    }

    public override void Pay(U8PayParams data)
	{
		string json = encodePayParams(data);
		U8SDK_Pay(json);
    }

    public override bool IsSupportExit()
    {
		return U8SDK_IsSupportExit();
    }

    public override bool IsSupportAccountCenter()
    {
		return U8SDK_IsSupportAccountCenter();
    }

    public override bool IsSupportLogout()
    {
        return U8SDK_IsSupportLogout();
    }
	
	private string encodeGameData(U8ExtraGameData data)
	{
		Dictionary<string, object> map = new Dictionary<string, object>();
		map.Add("dataType", data.dataType);
		map.Add("roleID", data.roleID);
		map.Add("roleName", data.roleName);
		map.Add("roleLevel", data.roleLevel);
		map.Add("serverID", data.serverID);
		map.Add("serverName", data.serverName);
		map.Add("moneyNum", data.moneyNum);
		return MiniJSON.Json.Serialize(map);        
	}
	
	private string encodePayParams(U8PayParams data)
	{
		Dictionary<string, object> map = new Dictionary<string, object>();
		map.Add("productId", data.productId);
		map.Add("productName", data.productName);
		map.Add("productDesc", data.productDesc);
		map.Add("price", data.price);
		map.Add("buyNum", data.buyNum);
		map.Add("coinNum", data.coinNum);
		map.Add("serverId", data.serverId);
		map.Add("serverName", data.serverName);
		map.Add("roleId", data.roleId);
		map.Add("roleName", data.roleName);
		map.Add("roleLevel", data.roleLevel);
		map.Add("vip", data.vip);
		map.Add("orderID", data.orderID);
		map.Add("extension", data.extension);
		
		return MiniJSON.Json.Serialize(map);        
	}
}
#endif