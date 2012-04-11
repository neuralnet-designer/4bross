package com.samsung.cares;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle; 
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import android.webkit.WebView;

public class WarrantyDetailActivity extends ActivityGroup {
	
	//private DetailAdapter adapter;
	private Activity activity;
	private XMLData xmlData;
	private WebView warranty;
	protected ProgressBar loadingProgressBar;
	
	protected String TYPE = "";
 
    /** Called when the activity is first created. */ 
    @Override 
    public void onCreate(Bundle savedInstanceState) { 
    	//setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
    	
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.page_warranty); 

        xmlData = getIntent().getExtras().getParcelable("xmlData");
        //adapter = new DetailAdapter(this, xmlData);
        TYPE = xmlData.type;
        activity = this;
        
        warranty = (WebView)findViewById(R.id.webView);
		warranty.getSettings().setJavaScriptEnabled(true);	
		
		loadingProgressBar = (ProgressBar)findViewById(R.id.loading_progress);
        loadingProgressBar.setVisibility(View.VISIBLE);
		
		Status.NETWORK = Util.checkNetworkStatus(this);
		
		getContent();
    }
    
    @Override
	public void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
    
    protected void getContent() {
		
		Runnable run = new Runnable() {
			@Override
			public void run() {
				
				boolean isNetworkError = true;
				
				try {

		            String CONTENTURL = "http://www.samsungsupport.com/feed/rss/cares_content.jsp?siteCode=us&type=" + TYPE + "&manufacturer=" + Util.urlEncoder(Status.MANUFACTURER) + "&model=" + Util.urlEncoder(Status.MODEL) + "&warrantyId=";
		            Logger.d(CONTENTURL);
		            
		            /*
					HttpClient client = new DefaultHttpClient();
		            HttpGet request = new HttpGet(CONTENTURL);
		            HttpResponse response = client.execute(request);

		            String html = "";
		            InputStream in = response.getEntity().getContent();
		            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		            StringBuilder str = new StringBuilder();
		            String line = null;
		            while((line = reader.readLine()) != null)
		            {
		                str.append(line);
		            }
		            in.close();
		            html = str.toString();
		            */
					
					warranty.loadUrl(CONTENTURL);		            
		            
		            isNetworkError = false;
		        }
		        catch (Exception e) {
		        	Logger.d("Page - getContent - Exception");
		        	showAlertDialog("Network");
		        	e.printStackTrace();
		        }
		        
		        if(isNetworkError) {
	            	showAlertDialog("Network");
	            }
				
		        loadingProgressBar.setVisibility(View.GONE);
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
