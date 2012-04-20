package com.samsung.cares.custom;

import com.samsung.cares.common.Status;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void onMeasure(int wspec, int hspec){
	    super.onMeasure(wspec, hspec);
	      
	    //Logger.d("density"+Status.DENSITY);
	    //Logger.d("density"+(int)(Status.DENSITY * 0.4375));
	    this.setMeasuredDimension(this.getMeasuredWidth(), this.getMeasuredHeight() - (int)(Status.DENSITY * 0.4375)); // the padding you need. 
	}
	
	/*
	protected int computeVerticalScrollRange() {
        float density = getResources().getDisplayMetrics().density;
        return super.computeVerticalScrollRange() + (int)(60 * density);
   }
   */
}
