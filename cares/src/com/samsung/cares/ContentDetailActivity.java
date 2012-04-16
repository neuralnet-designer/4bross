package com.samsung.cares;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ContentDetailActivity extends ActivityGroup {
	
	//private DetailAdapter adapter;
	private Activity activity;
	private XMLData xmlData;
	private ProgressBar loadingProgressBar;
	
	private String TYPE = "";
	private String SUBTYPE = "";
	private int LEVEL = 0;
	private int COUNT = 0;
    
	private String PRODUCT_ID = "";
    
	private String CONTENT_ID = "";
	private String CONTENT_URL = "";
	
	public int VIEW_PAGE_NUM;
    int nowPage = 0;
    
	ViewPager contentViewPager = null;
	ContentDetailPagerAdapter contentDetailPagerAdapter = null;
    Context context = null;
    
    private LinearLayout footerButtonView;
    private ImageButton homeButton;
    private ImageButton backButton;
    
    /** Called when the activity is first created. */ 
    @Override 
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        
        setContentView(R.layout.content_detail); 
        
        context = this;
        
        Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
				
			XMLData xmlData = bundle.getParcelable("xmlData");
			
			if(xmlData != null) {
				
				TYPE = xmlData.type;
				SUBTYPE = xmlData.subType;
				LEVEL = Util.parseInt(xmlData.level, 0);
				COUNT = Util.parseInt(xmlData.count, 0);
				VIEW_PAGE_NUM = Util.parseInt(xmlData.stepCount, 1);				
				PRODUCT_ID = xmlData.productId;
				CONTENT_ID = xmlData.contentId;
				CONTENT_URL = xmlData.contentURL;
			}
		}
		
		activity = this;
        
        ViewGroup buttonView = (ViewGroup)findViewById(R.id.step_view);
        
        if(VIEW_PAGE_NUM > 1) {
        	buttonView.setVisibility(View.VISIBLE);
        	buttonView.requestFocus();
	        for (int x = 0; x < VIEW_PAGE_NUM; x++) {
	        	TextView button = (TextView)buttonView.getChildAt(x);
	        	button.setVisibility(View.VISIBLE);
		        button.setId(x);
		        button.setText("Step " + (x + 1));
		        button.setOnClickListener(onStepClickListener);
	        }
        }
        
        contentDetailPagerAdapter = new ContentDetailPagerAdapter(getApplicationContext());
        
        contentViewPager = (ViewPager)findViewById(R.id.contentViewPager);
        contentViewPager.setAdapter(contentDetailPagerAdapter);
        contentViewPager.setOffscreenPageLimit(VIEW_PAGE_NUM);
        contentViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

			@Override
			public void onPageSelected(int position) {
				pageCheck(position);				
			}
        });
        
        footerButtonView = (LinearLayout)findViewById(R.id.footer_button);
        
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
        
        loadingProgressBar = (ProgressBar)findViewById(R.id.loading_progress);
        //loadingProgressBar.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.GONE);
		
		Status.NETWORK = Util.checkNetworkStatus(this);
    }
    
    OnClickListener onStepClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			nowPage = id;
			pageCheck(nowPage);
			contentViewPager.setCurrentItem(nowPage);
		}
    };
    
    protected void viewMain() {

    	Intent intent = new Intent(this, MainActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    }
	
	public void pageCheck(int pageNum){
		/*
		if(pageNum <= 0){
			bt_prev.setEnabled(false);
		}else if(pageNum >= VIEW_PAGE_NUM-1){
			bt_next.setEnabled(false);
		}else{
			bt_prev.setEnabled(true);			
			bt_next.setEnabled(true);
		}
		*/
		nowPage = pageNum;
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
    
    private class ContentDetailPagerAdapter extends PagerAdapter {
    	
    	private LayoutInflater layoutInflater;
    	
    	public ContentDetailPagerAdapter(Context context) {
            super();
            layoutInflater = LayoutInflater.from(context);
        }
                
    	@Override
    	public int getCount() {
    		return VIEW_PAGE_NUM;
    	}

    	@Override
        public Object instantiateItem(View collection, int position) {
    		View view = null;
    		
    		Logger.d("=====================================instantiateItem");

    		view = layoutInflater.inflate(R.layout.content_detail_webview, null);			
			WebView webView = (WebView)view.findViewById(R.id.webView);
			
			webView.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
		            view.loadUrl(url);
		            return true;
		        }
			});
			
			webView.setWebChromeClient(new WebChromeClient() {
	            public void onProgressChanged(WebView view, int progress) {
	            	if(progress == 100) {
	            		loadingProgressBar.setVisibility(View.GONE);
	            		footerButtonView.setVisibility(View.VISIBLE);
	            	}
	            	else if(loadingProgressBar.getVisibility() != View.VISIBLE) {
	            		loadingProgressBar.setVisibility(View.VISIBLE);
	            	}
	            }
			});
			
			webView.getSettings().setJavaScriptEnabled(true);	
			String param = "";
			if(position > 0) {
				param = "?s=" + (position + 1);
			}
			Logger.d("URL[" + position + "] : " + CONTENT_URL + param);
			
			webView.loadUrl(CONTENT_URL + param);    		
            ((ViewPager)collection).addView(view, position);
            
            return view;
        }

        @Override
        public void destroyItem(View collection, int position, Object view) {
        	((ViewPager)collection).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
        	return view == ((View)object);
        }

        @Override
        public void finishUpdate(View v) {
        }
                
        @Override
        public void restoreState(Parcelable pc, ClassLoader cl) {
        }

        @Override
        public Parcelable saveState() {
        	return null;
        }

        @Override
        public void startUpdate(View v) {
        }
    }
} 
