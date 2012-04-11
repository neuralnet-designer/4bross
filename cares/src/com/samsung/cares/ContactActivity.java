package com.samsung.cares;

import java.lang.reflect.Method;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

public class ContactActivity extends PageActivity {
	
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /*
        if(TYPE.equals("channel")) {
	        categorySpinner.setAdapter(spinnerAdapter);	        
	        listView.addFooterView(footerView, null, false);
	        listView.setOnScrollListener(this);
	        
	        setCategory();
        }
        */
        
        listView.addFooterView(footerView, null, false);
        listView.setOnScrollListener(this);
		listView.setAdapter(pageAdapter);
        
        //if(CONTENTID.equals("")) {
        	addItems();
        	if(!SUBTYPE.equals("product")) {
            	contactLayout.setVisibility(View.VISIBLE);
            }
        //}
        
        ImageView facebookView = (ImageView)findViewById(R.id.facebook);
		ImageView twitterView = (ImageView)findViewById(R.id.twitter);
		ImageView chatView = (ImageView)findViewById(R.id.chat);
		ImageView callView = (ImageView)findViewById(R.id.call);
		
		facebookView.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	//insert log
	        	openBrowser("http://www.facebook.com/share.php?u=http%3A%2F%2Fwww.samsung.com%2Fspstv");
	        }
	    });
		
		twitterView.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	//insert log
	        	openBrowser("http://twitter.com/home?status=Watching SAMSUNG SUPPORT PRODUCT TV on http://www.samsung.com/spstv");
	        }
	    });
		
		chatView.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	//insert log
	        	openBrowser("http://www.samsung.com");
	        }
	    });
		
		callView.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	//insert log
	        	call("2012865165");
	        }
	    });
    }
	
	protected void viewPage(XMLData xmlData) {
		if(xmlData.subType.equals("product")) {
		//if(xmlData.productGroupId.equals("") || xmlData.productTypeId.equals("")) {
			Intent intent = new Intent(this, ContactActivity.class);
			intent.putExtra("xmlData", xmlData);
			startActivity(intent);
		}
		else {
			Intent intent = new Intent(this, FAQDetailActivity.class);
			xmlData.type = "faq";
			intent.putExtra("xmlData", xmlData);
			startActivity(intent);
		}
	}
	
	protected void openBrowser(String url) {
    	
    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    	startActivity(intent);
    }
	
	protected void call(String phoneNumber) {
    	
    	Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
    	
    	/*
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
                | Intent.FLAG_ACTIVITY_CLEAR_TOP 
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	intent.putExtra("xmlData", xmlData);
    	
    	Status.SCREEN = Status.SCREEN_ON;
    	
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    	*/
    	startActivity(intent);
    	//overridePendingTransition(R.anim.fade, R.anim.hold);
    }
}