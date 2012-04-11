package com.samsung.cares;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

public class TrackingDetailActivity extends Activity {
	
	private ProgressBar loadingProgressBar;
	
	private String TYPE = "tracking_detail";
	
	private String ticketNo;
	private String phoneNo;
	
	private XMLData trackingDetailData;
	
	private LinearLayout detailLayout;
	
	private TextView ticketNoView;
	private TextView statusDescView; 
	private TextView ascNameView; 
	private TextView postingDateView; 
	private TextView scheduleDateView; 
	private TextView completeDateView; 
	
	private ImageButton callButton;
	private ImageButton uploadButton;
	
	private ImageButton homeButton;
	private ImageButton backButton;
	
	private static final int SELECT_RECEIPT = 100;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.page_tracking_detail);
        
        loadingProgressBar = (ProgressBar)findViewById(R.id.loading_progress);
        loadingProgressBar.setVisibility(View.VISIBLE);
        
        Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
				
			XMLData xmlData = bundle.getParcelable("xmlData");
			
			if(xmlData != null) {
				ticketNo = xmlData.ticketNo;
				phoneNo = xmlData.phoneNo;
				getTrackingDetail();
			}
		}
		
		detailLayout = (LinearLayout)findViewById(R.id.detailLayout);
        
        ticketNoView = (TextView)findViewById(R.id.ticketNo);
        statusDescView = (TextView)findViewById(R.id.statusDesc);
        ascNameView = (TextView)findViewById(R.id.ascName);
        postingDateView = (TextView)findViewById(R.id.postingDate);
        scheduleDateView = (TextView)findViewById(R.id.scheduleDate);
        completeDateView = (TextView)findViewById(R.id.completeDate);
        
        callButton = (ImageButton)findViewById(R.id.call_button);
        uploadButton = (ImageButton)findViewById(R.id.upload_button);
        
        homeButton = (ImageButton)findViewById(R.id.home_button);
        backButton = (ImageButton)findViewById(R.id.back_button);
        
        callButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	if(trackingDetailData != null && trackingDetailData.ascPhone != null) {
	        		call(trackingDetailData.ascPhone);
	        	}
	        }
	    });
        
        uploadButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	uploadImage();
	        }
	    });
        
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
        
        //for uploading test
        ImageView logo = (ImageView)findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	Intent intent = new Intent(TrackingDetailActivity.this, ImageUploadActivity.class);
	        	startActivity(intent);
	        }
	    });
    }
	
	private void viewMain() {
    	Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
    }
	
	private void uploadImage() {
		/*
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);  
		*/
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(
				Intent.createChooser(intent, "Select Receipt"), SELECT_RECEIPT);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

	    switch(requestCode) { 
	    case SELECT_RECEIPT:
	        if(resultCode == RESULT_OK){  
	            Uri selectedImage = imageReturnedIntent.getData();
	            String[] filePathColumn = {MediaStore.Images.Media.DATA};

	            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
	            cursor.moveToFirst();

	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String filePath = cursor.getString(columnIndex);
	            cursor.close();

	            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
	            
	        }
	    }
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
	
	private void getTrackingDetail() {
		
		Runnable run = new Runnable() {
			@Override
			public void run() {
				
				String tag;
		        
		        try {
		        	
		        	String XMLURL = "http://www.samsungsupport.com/feed/rss/cares.jsp?siteCode=us&type=" + TYPE + "&ticketNo=" + ticketNo + "&phoneNo=" + phoneNo;
		        	//Logger.d(XMLURL);
		        	URL url = new URL(XMLURL);
		        	
		        	XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
		            factory.setNamespaceAware(true); 
		            XmlPullParser xpp = factory.newPullParser(); 
		            
		            InputStream in = url.openStream();
		            xpp.setInput(in, "utf-8");
		         
		            int eventType = xpp.getEventType();
		            int index = 0;
		            
		            while(eventType != XmlPullParser.END_DOCUMENT) { 
		            	if(eventType == XmlPullParser.START_DOCUMENT) {
		            	}
		            	else if(eventType == XmlPullParser.END_DOCUMENT) { 
		            	}
		            	else if(eventType == XmlPullParser.START_TAG) {
		            		
		            		tag = xpp.getName();                  
		                  
		            		if(tag.equals("item")) {
		            			trackingDetailData = new XMLData();
		            		} 
		            		if(tag.equals("ticketNo")) {
		            			trackingDetailData.ticketNo = xpp.nextText();		                      
		            		} 
		            		if(tag.equals("company")) {
		            			trackingDetailData.company = xpp.nextText();		                      
		            		} 
		            		if(tag.equals("serviceType")) {
		            			trackingDetailData.serviceType = xpp.nextText();		                      
		            		} 
		            		if(tag.equals("status")) {
		            			trackingDetailData.status = xpp.nextText();
		            		} 
		            		if(tag.equals("statusDesc")) {
		            			trackingDetailData.statusDesc = xpp.nextText();
		            		}
		            		if(tag.equals("delayReason")) {
		            			trackingDetailData.delayReason = xpp.nextText();
		            		}
		            		if(tag.equals("delayReasonDesc")) {                      
		            			trackingDetailData.delayReasonDesc = xpp.nextText() ;
		            		}
		            		if(tag.equals("ascNo")) {
		            			trackingDetailData.ascNo = xpp.nextText();
		            		}
		            		if(tag.equals("ascName")) {
		            			trackingDetailData.ascName = xpp.nextText();
		            		}
		            		if(tag.equals("ascPhone")) {
		            			trackingDetailData.ascPhone = xpp.nextText();
		            		}
		            		if(tag.equals("postingDate")) {
		            			trackingDetailData.postingDate = xpp.nextText();
		            		}  
		            		if(tag.equals("scheduleDate")) {
		            			trackingDetailData.scheduleDate = xpp.nextText();
		            		}  
		            		if(tag.equals("completeDate")) {
		            			trackingDetailData.completeDate = xpp.nextText();
		            		}
		            		if(tag.equals("receiveDate")) {
		            			trackingDetailData.receiveDate = xpp.nextText();
		            		}
		            		if(tag.equals("shipDate")) {
		            			trackingDetailData.shipDate = xpp.nextText();		                      
		            		}  
		            		if(tag.equals("trackingNo")) {
		            			trackingDetailData.trackingNo = xpp.nextText();		                      
		            		} 
		            		if(tag.equals("trackingURL")) {
		            			trackingDetailData.trackingURL = xpp.nextText();		                      
		            		} 
		            		if(tag.equals("receiptFileName")) {
		            			trackingDetailData.receiptFileName = xpp.nextText();		                      
		            		} 
		            	}
		            	else if(eventType == XmlPullParser.END_TAG) { 
		            		tag = xpp.getName();
		            		if(tag.equals("item")) {
		            			//trackingDetailData = null;
		            			index++;
		            		}
		            	}
		            	else if(eventType == XmlPullParser.TEXT) {
		            	} 
		            	
		            	eventType = xpp.next(); 
		            }
		            
		            loadingProgressBar.setVisibility(View.GONE);
		            Logger.d("Tracking Count:"+index);
			        //pageListView.setVisibility(View.INVISIBLE);
			        if(index > 0) { //there is no gallery, so layout should not be displayed.
			        	setTrackingDetail(trackingDetailData);
			        }
		        }
		        catch (Exception e) {
		        	Logger.d("Page - getTrackingDetail - Exception");
		        	e.printStackTrace();
		        }
			}
		};
		
		Handler handler = new Handler();
		handler.postDelayed(run, 1000);
	}
	
	private void setTrackingDetail(XMLData xmlData) {
		
		detailLayout.setVisibility(View.VISIBLE);
		
		//if(xmlData.serviceType.equals("IH")) {
			//inHomeLayout.setVisibility(View.VISIBLE);
			//depotLayout.setVisibility(View.GONE);
			ticketNoView.setText(xmlData.ticketNo);
	        statusDescView.setText(xmlData.statusDesc);
	        ascNameView.setText(xmlData.ascName);
	        postingDateView.setText(xmlData.postingDate);
	        scheduleDateView.setText(xmlData.scheduleDate);
	        completeDateView.setText(xmlData.completeDate);
	        if(xmlData != null && xmlData.ascPhone != null) {
	        	callButton.setVisibility(View.VISIBLE);
	        }
		//}
	        
	    
	}
}