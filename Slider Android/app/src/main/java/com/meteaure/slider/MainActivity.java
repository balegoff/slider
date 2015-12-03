package com.meteaure.slider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	protected static Bluetooth bluetooth;
	public static final String TAG = "SLIDER_DEBUG";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		// Managing the bluetooth connection
		bluetooth = new Bluetooth(this);
		bluetooth.enable();

		ArrayAdapter<String> itemsAdapter =  new ArrayAdapter<String>(this, R.layout.layout_devices_list, bluetooth.getPairedDevices());
		ListView listView = (ListView) findViewById(R.id.known_devices_list);
		listView.setAdapter(itemsAdapter);

		if(itemsAdapter.isEmpty()) {
			itemsAdapter.add("No devices available");
		}

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				bluetooth.connectDeviceAtIndex(position);
			}
		});
	}
	
	@Override
	public void onResume(){
		super.onResume();
		bluetooth.enable();
	}
	
	@Override
	public void onDestroy(){
		bluetooth.disconnect();
		unregisterReceiver(bluetooth.getReceiver());
		super.onDestroy();
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);
	    if (hasFocus) {
	        this.findViewById(R.id.main_layout).setSystemUiVisibility(
	                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
	}
}
