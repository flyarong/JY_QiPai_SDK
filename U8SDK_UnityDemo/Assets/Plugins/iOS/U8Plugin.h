//
//  U8SDK.h
//  U8SDK
//
//  Created by dayong on 15-1-21.
//  Copyright (c) 2015年 u8sdk. All rights reserved.
//

#import "U8SDK.h"

@interface U8Plugin : NSObject<U8PluginProtocol>

-(UIView*) view;
-(UIViewController*) viewController;

-(void) eventPlatformInit:(NSDictionary*) params;
-(void) eventUserLogin:(NSDictionary*) params;
-(void) eventUserLogout:(NSDictionary*) params;
-(void) eventPayPaid:(NSDictionary*) params;

@end
