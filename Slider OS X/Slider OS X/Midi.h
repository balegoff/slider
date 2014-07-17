//
//  Midi.h
//  Slider OS X
//
//  Created by Baptiste Le Goff on 16/07/2014.
//  Copyright (c) 2014 Baptiste Le Goff. All rights reserved.
//

#import <CoreMIDI/CoreMIDI.h>

@interface Midi : NSObject{
    MIDIClientRef           client;
    MIDIPortRef             outputPort;
    MIDIEndpointRef         midiOut;
}

-(id)init;
-(void)listEndpoints;
-(void)sendMidi:(int)control withNote:(int)note;

@end
