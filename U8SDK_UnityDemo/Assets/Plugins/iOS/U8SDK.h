//
//  U8SDK.h
//  U8SDK
//
//  Created by dayong on 15-1-21.
//  Copyright (c) 2015年 u8sdk. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#ifdef __cplusplus
#define U8SDK_EXTERN		extern "C" __attribute__((visibility ("default")))
#else
#define U8SDK_EXTERN	    extern __attribute__((visibility ("default")))
#endif


//U8User 账号登录相关接口
@protocol U8User

- (void) login;
- (void) logout;
- (void) switchAccount;

- (BOOL) hasAccountCenter;
@optional
- (void) showAccountCenter;

//- (BOOL) isLogined;
//- (NSString*) getSessionID;

@end

//U8Pay 应用内购接口
@protocol U8Pay

-(void) pay:(NSDictionary*) profuctInfo;

@optional
-(void) openIAP;

@optional
-(void) closeIAP;

@optional
-(void) finishTransactionId:(NSString*)transactionId;

@end

// U8Plugin 插件接口
@protocol U8PluginProtocol

-(instancetype) initWithParams:(NSDictionary*)params;

@optional

-(void) setupWithParams:(NSDictionary*)params;

// UIApplicationDelegate事件
- (void)didFinishLaunchingWithOptions:(NSDictionary *)launchOptions;
- (void)applicationWillResignActive:(UIApplication *)application;
- (void)applicationDidEnterBackground:(UIApplication *)application;
- (void)applicationWillEnterForeground:(UIApplication *)application;
- (void)applicationDidBecomeActive:(UIApplication *)application;
- (void)applicationWillTerminate:(UIApplication *)application;

- (void)didRegisterForRemoteNotificationsWithDeviceToken:(NSData*)deviceToken;
- (void)didFailToRegisterForRemoteNotificationsWithError:(NSError*)error;
- (void)didReceiveLocalNotification:(UILocalNotification*)notification;
- (void)didReceiveRemoteNotification:(NSDictionary*)userInfo;

- (void)openURL:(NSURL*)url sourceApplication:(NSString*)sourceApplication annotation:(id)annotation;

@end

// U8SDK回调接口
@protocol U8SDKDelegate <NSObject>

-(UIView*) GetView;
-(UIViewController*) GetViewController;

@optional

-(void) OnPlatformInit:(NSNotification*)notification;
-(void) OnUserLogin:(NSNotification*)notification;
-(void) OnUserLogout:(NSNotification*)notification;
-(void) OnPayPaid:(NSNotification*)notification;

@end

// U8SDK的核心类
// 负责插件管理和事件分发
@interface U8SDK : NSObject

@property (nonatomic, copy) NSDictionary* sdkParams;
@property (strong, nonatomic) NSObject<U8User>* defaultUser;
@property (strong, nonatomic) NSObject<U8Pay>* defaultPay;

+(U8SDK*) sharedInstance;


-(void) setDelegate:(id<U8SDKDelegate>)delegate;
-(void) registerPlugin:(NSObject<U8PluginProtocol>*)plugin;

// NSDictionary转换为Http的URL参数
+(NSString*) encodeHttpParams:(NSDictionary*)params encode:(NSStringEncoding)encoding;
// U8官方提供的账号验证方法
-(void) U8AccountValidate:(NSDictionary*)params responseHandler:(void (^)(NSURLResponse* response, id data, NSError* connectionError)) handler;


-(NSArray*) plugins;

-(UIView*) GetView;
-(UIViewController*) GetViewController;

-(void) initWithParams:(NSDictionary*)params;
-(void) setupWithParams:(NSDictionary*)params;

-(BOOL) IsSupportFunction:(SEL)function;

// UIApplicationDelegate事件
- (void)didFinishLaunchingWithOptions:(NSDictionary *)launchOptions;
- (void)applicationWillResignActive:(UIApplication *)application;
- (void)applicationDidEnterBackground:(UIApplication *)application;
- (void)applicationWillEnterForeground:(UIApplication *)application;
- (void)applicationDidBecomeActive:(UIApplication *)application;
- (void)applicationWillTerminate:(UIApplication *)application;

// 推送通知相关事件
- (void)didRegisterForRemoteNotificationsWithDeviceToken:(NSData*)deviceToken;
- (void)didFailToRegisterForRemoteNotificationsWithError:(NSError*)error;
- (void)didReceiveLocalNotification:(UILocalNotification*)notification;
- (void)didReceiveRemoteNotification:(NSDictionary*)userInfo;

// url处理
- (void)openURL:(NSURL*)url sourceApplication:(NSString*)sourceApplication annotation:(id)annotation;

@end

U8SDK_EXTERN NSString* const U8SDKPlatformInit;
U8SDK_EXTERN NSString* const U8SDKUserLogin;
U8SDK_EXTERN NSString* const U8SDKUserLogout;
U8SDK_EXTERN NSString* const U8SDKPayPaid;
