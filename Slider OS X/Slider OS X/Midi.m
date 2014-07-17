//
//  Midi.m
//  Slider OS X
//
//  Created by Baptiste Le Goff on 16/07/2014.
//  Copyright (c) 2014 Baptiste Le Goff. All rights reserved.
//

#import "Midi.h"

@implementation Midi

-(id) init{
    //Create the MIDI client, MIDI output port and MIDI endpoint.
    MIDIClientCreate((CFStringRef)@"Midi client", NULL, NULL, &client);
    MIDIOutputPortCreate(client, (CFStringRef)@"Output port", &outputPort);
    midiOut = MIDIGetDestination(0);
    
    return self;
}

//Enumerate through the avalaible MIDI destinations
-(void) listEndpoints{
    
    for (ItemCount index = 0; index < MIDIGetNumberOfDestinations(); index++) {
        MIDIEndpointRef outputEndpoint = MIDIGetDestination(index);

        //Getting the names of the destinations
        CFStringRef endpointName = NULL;
        MIDIObjectGetStringProperty(outputEndpoint, kMIDIPropertyName, &endpointName);
        char endpointNameC[255];
        CFStringGetCString(endpointName, endpointNameC, 255, kCFStringEncodingUTF8);
        NSLog(@"The endpoint name at %ld is %s", index, endpointNameC);
    }
}

-(void) sendMidi:(int)control withNote:(int)note{
    //Set up the data to be sent
    const UInt8 controlData[] = { 0xb0, control, note };
    
    //Create a the packets that will be sent to the device.
    Byte packetBuffer[sizeof(MIDIPacketList)];
    MIDIPacketList *packetList = (MIDIPacketList *)packetBuffer;
    ByteCount size = sizeof(controlData);
    
    MIDIPacketListAdd(packetList,
                      sizeof(packetBuffer),
                      MIDIPacketListInit(packetList),
                      0,
                      size,
                      controlData);
    
    MIDISend(outputPort, midiOut, packetList);
}

@end
