package com.samsung.cares;

import java.io.InputStream;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.samsung.cares.common.Status;
import com.samsung.cares.common.XMLData;
import com.samsung.cares.util.Logger;
import com.samsung.cares.util.Util;

public class TrackingDetailActivity extends Activity implements OnScrollListener {
	
	private ProgressBar loadingProgressBar;
	
	private String TYPE = "TRACKING_DETAIL";
	
	private String ticketNo;
	private String phoneNo;
	
	private XMLData trackingDetailData;
	
	private LinearLayout detailLayout;
	
	private ImageButton callButton;
	private ImageButton callUnableButton;
	private ImageButton uploadButton;
	private ImageButton homeButton;
	private ImageButton backButton;
	
	private static final int SELECT_RECEIPT = 100;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.tracking_detail);
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
       
        callButton = (ImageButton)findViewById(R.id.call_button);
        callUnableButton = (ImageButton)findViewById(R.id.call_unable_button);
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
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
		
		Status.NETWORK = Util.checkNetworkStatus(this);
  		
  		if(Status.NETWORK == Status.NETWORK_NONE) {
  			showAlertDialog("Connection");
  		}
  		else {
		
			Runnable run = new Runnable() {
				@Override
				public void run() {
					
					String tag;
					int index = 0;
		            
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
			            boolean isNetworkError = true;
			            
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
			            		isNetworkError = false;
			            	}
			            	else if(eventType == XmlPullParser.TEXT) {
			            	} 
			            	
			            	eventType = xpp.next(); 
			            }
			            
			            if(isNetworkError) {
			            	showAlertDialog("Network");
			            }
			        }
			        catch (Exception e) {
			        	Logger.d("Page - getTrackingDetail - Exception");
			        	e.printStackTrace();
			        }
			        
			        loadingProgressBar.setVisibility(View.GONE);
		            Logger.d("Tracking Count:"+index);
			        //pageListView.setVisibility(View.INVISIBLE);
			        if(index > 0) { //there is no gallery, so layout should not be displayed.
			        	setTrackingDetail(trackingDetailData);
			        }
				}
			};
			
			Handler handler = new Handler();
			handler.postDelayed(run, 1000);
  		}
	}
	
	private void setTrackingDetail(XMLData xmlData) {
		detailLayout.setVisibility(View.VISIBLE);
		
        ((TextView)findViewById(R.id.ticketNo)).setText(xmlData.ticketNo);
        ((TextView)findViewById(R.id.statusDesc)).setText(xmlData.statusDesc);
    	((TextView)findViewById(R.id.requestDate)).setText(xmlData.postingDate);

        if(xmlData.serviceType.equals("IH")) {
        	setTrackingDetailVisible(true);
        
        	((TextView)findViewById(R.id.ascName)).setText(xmlData.ascName);
        	((TextView)findViewById(R.id.scheduleDate)).setText(xmlData.scheduleDate);
        	((TextView)findViewById(R.id.completeDate)).setText(xmlData.completeDate);

            if(xmlData != null && xmlData.ascPhone != null) {
            	callButton.setVisibility(View.VISIBLE);
            	callUnableButton.setVisibility(View.GONE);
            }
            else {
            	callButton.setVisibility(View.GONE);
            	callUnableButton.setVisibility(View.VISIBLE);
            }

		}else{
			setTrackingDetailVisible(false);
			
			callButton.setVisibility(View.GONE);
        	callUnableButton.setVisibility(View.VISIBLE);
        	
        	((TextView)findViewById(R.id.receivedDate)).setText(xmlData.receiveDate);
        	((TextView)findViewById(R.id.shipDate)).setText(xmlData.shipDate);
        	((TextView)findViewById(R.id.trackingInfo)).setText(xmlData.trackingNo);
		} 
         
	    
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
	
	private void showAlertDialog(String title) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(title);
		alertDialog.setCancelable(true);
        
        if(title.equals("Network")) {
        	alertDialog.setMessage(getString(R.string.msg_network_error));
        	alertDialog.setPositiveButton("Close",
	        	new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	dialog.dismiss();
	                finish();
	            }
	        });
        }
        else if(title.equals("Connection")) {
			alertDialog.setMessage(getString(R.string.msg_no_connection));
			alertDialog.setPositiveButton("Close",
		        	new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	dialog.dismiss();
		                finish();
		            }
		        });
			
			/*
			final AlertDialog dlg = alertDialog.create();

            dlg.show();

            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                public void run() {
                    dlg.dismiss(); // when the task active then close the dialog
                    t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                }
            }, 2000); // after 2 second (or 2000 miliseconds), the task will be active.
            */
        }
        else if(title.equals("Exit")) {
        	alertDialog.setMessage(getString(R.string.msg_exit));
        	alertDialog.setPositiveButton("Yes",
            	new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	dialog.dismiss();
                    finish();
                }
            });
        	alertDialog.setNegativeButton("No", null);
        }
        
        alertDialog.show();
    }
	
	private void setTrackingDetailVisible(boolean isIH) {
		
		int visible = isIH ? View.VISIBLE : View.GONE;
		
		findViewById(R.id.ascNameDiv).setVisibility(visible);
		findViewById(R.id.ascNameTitle).setVisibility(visible);
    	findViewById(R.id.ascName).setVisibility(visible);
    	findViewById(R.id.scheduleDateDiv).setVisibility(visible);
    	findViewById(R.id.scheduleDateTitle).setVisibility(visible);
    	findViewById(R.id.scheduleDate).setVisibility(visible);
    	findViewById(R.id.completeDateDiv).setVisibility(visible);
    	findViewById(R.id.completeDateTitle).setVisibility(visible);
    	findViewById(R.id.completeDate).setVisibility(visible);
    	
    	visible = isIH ? View.GONE : View.VISIBLE;
    	
    	findViewById(R.id.receivedDateDiv).setVisibility(visible);
    	findViewById(R.id.receivedDateTitle).setVisibility(visible);
    	findViewById(R.id.receivedDate).setVisibility(visible);
    	findViewById(R.id.shipDateDiv).setVisibility(visible);
    	findViewById(R.id.shipDateTitle).setVisibility(visible);    	
    	findViewById(R.id.shipDate).setVisibility(visible);
    	findViewById(R.id.trackingInfoDiv).setVisibility(visible);
    	findViewById(R.id.trackingInfoTitle).setVisibility(visible);
    	findViewById(R.id.trackingInfo).setVisibility(visible);
	}
}