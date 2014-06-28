//
//  AppDelegate.m
//  Slider OS X
//
//  Created by Baptiste Le Goff on 27/06/2014.
//  Copyright (c) 2014 Baptiste Le Goff. All rights reserved.
//

#import "AppDelegate.h"

@implementation AppDelegate


-(id)init
{
    bluetooth = [[Bluetooth alloc] init];
    return self;
}

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
    [bluetooth advertise];
}

@end
