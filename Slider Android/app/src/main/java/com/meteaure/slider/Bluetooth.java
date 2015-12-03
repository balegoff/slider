package com.meteaure.slider;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
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
import android.widget.Toast;

public class Bluetooth {
	
	public static final int REQUEST_ENABLE_BT = 1;
	
	private BluetoothAdapter mBluetoothAdapter;
	private ConnectionThread mConnectionThread;
	private SendingThread mSendingThread;
	private Context mContext;
	private boolean mIsConnected;
	
	// BroadcastReceiver managing bluetooth connection events
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        //Button connectButton = (Button) ((Activity)mContext).findViewById(R.id.connectButton);
	        
	        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
	            mIsConnected = true;
	            Toast.makeText(mContext, "Connected", Toast.LENGTH_SHORT).show();
	        }
	        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
	            mIsConnected = false;
	            mSendingThread = null;
				if(mConnectionThread != null) {
					mConnectionThread.cancel();
					mConnectionThread = null;
				}
	        }
	    }
	};
	
	/**
	 * Constructor
	 * Only initializes the Android Bluetooth Adapter 
	 */
	public Bluetooth(Context context){
		mIsConnected = false;
		mContext = context;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		mContext.registerReceiver(mReceiver, filter1);
		mContext.registerReceiver(mReceiver, filter2);
	}
	
	
	/**
	 * Enables bluetooth on phone
	 * If bluetooth isn't enabled, a pop-up will appear to ask the user to activate it
	 */
	public void enable(){
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			((Activity)mContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		// Waiting the system to turn on bluetooth
		while(!mBluetoothAdapter.isEnabled()){
			try{
				Thread.sleep(500);
			} catch(InterruptedException e){
				Log.v(MainActivity.TAG, e.getMessage());
			}
		}
	}
	
	
	/**
	 * Listing the devices that the user has already paired before
	 * Getting a reference to the Mac
	 * @return The list of Paired Devices names
	 */
	public List<String> getPairedDevices(){
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		List<String> knownDevices = new ArrayList<String>();

		for (BluetoothDevice device : pairedDevices) {
			knownDevices.add(device.getName());
		}

		return knownDevices;
	}
	
	/**
	 * Connecting to the Mac bluetooth socket
	 * @param index The index of the target bluetooth device in th system devices list
	 */
	public boolean connectDeviceAtIndex(int index){
		if(!mIsConnected && index < mBluetoothAdapter.getBondedDevices().size()){
			List<BluetoothDevice> nameList = new ArrayList<BluetoothDevice>(mBluetoothAdapter.getBondedDevices());
			BluetoothDevice device = nameList.get(index);
    		mConnectionThread = new ConnectionThread(device);
    		mConnectionThread.start();
    	}
		return mIsConnected;
	}
	
	public void disconnect(){
		mSendingThread = null;
		mConnectionThread.cancel();
		mConnectionThread = null;
	}
	
	/**
	 * Sending MIDI messages to the Mac
	 * @param control The value of the continuous controller that we want to send MIDI to
	 * @param value The value of the MIDI signal (from 0 to 127)
	 */
	public void sendMidiSignal(int control, int value){
		if(mIsConnected){
    		if(mSendingThread == null){
    			mSendingThread = new SendingThread(mConnectionThread.getSocket());
    			mSendingThread.start();
    		}
    		
    		else {
    			mSendingThread.send(control, value);
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

	        Log.v(MainActivity.TAG, device.getName());
	        
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) {Log.v(MainActivity.TAG, "UUID STEP ERROR");}
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
	        	Log.v(MainActivity.TAG, "ERROR : " + connectException.toString());

	            try {
	                mmSocket.close();
	            } catch (IOException e) {
					Log.v(MainActivity.TAG, e.getMessage());
				}
	        }
	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
				if(mmSocket.isConnected())
	           		mmSocket.close();
	        } catch (IOException e) {
				Log.v(MainActivity.TAG, e.getMessage());
			}
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
	        } catch (IOException e) {
				Log.v(MainActivity.TAG, e.getMessage());
			}
	 
	        mmOutStream = tmpOut;
	    }
	    
	    public void send(int control, int note){
	    	byte[] buffer = new byte[2];  // buffer store for the stream
	        
	        buffer[0] = (byte) control;
	        buffer[1] = (byte) note;
	        
	        try{
	        	mmOutStream.write(buffer);
	        }
	        catch(IOException e){
				Log.v(MainActivity.TAG, e.getMessage());
			}
	    }
	}

}
