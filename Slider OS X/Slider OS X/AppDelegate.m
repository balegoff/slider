//
//  AppDelegate.m
//  Slider OS X
//
//  Created by Baptiste Le Goff on 27/06/2014.
//  Copyright (c) 2014 Baptiste Le Goff. All rights reserved.
//

#import "AppDelegate.h"
#import "Midi.h"


@implementation AppDelegate

-(id)init
{
    bluetooth = [[Bluetooth alloc] init];
    midi = [[Midi alloc] init];
    
    // Register the callbacks for connection and for disconnection:
    [bluetooth registerForNewConnection:self action:@selector(handleRemoteConnection)];
    [bluetooth registerForNewData:self action:@selector(handleNewData:)];
    return self;
}

- (void)awakeFromNib
{
    NSImage *menuIcon = [NSImage imageNamed:@"Menu Icon"];
    NSImage *highlightIcon = [NSImage imageNamed:@"Menu Icon"]; // Yes, we're using the exact same image asset.
    [highlightIcon setTemplate:YES]; // Allows the correct highlighting of the icon when the menu is clicked.
    
    _statusItem = [[NSStatusBar systemStatusBar] statusItemWithLength:NSVariableStatusItemLength];
    [[self statusItem] setImage:menuIcon];
    [[self statusItem] setAlternateImage:highlightIcon];
    [[self statusItem] setMenu:[self menu]];
    [[self statusItem] setHighlightMode:YES];
    
    menuItmeConnectionStatus = [[_statusItem menu] itemWithTag:1];
}

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
    [bluetooth advertise];
    menuItmeConnectionStatus.title = @"Waiting for Android";
}


/* ##################################
 *          UI METHODS
 ##################################*/

// UI callback when the user clicks on "Preferences"
- (IBAction)preferences:(id)sender
{
    
    [NSApp activateIgnoringOtherApps:YES];

    prefPane = [[NSWindowController alloc] initWithWindowNibName:@"PrefPane"];
    [[prefPane window] makeKeyAndOrderFront:nil];
    [prefPane showWindow:nil];
}

- (IBAction)closePreferences:(id)sender{
   [NSApp activateIgnoringOtherApps:NO];
}


/* ##################################
 *          CALLBACK METHODS
 ##################################*/

// Method called when a connection has been established
- (void)handleRemoteConnection
{
    menuItmeConnectionStatus.title = [bluetooth getDeviceName];
}

// Method called when new data is incoming on the channel
- (void)handleNewData:(NSData*)dataObject
{
    const char* bytes = [dataObject bytes];
    int cc = *(int*)(bytes);
    int value = *(int*)(bytes+1);
    
    [midi sendMidi:cc withNote:value];
}

@end
