package com.meteaure.slider;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.Gravity;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;

public class Slider extends SeekBar{
	
	private int cc;
	
	// Constructor
	public Slider(Context context, int c) {
		super(context);
		cc = c;
		this.setMax(127);
		this.makeStyle();
        this.addListeners();
	}
	
	
	// Slider custom style
	public void makeStyle(){
		ShapeDrawable s1 = new ShapeDrawable();
        s1.getPaint().setColor(Color.BLACK);
        ShapeDrawable s2 = new ShapeDrawable();
        s2.getPaint().setColor(Color.MAGENTA); 
        
        ClipDrawable clip = new ClipDrawable(s2, Gravity.LEFT,ClipDrawable.HORIZONTAL);
        
        LayerDrawable colors = new LayerDrawable(new Drawable[]{s1,clip});

        this.setProgressDrawable(colors);
        this.setPadding(30, 60, 30, 0);
        this.setThumb(null);
        
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 300);
        this.setLayoutParams(lp);
	}
	
	
	// Adding events listeners
	public void addListeners(){
		this.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener (){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				MainActivity.bluetooth.sendMidiSignal(cc, progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
		});
	}
	
}
