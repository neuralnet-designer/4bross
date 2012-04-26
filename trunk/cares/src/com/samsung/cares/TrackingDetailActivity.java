package com.samsung.cares;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.cares.common.Status;
import com.samsung.cares.common.XMLData;
import com.samsung.cares.custom.CustomImageView;
import com.samsung.cares.util.Logger;
import com.samsung.cares.util.Util;

public class TrackingDetailActivity extends Activity implements OnScrollListener {
	
	private ProgressBar loadingProgressBar;
	
	private String TYPE = "TRACKING_DETAIL";
	
	private String ticketNo;
	private String phoneNo;
	
	private XMLData trackingDetailData;
	
	private LinearLayout detailLayout;
	private LinearLayout trackingLayout;
	private LinearLayout receiptLayout;
	private LinearLayout receiptImageButtonLayout;
	
	private CustomImageView receiptView;
	private Button receiptUploadButton;
	private Button receiptCancelButton;
	private Bitmap receiptBitmap;
	
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
		trackingLayout = (LinearLayout)findViewById(R.id.trackingLayout);
		receiptLayout = (LinearLayout)findViewById(R.id.receiptLayout);
		receiptImageButtonLayout = (LinearLayout)findViewById(R.id.receiptImageButtonLayout);
		
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
	        	if(receiptLayout.getVisibility() == View.VISIBLE) {
	        		receiptLayout.setVisibility(View.GONE);
					//trackingLayout.setVisibility(View.VISIBLE);
					detailLayout.setVisibility(View.VISIBLE);
	        	}
	        	else {
	        		finish();
	        	}
	        }
	    });
        
        receiptView = (CustomImageView)findViewById(R.id.receiptImageView);
        receiptUploadButton = (Button) findViewById(R.id.receiptImageUpload);        
        receiptUploadButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if(receiptBitmap == null) {
					Toast.makeText(getApplicationContext(), "Please select receipt image.", Toast.LENGTH_SHORT).show();
				}
				else {
					receiptImageButtonLayout.setVisibility(View.INVISIBLE);
					loadingProgressBar.setVisibility(View.VISIBLE);
					new ImageUploadTask().execute();
				}
			}
		});
        
        receiptCancelButton = (Button) findViewById(R.id.receiptImageCancel);
        receiptCancelButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				receiptLayout.setVisibility(View.GONE);
				//trackingLayout.setVisibility(View.VISIBLE);
				detailLayout.setVisibility(View.VISIBLE);
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
	            /*
	        	Uri selectedImage = imageReturnedIntent.getData();
	            String[] filePathColumn = {MediaStore.Images.Media.DATA};

	            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
	            cursor.moveToFirst();

	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String filePath = cursor.getString(columnIndex);
	            cursor.close();

	            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
	            */
	        	Uri selectedImageUri = imageReturnedIntent.getData();
				String filePath = null;

				try {
					// OI FILE Manager
					String filemanagerstring = selectedImageUri.getPath();

					// MEDIA GALLERY
					String selectedImagePath = getPath(selectedImageUri);

					if(selectedImagePath != null) {
						filePath = selectedImagePath;
					}
					else if(filemanagerstring != null) {
						filePath = filemanagerstring;
					}
					else {
						Toast.makeText(getApplicationContext(), "There is no image path.", Toast.LENGTH_LONG).show();
					}

					if(filePath != null) {
						decodeFile(filePath);
					}
					else {
						receiptBitmap = null;
					}
				} catch (Exception e) {
					/*
					Toast.makeText(getApplicationContext(), "Internal error",
							Toast.LENGTH_LONG).show();
					Log.e(e.getClass().getName(), e.getMessage(), e);
					*/
					e.printStackTrace();
				}
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
  			showAlertDialog("Connection", false);
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
			            		if(tag.equals("emailAddress")) {
			            			trackingDetailData.emailAddress = xpp.nextText();		                      
			            		} 
			            		if(tag.equals("modelCode")) {
			            			trackingDetailData.modelCode = xpp.nextText();		                      
			            		} 
			            		if(tag.equals("serialNo")) {
			            			trackingDetailData.serialNo = xpp.nextText();		                      
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
			            	showAlertDialog("Network", false);
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
	
	private void showAlertDialog(String title, boolean isSuccess) {

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
        	
        	alertDialog.show();
        }
        else if(title.equals("Connection")) {
			alertDialog.setMessage(getString(R.string.msg_no_connection));
			alertDialog.setPositiveButton("Close",
		       	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
		            	dialog.dismiss();
		                finish();
		            }
		        }
			);
			
			alertDialog.show();
        }
        else if(title.equals("Receipt")) {
			if(isSuccess) {
				alertDialog.setMessage(getString(R.string.msg_receipt_upload_success));
			}
			else {
				alertDialog.setMessage(getString(R.string.msg_receipt_upload_fail));
			}
			
			final AlertDialog dlg = alertDialog.create();

            dlg.show();

            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                public void run() {
                    dlg.dismiss(); // when the task active then close the dialog
                    t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                }
            }, 2000); // after 2 second (or 2000 miliseconds), the task will be active.
        }
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
	
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}

	public void decodeFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 1024;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while(true) {
			if(width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		receiptBitmap = BitmapFactory.decodeFile(filePath, o2);
		receiptView.setImageBitmap(receiptBitmap);
		
		receiptLayout.setVisibility(View.VISIBLE);
		//trackingLayout.setVisibility(View.GONE);
		detailLayout.setVisibility(View.GONE);
	}
	
	class ImageUploadTask extends AsyncTask <Void, Void, String> {
		@Override
		protected String doInBackground(Void... unsued) {
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();
				HttpPost httpPost = new HttpPost("http://www.samsungsupport.com/feed/rss/cares_upload.jsp?object_id=" + trackingDetailData.ticketNo + "&email=" + trackingDetailData.emailAddress + "&model=" + trackingDetailData.modelCode + "&serial=" + trackingDetailData.serialNo + "&receipt_filename=" + trackingDetailData.receiptFileName);
				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				receiptBitmap.compress(CompressFormat.JPEG, 100, bos);
				byte[] data = bos.toByteArray();
				/*
				entity.addPart("object_id", new StringBody(trackingDetailData.ticketNo));
				entity.addPart("email", new StringBody(trackingDetailData.emailAddress));
				entity.addPart("model", new StringBody(trackingDetailData.modelCode));
				entity.addPart("serial", new StringBody(trackingDetailData.serialNo));
				entity.addPart("receipt_filename", new StringBody(trackingDetailData.receiptFileName));
				entity.addPart("returnformat", new StringBody("json"));
				*/
				entity.addPart("uploaded", new ByteArrayBody(data, "cares.jpg"));
				httpPost.setEntity(entity);
				HttpResponse response = httpClient.execute(httpPost, localContext);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "UTF-8"));
				
				String resultJson = "";
				String thisLine = "";
				while ((thisLine = reader.readLine()) != null) {
					resultJson += thisLine;
			    }
				
				return resultJson.trim();
				
			}
			catch (Exception e) {
				
				loadingProgressBar.setVisibility(View.GONE);
				/*
				if (dialog.isShowing())
					dialog.dismiss();
				*/
				/*
				Toast.makeText(getApplicationContext(),
						e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
				*/
				e.printStackTrace();
				return null;
			}

			// (null);
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String resultJson) {
			try {
				/*
				if (dialog.isShowing())
					dialog.dismiss();
				*/
				
				if (resultJson != null) {
					Logger.d("resultJson : " + resultJson);
					JSONObject jsonObject = new JSONObject(resultJson);
					
					String status = jsonObject.getString("status");
					String message = jsonObject.getString("message");
					String receiptFileName = jsonObject.getString("receiptFileName");
					
					if(status != null && status.equals("ok")) { //success
						trackingDetailData.receiptFileName = receiptFileName; 
						showAlertDialog("Receipt", true);
					}
					else {
						showAlertDialog("Receipt", false);	
					}
				}
			} catch (Exception e) {
				/*
				Toast.makeText(getApplicationContext(),
						e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
				*/
				e.printStackTrace();
			}
			
			loadingProgressBar.setVisibility(View.GONE);
			
			receiptLayout.setVisibility(View.GONE);
			receiptImageButtonLayout.setVisibility(View.VISIBLE);
			//trackingLayout.setVisibility(View.VISIBLE);
			detailLayout.setVisibility(View.VISIBLE);
		}
	}
}