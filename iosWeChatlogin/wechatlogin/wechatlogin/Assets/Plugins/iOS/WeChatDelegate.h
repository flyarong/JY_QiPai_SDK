
#ifndef WeChatDelegate_h
#define WeChatDelegate_h
#import "WXApiManager.h"

@interface WeChatDelegate : NSObject<WXApiManagerDelegate>
+ (instancetype)sharedWeChatTool;
-(void)initApp:(NSString*) app_id;
-(void)login;
@end
#endif
