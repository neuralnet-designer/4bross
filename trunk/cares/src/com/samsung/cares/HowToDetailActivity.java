package com.samsung.cares;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.samsung.cares.common.Status;
import com.samsung.cares.common.XMLData;
import com.samsung.cares.util.Logger;
import com.samsung.cares.util.Util;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle; 
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class HowToDetailActivity extends ActivityGroup {
	
	//private DetailAdapter adapter;
	private Activity activity;
	private XMLData xmlData;
    
    protected String CHANNELID = "";
    protected String SCHEDULEID = "";
    
    //private LinearLayout footerButtonView;
    private ImageButton homeButton;
    private ImageButton backButton;
 
    /** Called when the activity is first created. */ 
    @Override 
    public void onCreate(Bundle savedInstanceState) { 
    	//setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
    	
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.howto_detail); 

        xmlData = getIntent().getExtras().getParcelable("xmlData");
        //adapter = new DetailAdapter(this, xmlData);
        activity = this;
        
        CHANNELID = xmlData.channelId;
        SCHEDULEID = xmlData.scheduleId;
		
		//setTitle(xmlData.title);
        
        TextView channelTitleView = (TextView)findViewById(R.id.channel_title);
        //channelTitleView.setText(xmlData.channelGroup + " / " + xmlData.channelTitle);
        channelTitleView.setText(xmlData.channelTitle);
		
		TextView titleView = (TextView)findViewById(R.id.title);
        titleView.setText(xmlData.title);
        
        TextView descriptionView = (TextView)findViewById(R.id.description);
        descriptionView.setText(xmlData.description);
        
        TextView scheduleDateView = (TextView)findViewById(R.id.schedule_date);
        scheduleDateView.setText(xmlData.scheduleDate);
        
        ImageButton yesButton = (ImageButton)findViewById(R.id.yes_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	setPoll(1);
	        }
	    });
		
		ImageButton noButton = (ImageButton)findViewById(R.id.no_button);
		noButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	setPoll(0);
	        }
	    });
        
		setScreen();
		
		Status.NETWORK = Util.checkNetworkStatus(this);
		
		//footerButtonView = (LinearLayout)findViewById(R.id.footer_button);
        
        homeButton = (ImageButton)findViewById(R.id.home_button);
        backButton = (ImageButton)findViewById(R.id.back_button);
        
        homeButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	viewMain();
	        }
	    });
        
        backButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	finish();
	        }
	    });
    }
    
    @Override
	public void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
    
    private void viewMain() {

    	Intent intent = new Intent(this, MainActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    }
    
    private void viewVideo(XMLData xmlData) {
    	
    	Intent intent = new Intent(this, PlayerActivity.class);
    	
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
                | Intent.FLAG_ACTIVITY_CLEAR_TOP 
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	intent.putExtra("xmlData", xmlData);
    	
    	Status.SCREEN = Status.SCREEN_ON;
    	
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    	startActivity(intent);
    	overridePendingTransition(R.anim.fade, R.anim.hold);
    	
    	setContentLog(xmlData.type, xmlData.productId, xmlData.scheduleId, xmlData.orgType, xmlData.orgContentId);
    }
    
    private void setPoll(int isHelpful) {
    	
    	try {
        	
        	String XMLURL = "http://www.samsungsupport.com/feed/rss/cares.jsp?type=POLL&siteCode=" + Status.SITECODE + "&userId=" + Status.USERID + "&channelId=" + CHANNELID + "&scheduleId=" + SCHEDULEID + "&isHelpful=" + isHelpful;
            
        	URL url = new URL(XMLURL);
        	//Logger.d(XMLURL);
        	//Logger.d("CHANNELID:"+CHANNELID);
        	//Logger.d("SCHEDULEID:"+SCHEDULEID);
            
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
            factory.setNamespaceAware(true); 
            XmlPullParser xpp = factory.newPullParser(); 
            
            InputStream in = url.openStream();
            xpp.setInput(in, "utf-8");
         
            int eventType = xpp.getEventType();
            String tag;
            
            while(eventType != XmlPullParser.END_DOCUMENT) { 
            	if(eventType == XmlPullParser.START_DOCUMENT) {
            	}
            	else if(eventType == XmlPullParser.END_DOCUMENT) { 
            	}
            	else if(eventType == XmlPullParser.START_TAG) {
            		
            		tag = xpp.getName();                  
                  
            		if(tag.equals("channel")) {
            			String strStatus = xpp.getAttributeValue(0);
            			String strMessage = xpp.getAttributeValue(1);
            			
            			showResultDialog("Poll", strMessage);
            		}  
            	}
            	
            	eventType = xpp.next(); 
            }
        }
        catch(Exception e) {
        	Logger.d("Exception");
        	showAlertDialog("Network");
        	e.printStackTrace();
        }
    }
    
    protected void setContentLog(final String contentType, final String productId, final String contentId, final String orgContentType, final String orgContentId) {
		
		Status.NETWORK = Util.checkNetworkStatus(this);
  		
  		if(Status.NETWORK != Status.NETWORK_NONE) {
		
			Runnable run = new Runnable() {
				@Override
				public void run() {
			        
			        try {
			        	
			        	String XMLURL = "http://www.samsungsupport.com/feed/rss/cares.jsp?type=CONTENT_LOG&siteCode=" + Status.SITECODE + "&userId=" + Status.USERID + "&contentType=" + contentType + "&productId=" + productId + "&contentId=" + contentId + "&orgContentType=" + orgContentType + "&orgContentId=" + orgContentId;
			        	Logger.d(XMLURL);
	
			        	URL url = new URL(XMLURL);
			        	
			        	XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
				        factory.setNamespaceAware(true); 
				        XmlPullParser xpp = factory.newPullParser(); 
				            
			            InputStream in = url.openStream();
			            xpp.setInput(in, "utf-8");
				         
			            int eventType = xpp.getEventType();
			            String tag;
			            
			            while(eventType != XmlPullParser.END_DOCUMENT) { 
			            	if(eventType == XmlPullParser.START_DOCUMENT) {
			            	}
			            	else if(eventType == XmlPullParser.END_DOCUMENT) { 
			            	}
			            	else if(eventType == XmlPullParser.START_TAG) {
			            		
			            		tag = xpp.getName();                  
			                  
			            		if(tag.equals("channel")) {
			            			String strStatus = xpp.getAttributeValue(0);
			            			String strMessage = xpp.getAttributeValue(1);
			            		}  
			            	}
			            	
			            	eventType = xpp.next(); 
			            }
			        }
			        catch(Exception e) {
			        	Logger.d("Player - Exception");
			        	//showAlertDialog("Network");
			        	//e.printStackTrace();
			        }
				}
			};
			
			Handler handler = new Handler();
			handler.postDelayed(run, 1000);
  		}
	}

    private Bitmap getRemoteImage(String imageURL) {
    	Bitmap bitmap = null;
    	try {
    		URL url = new URL(imageURL);
    		URLConnection conn = url.openConnection();
    		conn.connect();
    		BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
    		bitmap = BitmapFactory.decodeStream(bis);
    	}
    	catch(Exception e) {
    	}
    	return bitmap;
    }
    
    private void setScreen() {
    	
    	Runnable run = new Runnable() {
			@Override
			public void run() {

		        ImageView screenImage = (ImageView)findViewById(R.id.screen);
		        if(!xmlData.fileURL.equals("") && !xmlData.HQFileURL.equals("")) {
			        screenImage.setOnClickListener(new View.OnClickListener() {
			            public void onClick(View v) {
			            	//finish();
			            	viewVideo(xmlData);
			            }
			        });
		        }
		        
		        screenImage.setScaleType(ImageView.ScaleType.FIT_XY);
		        
		        if(!xmlData.JPG.equals("")) {
		        	screenImage.setImageBitmap(getRemoteImage(xmlData.JPG));
				}
			}
		};
		
		Handler handler = new Handler();
		handler.postDelayed(run, 1000);
	}
    
    private void showAlertDialog(String title) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        
        if(title.equals("Network")) {
        	alertDialog.setMessage("An error occurred while fetching data. Please try again later.");
        	/*
            alertDialog.setPositiveButton("Close",
	        	new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	dialog.dismiss();
	                finish();
	            }
	        });
	        */
        }
        
        alertDialog.show();
    }
    
    private void showResultDialog(String title, String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Close",
        	new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();
            }
        });
        alertDialog.show();
    }
} 
