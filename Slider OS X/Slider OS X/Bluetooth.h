//
//  Bluetooth.h
//  Slider OS X
//
//  Created by Baptiste Le Goff on 27/06/2014.
//  Copyright (c) 2014 Baptiste Le Goff. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <IOBluetooth/IOBluetooth.h>
#import "Midi.h"


@interface Bluetooth : NSObject{
    IOBluetoothRFCOMMChannel *mRFCOMMChannel;
    IOBluetoothUserNotification *mIncomingChannelNotification;
    BluetoothRFCOMMChannelID mServerChannelID;
    Midi *midiManager;
}

- (id) init;

- (BOOL) advertise;
- (void) stopAdvertising;
- (void) connected: (IOBluetoothUserNotification *)inNotification channel:(IOBluetoothRFCOMMChannel *)newChannel;


// Implementation of delegate calls (see IOBluetoothRFCOMMChannel.h) Only the basic ones:
- (void)rfcommChannelOpenComplete:(IOBluetoothRFCOMMChannel*)rfcommChannel status:(IOReturn)error;
- (void)rfcommChannelData:(IOBluetoothRFCOMMChannel*)rfcommChannel data:(void *)dataPointer length:(size_t)dataLength;
- (void)rfcommChannelClosed:(IOBluetoothRFCOMMChannel*)rfcommChannel;



@end