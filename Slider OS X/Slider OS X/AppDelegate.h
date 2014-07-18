//
//  AppDelegate.h
//  Slider OS X
//
//  Created by Baptiste Le Goff on 27/06/2014.
//  Copyright (c) 2014 Baptiste Le Goff. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import "Bluetooth.h"
#import "Midi.h"

@interface AppDelegate : NSObject <NSApplicationDelegate>{
    Bluetooth *bluetooth;
    Midi *midi;
    
    //UI elements
    NSMenuItem *menuItmeConnectionStatus;
    NSWindowController *prefPane;
}

@property (readwrite, retain) IBOutlet NSMenu *menu;
@property (readwrite, retain) IBOutlet NSStatusItem *statusItem;

-(id)init;
- (void)awakeFromNib;
- (void)applicationDidFinishLaunching:(NSNotification *)aNotification;

- (void)handleRemoteConnection;
- (void)handleEndOfConnection;
- (void)handleNewData:(NSData*)dataObject;

- (IBAction)preferences:(id)sender;


@end
