//
//  Bluetooth.m
//  Slider OS X
//
//  Created by Baptiste Le Goff on 27/06/2014.
//  Copyright (c) 2014 Baptiste Le Goff. All rights reserved.
//

#import "Bluetooth.h"

@implementation Bluetooth

- (id) init{
    return self;
}

// Connection method
// returns TRUE if the connection has been established
- (BOOL)advertise{
    
    BOOL				returnValue = FALSE;
    NSString			*dictionaryPath = nil;
	NSString			*serviceName = nil;
    NSMutableDictionary	*sdpEntries = nil;
    
    NSLog(@"Starting advertising");
    
    // Builds a string with the service name we wish to offer
    serviceName = @"Slider MIDI Controller";
    // Get the path for the dictonary we wish to publish.
    dictionaryPath = [[NSBundle mainBundle] pathForResource:@"SerialPortDictionary" ofType:@"plist"];
    
    if ( (dictionaryPath != nil) && (serviceName != nil) )
    {
        // Loads the dictionary from the path:
        sdpEntries = [NSMutableDictionary dictionaryWithContentsOfFile:dictionaryPath];
        
        if (sdpEntries != nil)
        {
            
            [sdpEntries setObject:serviceName forKey:@"0100 - ServiceName*"];
            
            // Add SDP dictionary, the rfcomm channel assigned to this service comes back in mServerChannelID.
            serviceRecord = [IOBluetoothSDPServiceRecord publishedServiceRecordWithDictionary:sdpEntries];
            [serviceRecord getRFCOMMChannelID:&mServerChannelID];
            
            // Register a notification so we get notified when an incoming RFCOMM channel is opened to the channel assigned to our service.
            mIncomingChannelNotification = [IOBluetoothRFCOMMChannel registerForChannelOpenNotifications:self
                                                            selector:@selector(connected:channel:)
                                                            withChannelID:mServerChannelID
                                                            direction:kIOBluetoothUserNotificationChannelDirectionIncoming];
            
            returnValue = TRUE;
        }
    }
    return returnValue;
}


// Called when a connection has been established with a device
- (void)connected: (IOBluetoothUserNotification *)inNotification channel:(IOBluetoothRFCOMMChannel *)newChannel{
    
    // Make sure the channel is an incoming channel on the right channel ID.
    if ( (newChannel != nil) && [newChannel isIncoming] && ([newChannel getChannelID] == mServerChannelID) ){
        
		mRFCOMMChannel = newChannel;
        
		// Set self as the channel's delegate: THIS IS THE VERY FIRST THING TO DO FOR A SERVER !!!!
		if ( [mRFCOMMChannel setDelegate:self] == kIOReturnSuccess ){
			[self stopAdvertising];
            
        }
        
        // The setDelgate: call failed. This is catastrophic for a server
		else
			mRFCOMMChannel = nil;
    }
}


// Stops providing services
- (void)stopAdvertising{
    NSLog(@"Stopping advertising");
    
    //Remove the service
    [serviceRecord removeServiceRecord];
    
    // Unregisters the notification:
    if ( mIncomingChannelNotification != nil )
    {
        [mIncomingChannelNotification unregister];
		mIncomingChannelNotification = nil;
	}
	mServerChannelID = 0;
}

-(void) disconnect{
    [mRFCOMMChannel closeChannel];
    mRFCOMMChannel = nil;
}

// Registers selector for a successful completed connection:
// tells to this class to call myTarget and myTargetAction when the channel obtains a connection:
- (void)registerForNewConnection:(id)myTarget action:(SEL)actionMethod
{
	mHandleRemoteConnectionSelector = actionMethod;
	mConnectionTarget = myTarget;
}

- (void)registerForEndOfConnection:(id)myTarget action:(SEL)actionMethod{
    mHandleEndOfConnectionSelector = actionMethod;
	mEndofConnectionTarget = myTarget;
}

// Registers selector for incoming data:
// tells to this class to call myTarget and myTargetAction when new data shows up:
- (void)registerForNewData:(id)myTarget action:(SEL)actionMethod
{
	mHandleNewDataSelector = actionMethod;
	mNewDataTarget = myTarget;
}

// Returns device name
- (NSString*)getDeviceName{
    return [[mRFCOMMChannel getDevice] name];
}


/*####################################
 *    Delegate calls implementation
 ####################################*/

- (void)rfcommChannelOpenComplete:(IOBluetoothRFCOMMChannel*)rfcommChannel status:(IOReturn)error;
{
    if (error != kIOReturnSuccess) {
        NSLog(@"Failed to open channel, error %d", error);
        return;
    }
    else
        [mConnectionTarget performSelector:mHandleRemoteConnectionSelector];
}

- (void)rfcommChannelData:(IOBluetoothRFCOMMChannel*)rfcommChannel data:(void *)dataPointer length:(size_t)dataLength;
{
	[mNewDataTarget performSelector:mHandleNewDataSelector withObject:[NSData dataWithBytes:dataPointer length:dataLength]];
}

- (void)rfcommChannelClosed:(IOBluetoothRFCOMMChannel*)rfcommChannel;
{
    [mEndofConnectionTarget performSelector:mHandleEndOfConnectionSelector];
}


@end
