//
//  AppDelegate.m
//  Slider OS X
//
//  Created by Baptiste Le Goff on 27/06/2014.
//  Copyright (c) 2014 Baptiste Le Goff. All rights reserved.
//

#import "AppDelegate.h"
#import <CoreMIDI/CoreMIDI.h>

@implementation AppDelegate


-(id)init
{
    bluetooth = [[Bluetooth alloc] init];
    //midi = [[Midi alloc] init];
    return self;
}

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
    [bluetooth advertise];
    [midi listEndpoints];
    for(int i=0; i<127; i++){
        [midi sendMidi:15 withNote:i];
    }

    
}

@end
