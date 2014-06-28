//
//  AppDelegate.h
//  Slider OS X
//
//  Created by Baptiste Le Goff on 27/06/2014.
//  Copyright (c) 2014 Baptiste Le Goff. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import "Bluetooth.h"

@interface AppDelegate : NSObject <NSApplicationDelegate>{
    Bluetooth *bluetooth;
}

-(id)init;

@property (assign) IBOutlet NSWindow *window;

@end
