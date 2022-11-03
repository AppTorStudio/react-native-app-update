#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(AppUpdate, NSObject)

RCT_EXTERN_METHOD(getAppUpdateInfo:(NSString *)name
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(openAppStore:(NSString *)name
                  withResolver:(RCTPromiseResolveBlock)resolve
                  withRejecter:(RCTPromiseRejectBlock)reject)

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
