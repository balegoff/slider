//
//  Bluetooth.h
//  Slider OS X
//
//  Created by Baptiste Le Goff on 27/06/2014.
//  Copyright (c) 2014 Baptiste Le Goff. All rights reserved.
//

#import <IOBluetooth/IOBluetooth.h>

@interface Bluetooth : NSObject{
    IOBluetoothRFCOMMChannel *mRFCOMMChannel;
    IOBluetoothUserNotification *mIncomingChannelNotification;
    BluetoothRFCOMMChannelID mServerChannelID;
    
    // This is the method to call when the RFCOMM channel appears
    SEL mHandleRemoteConnectionSelector;
    id	mConnectionTarget;
    
    // This is the method to call when the RFCOMM channel disappears
    SEL mHandleEndOfConnectionSelector;
    id	mEndofConnectionTarget;
    
    // This is the method to call in the UI when new data shows up:
    SEL	mHandleNewDataSelector;
    id	mNewDataTarget;
}

- (id) init;

- (BOOL) advertise;
- (void) stopAdvertising;
- (void) connected: (IOBluetoothUserNotification *)inNotification channel:(IOBluetoothRFCOMMChannel *)newChannel;

- (void)registerForNewConnection:(id)myTarget action:(SEL)actionMethod;
- (void)registerForEndOfConnection:(id)myTarget action:(SEL)actionMethod;
- (void)registerForNewData:(id)myTarget action:(SEL)actionMethod;

- (NSString*)getDeviceName;

// Implementation of delegate calls (see IOBluetoothRFCOMMChannel.h) Only the basic ones:
- (void)rfcommChannelOpenComplete:(IOBluetoothRFCOMMChannel*)rfcommChannel status:(IOReturn)error;
- (void)rfcommChannelData:(IOBluetoothRFCOMMChannel*)rfcommChannel data:(void *)dataPointer length:(size_t)dataLength;
- (void)rfcommChannelClosed:(IOBluetoothRFCOMMChannel*)rfcommChannel;



@end