package com.samsung.cares.custom;

import com.samsung.cares.common.Status;
import com.samsung.cares.util.Logger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.webkit.WebView;

public class CustomWebView extends WebView {

	public CustomWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void onMeasure(int wspec, int hspec){
	    super.onMeasure(wspec, hspec);
	    this.setMeasuredDimension(this.getMeasuredWidth(), this.getMeasuredHeight() - (int)(Status.DENSITY * 0.34));
	}
	
	/*
	protected int computeVerticalScrollRange() {
        float density = getResources().getDisplayMetrics().density;
        return super.computeVerticalScrollRange() + (int)(60 * density);
   }
   */
}
