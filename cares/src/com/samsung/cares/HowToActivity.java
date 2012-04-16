package com.samsung.cares;

import java.lang.reflect.Method;
import java.util.Calendar;

import com.samsung.cares.common.Status;
import com.samsung.cares.common.XMLData;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;

public class HowToActivity extends PageActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Status.DEVICE = isTabletDevice() ? Status.DEVICE_TABLET : Status.DEVICE_PHONE;
        Status.PHONE = getMyPhoneNumber();
        
        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) { //HONEYCOMB
        if(Status.DEVICE == Status.DEVICE_TABLET) {
        	logoView.setBackgroundDrawable(getResources().getDrawable(R.drawable.logo_bg_1280));
		}
        
        if(!CHANNELID.equals("")) {
	        categorySpinner.setAdapter(spinnerAdapter);	        
	        //listView.addFooterView(footerView, null, false);
	        listView.setOnScrollListener(this);
	        
	        setHowToCategory();
        }
        
		listView.setAdapter(pageAdapter);
        
        if(CATEGORYID.equals("")) {
        	addItems();
        }
    }
	
	protected void viewDetail(XMLData xmlData) {
		
		Intent intent = new Intent(this, HowToDetailActivity.class);
		intent.putExtra("xmlData", xmlData);
        startActivity(intent);
	}
	
	protected void viewPage(XMLData xmlData) {
		
		Intent intent = new Intent(this, HowToActivity.class);
		intent.putExtra("xmlData", xmlData);
        startActivity(intent);
	}
	
	private boolean isTabletDevice() {
    	if(Build.VERSION.SDK_INT >= 11) { // honeycomb
    		//test screen size, use reflection because isLayoutSizeAtLeast is
    		//only available since 11
    	    Configuration con = getResources().getConfiguration();
    	    try {
    	    	Method mIsLayoutSizeAtLeast = con.getClass().getMethod(
    	    			"isLayoutSizeAtLeast", int.class);
    	    	boolean r = (Boolean) mIsLayoutSizeAtLeast.invoke(con, 0x00000004); // Configuration.SCREENLAYOUT_SIZE_XLARGE
    	    	return r;
    	    }
    	    catch (Exception e) {
    	    	e.printStackTrace();
    	    	return false;
    	    }
    	}
    	
    	return false;
    }
	
	private String getMyPhoneNumber() {
        TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
        return mTelephonyMgr.getLine1Number();
    }
}