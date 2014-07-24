package com.meteaure.slider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
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
		Slider slider1 = new Slider(this, 1, 51, 181, 229);
		wrapper.addView(slider1);
		
		Slider slider2 = new Slider(this, 2, 170, 102, 204);
		wrapper.addView(slider2);
		
		Slider slider3 = new Slider(this, 3, 153, 204, 00);
		wrapper.addView(slider3);
		
		Slider slider4 = new Slider(this, 4, 255, 187, 51);
		wrapper.addView(slider4);
		
		Slider slider5 = new Slider(this, 5, 255, 68, 68);
		wrapper.addView(slider5);
		
		Slider slider6 = new Slider(this, 6, 255, 255, 255);
		wrapper.addView(slider6);
		
		Slider slider7 = new Slider(this, 7, 51, 181, 229);
		wrapper.addView(slider7);
		
		Slider slider8 = new Slider(this, 8, 170, 102, 204);
		wrapper.addView(slider8);

		
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
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);
	    if (hasFocus) {
	        this.findViewById(R.id.linearLayout).setSystemUiVisibility(
	                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
	}
}
