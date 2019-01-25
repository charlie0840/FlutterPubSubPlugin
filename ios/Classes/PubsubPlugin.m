#import "PubsubPlugin.h"
#import <pubsub/pubsub-Swift.h>

@implementation PubsubPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftPubsubPlugin registerWithRegistrar:registrar];
}
@end
