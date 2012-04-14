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
        	if(SUBTYPE.equals("CONTENT")) {
            	contactLayout.setVisibility(View.VISIBLE);
            	
            	ImageView facebookView = (ImageView)findViewById(R.id.facebook);
        		ImageView twitterView = (ImageView)findViewById(R.id.twitter);
        		ImageView chatView = (ImageView)findViewById(R.id.chat);
        		ImageView callView = (ImageView)findViewById(R.id.call);
        		
        		facebookView.setOnClickListener(new View.OnClickListener() {
        	        public void onClick(View v) {
        	        	if(FACEBOOK_URL != null && !FACEBOOK_URL.equals("")) {
        	        		//insert log
            	        	openBrowser(FACEBOOK_URL);
        	        	}
        	        }
        	    });
        		
        		twitterView.setOnClickListener(new View.OnClickListener() {
        	        public void onClick(View v) {
        	        	if(TWITTER_URL != null && !TWITTER_URL.equals("")) {
        	        		//insert log
            	        	openBrowser(TWITTER_URL);
        	        	}
        	        }
        	    });
        		
        		chatView.setOnClickListener(new View.OnClickListener() {
        	        public void onClick(View v) {
        	        	if(CHAT_URL != null && !CHAT_URL.equals("")) {
        	        		//insert log
            	        	openBrowser(CHAT_URL);
        	        	}
        	        }
        	    });
        		
        		callView.setOnClickListener(new View.OnClickListener() {
        	        public void onClick(View v) {
        	        	if(CALL_NUMBER != null && !CALL_NUMBER.equals("")) {
        	        		if(CALL_YN != null && CALL_YN.equals("Y")) {
		        	        	//insert log
		        	        	call(CALL_NUMBER);
        	        		}
        	        		else {
        	        			//show intent
        	        		}
        	        	}
        	        }
        	    });
            }
        //}
        
        
    }
	
	protected void viewPage(XMLData xmlData) {
		if(!xmlData.contentId.equals("") && !xmlData.contentURL.equals("")) {
			Intent intent = new Intent(this, ContentDetailActivity.class);
			intent.putExtra("xmlData", xmlData);
			startActivity(intent);
		}
		else {
			Intent intent = new Intent(this, ContactActivity.class);
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