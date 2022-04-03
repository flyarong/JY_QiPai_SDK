//
//  U8SDK_Interface.m
//  Unity-iPhone
//
//  Created by dayong on 15/7/30.
//
//

#import <Foundation/Foundation.h>
#import "U8SDK.h"

void U8SDK_Setup()
{
    [[U8SDK sharedInstance] setupWithParams:nil];
}

void U8SDK_Login()
{
    [[U8SDK sharedInstance].defaultUser login];
}

void U8SDK_LoginCustom(const char* customData)
{
    [[U8SDK sharedInstance].defaultUser login];
}

void U8SDK_SwitchLogin()
{
    [[U8SDK sharedInstance].defaultUser switchAccount];
}

bool U8SDK_Logout()
{
    [[U8SDK sharedInstance].defaultUser logout];
    
    return false;
}

bool U8SDK_ShowAccountCenter()
{
    [[U8SDK sharedInstance].defaultUser showAccountCenter];
    
    return false;
}

bool U8SDK_SubmitGameData(const char* extraData)
{
    return false;
}

bool U8SDK_SDKExit()
{
    return false;
}

void U8SDK_Pay (const char* payParams)
{
    NSError* error;
    NSDictionary* json = [NSJSONSerialization JSONObjectWithData:[NSData dataWithBytes:payParams length:strlen(payParams)] options:kNilOptions error:&error];
    
    [[U8SDK sharedInstance].defaultPay pay:json];
}

bool U8SDK_IsSupportExit()
{
    return false;
}

bool U8SDK_IsSupportAccountCenter ()
{
    return [[U8SDK sharedInstance] IsSupportFunction:@selector(showAccountCenter)];
}

bool U8SDK_IsSupportLogout ()
{
    return true;
}
