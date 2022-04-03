
#import <Foundation/Foundation.h>
#import "WeChatDelegate.h"
#import "AppDelegateListener.h"
#import "Foundation/NSJSONSerialization.h"
#import "UnityAppController.h"

extern "C"
{
    void WechatInit(const char* app_id);
    void WechatLogin();
}

@implementation WeChatDelegate

//生命周期
+(instancetype)sharedWeChatTool
{
    static dispatch_once_t onceToken;
    static WeChatDelegate *instance;
    dispatch_once(&onceToken, ^{ instance = [[WeChatDelegate alloc] init]; });
    return instance;
}

- (void)onOpenURL:(NSNotification*)notification
{
    NSDictionary *userInfo = notification.userInfo;
    NSURL* url = [userInfo valueForKey:@"url"];
    if(url != nil){[WXApi handleOpenURL:url delegate:[WXApiManager sharedManager]];}
}

-(void)initApp:(NSString*) app_id
{
    [WXApi registerApp:app_id];
    [WXApiManager sharedManager].delegate = self;
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kUnityOnOpenURL object:nil];
    [[NSNotificationCenter defaultCenter]    addObserver:self
                                                selector:@selector(onOpenURL:)
                                                    name:kUnityOnOpenURL
                                                  object:nil];
}

-(void)login
{
    SendAuthReq* req = [[SendAuthReq alloc] init];
    req.scope = @"snsapi_userinfo";
    req.state = @"wechatlogin";
    [WXApi sendReq:req];
}

-(void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kUnityOnOpenURL object:nil];
}

//Unity调用，初始化微信
void WechatInit(const char* app_id)
{
    NSString* _id = [NSString stringWithUTF8String:app_id];
    [[WeChatDelegate sharedWeChatTool] initApp:_id];
}

//Unity调用，微信登录
void WechatLogin()
{
    [[WeChatDelegate sharedWeChatTool] login];
}

//登陆回调通知，返回数据给Unity
- (void)managerDidRecvAuthResponse:(SendAuthResp *)response {
    NSString* json = @"{\"code\":\"%@\",\"state\":\"%@\",\"errCode\":\"%d\",\"lang\":\"%@\",\"country\":\"%@\"}";
    json = [NSString stringWithFormat:json,response.code,response.state,response.errCode,response.lang,response.country];
    UnitySendMessage("Main Camera", "wechatlogincallback" ,  [response.code UTF8String] );
    json = nil;
}

@end
