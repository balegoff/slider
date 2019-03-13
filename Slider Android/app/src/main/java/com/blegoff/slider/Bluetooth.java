package com.blegoff.slider;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Bluetooth {
	
	public static final int REQUEST_ENABLE_BT = 1;
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice macbook;
	private ConnectionThread connectionTh;
	private SendingThread sendingThread;
	private Activity activity;
	private boolean connected;
	
	// BroadcastReceiver managing bluetooth connection events
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        Button connectButton = (Button) activity.findViewById(R.id.connectButton);
	        
	        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
	            connected = true;
	            connectButton.setVisibility(View.GONE);
	            Toast.makeText(activity.getApplicationContext(), "BT Connected", Toast.LENGTH_SHORT).show();
	        }
	        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
	            connected = false;
	            sendingThread = null;
	    		connectionTh.cancel();
	    		connectionTh = null;
	    		connectButton.setVisibility(View.VISIBLE);
	            Toast.makeText(activity.getApplicationContext(), "BT Disconnected", Toast.LENGTH_SHORT).show();
	        }
	    }
	};
	
	/**
	 * Constructor
	 * Only initializes the Android Bluetooth Adapter 
	 */
	public Bluetooth(Activity a){
		connected = false;
		activity = a;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        activity.registerReceiver(mReceiver, filter1);
        activity.registerReceiver(mReceiver, filter2);
	}
	
	
	/**
	 * Enables bluetooth on phone
	 * If bluetooth isn't enabled, a pop-up will appear to ask the user to activate it
	 * @param activity The current activity, needed to create the pop-up
	 */
	public void enable(){
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		// Waiting the system to turn on bluetooth
		while(!mBluetoothAdapter.isEnabled()){
			try{ Thread.sleep(500); } catch(InterruptedException e){}
		}
	}
	
	
	/**
	 * Listing the devices that the user has already paired before
	 * Getting a reference to the Mac
	 * @return The list of Paired Devices
	 */
	public String[] getPairedDevices(){
		String[] knownDevices = new String[16];
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			int i = 0;
			// Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		    	if (i == 0)
		    		macbook = device;
		        knownDevices[i] = device.getName() + ": " + device.getAddress();
		        Log.v("DEVICES", "Device " + i +  " : " + knownDevices[i]);
		        i++;
		    }
		}

		return knownDevices;
	}
	
	/**
	 * Connecting to the Mac bluetooth socket
	 */
	public void connect(){
		if(!connected){
    		connectionTh = new ConnectionThread(macbook);
    		connectionTh.start();
    	}
	}
	
	public void disconnect(){
		sendingThread = null;
		connectionTh.cancel();
		connectionTh = null;
	}
	
	/**
	 * Sending MIDI messages to the Mac
	 * @param control The value of the continuous controller that we want to send MIDI to
	 * @param value The value of the MIDI signal (from 0 to 127)
	 */
	public void sendMidiSignal(int control, int value){
		if(connected){
    		if(sendingThread == null){
    			sendingThread = new SendingThread(connectionTh.getSocket());
    			sendingThread.start();
    		}
    		
    		else {
    			sendingThread.send(control, value);
    		}
    	}
	}
	
	public BroadcastReceiver getReceiver(){
		return mReceiver;
	}
	
	//////////////////////////////////////////////////////////////////
	///////////////////////////PRIVATE CLASSES////////////////////////
	//////////////////////////////////////////////////////////////////
	
	
	/**
	 * Private class ConnectionThread
	 * Thread handling the bluetooth connection with the Mac
	 */
	private class ConnectionThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	    // The UUID must match the one on the server side
	    private final UUID MY_UUID = UUID.fromString("513FD11D-55DA-425C-8AA8-99009230B6E9");
	    
	    public ConnectionThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket, because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;

	        Log.v("DEVICE CONNECT", device.getName());
	        
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) {Log.v("DEVICE CONNECT", "UUID STEP ERROR");}
	        mmSocket = tmp;
	    }
	    
	    public BluetoothSocket getSocket(){
	    	return mmSocket;
	    }
	 
		public void run() {
	        // Cancel discovery because it will slow down the connection
	        mBluetoothAdapter.cancelDiscovery();
	        try {
	            // Connect the device through the socket. This will block until it succeeds or throws an exception
	            mmSocket.connect();
	            
	        } catch (IOException connectException) {
	            // Unable to connect
	        	Log.v("DEVICE CONNECT", "ERROR : " + connectException.toString());

	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	        }
	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	           mmSocket.close();
	        } catch (IOException e) {}
	    }
	}
	
	
	/**
	 * Private class SendingThread
	 * Thread sending messages to the mac
	 */
	private class SendingThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final OutputStream mmOutStream;
	 
	    public SendingThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        OutputStream tmpOut = null;
	 
	        // Get the output stream, using temp object because OutputStream is final
	        try {
	            tmpOut = mmSocket.getOutputStream();
	        } catch (IOException e) { }
	 
	        mmOutStream = tmpOut;
	    }
	    
	    public void send(int control, int note){
	    	byte[] buffer = new byte[2];  // buffer store for the stream
	        
	        buffer[0] = (byte) control;
	        buffer[1] = (byte) note;
	        
	        try{
	        	mmOutStream.write(buffer);
	        }
	        catch(IOException e){}
	    }
	}

}
