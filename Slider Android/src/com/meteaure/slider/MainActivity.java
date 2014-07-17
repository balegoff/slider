package com.meteaure.slider;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	
	protected static Bluetooth bluetooth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		LinearLayout wrapper = (LinearLayout) findViewById(R.id.linearLayout);

		// Managing the bluetooth connection
		bluetooth = new Bluetooth(this);
		bluetooth.enable();
		bluetooth.getPairedDevices();
		
		// Creating the connection buttons
		Button connectButton = (Button) findViewById(R.id.connectButton);
		
		// Creating the sliders
		Slider slider1 = new Slider(this, 1);
		wrapper.addView(slider1);
		
		Slider slider2 = new Slider(this, 2);
		wrapper.addView(slider2);
		
		Slider slider3 = new Slider(this, 3);
		wrapper.addView(slider3);
		
		Slider slider4 = new Slider(this, 4);
		wrapper.addView(slider4);
		
		// Listeners
		connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	bluetooth.connect();
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
		super.onDestroy();
		unregisterReceiver(bluetooth.getReceiver());
		bluetooth.disconnect();
	}
}
