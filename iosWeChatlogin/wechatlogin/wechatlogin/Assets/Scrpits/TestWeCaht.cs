using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Runtime.InteropServices;

public class TestWeCaht : MonoBehaviour
{

    #region 与oc交互相关的代码
#if UNITY_IOS
    #region IOS Native
        private const string Native = "__Internal";
        [DllImport(Native)]
        private static extern void WechatInit(string app_id);
        [DllImport(Native)]
        private static extern bool WechatLogin();
    #endregion
#endif

    public void initWeChat()
    {
#if UNITY_EDITOR
#elif UNITY_ANDROID
#elif UNITY_IOS
      WechatInit("wxeb769e4dffd31c3c");//换成你自己应用的微信APPID
#endif
    }

    public bool isWeChatInstalled()
    {
#if UNITY_EDITOR
        return false;
#elif UNITY_ANDROID
        return false;
#elif UNITY_IOS
        return isWXAppInstalled();
#endif
    }

    public void LoginWeChat()
    {
#if UNITY_EDITOR
        wechatcode = "请用安装了微信的设备测试";
#elif UNITY_ANDROID
         wechatcode = "本例子暂时只支持iOS设备";
#elif UNITY_IOS
        WechatLogin();
#endif
    }

    #endregion

    private string wechatcode = "微信返回code";

    void Start()
    {
         initWeChat();
    }

    void OnGUI()
    {
        GUI.Label(new Rect(300, 300, 200, 40), wechatcode);
        if (GUI.Button(new Rect(300, 450, 200, 40), "登陆微信"))
        {
            LoginWeChat();
        }
    }

    //微信回调
    public void wechatlogincallback(string code)
    {
        wechatcode = code;
    }
}