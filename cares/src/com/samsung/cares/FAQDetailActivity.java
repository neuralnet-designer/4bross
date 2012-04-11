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

public class FAQDetailActivity extends ActivityGroup {
	
	//private DetailAdapter adapter;
	private Activity activity;
	private XMLData xmlData;
	protected ProgressBar loadingProgressBar;
	
	protected String TYPE = "";
    protected String SUBTYPE = "";
    protected int LEVEL = 0;
    protected int COUNT = 0;
    
    protected String PRODUCTID = "";
    
    protected String CONTENTID = "";
	
	public int VIEW_PAGE_NUM;
    int nowPage = 0;
    
	ViewPager faqViewPager = null;
	FAQDetailPagerAdapter faqDetailPagerAdapter = null;
    Context context = null;
    
    protected ArrayList contentURLList;
 
    /** Called when the activity is first created. */ 
    @Override 
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        
        setContentView(R.layout.page_faq); 
        
        context = this;
        contentURLList = new ArrayList();
        
        Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
				
			XMLData xmlData = bundle.getParcelable("xmlData");
			
			if(xmlData != null) {
				
				TYPE = xmlData.type;
				SUBTYPE = xmlData.subType;
				try {
					LEVEL = Integer.parseInt(xmlData.level);
					COUNT = Integer.parseInt(xmlData.count);
				}
				catch(NumberFormatException nfe) {
				}
				
				PRODUCTID = xmlData.productId;
				CONTENTID = xmlData.contentId;
			}
		}
		
		VIEW_PAGE_NUM = 5; //temporary

        activity = this;
        
        ViewGroup buttonView = (ViewGroup)findViewById(R.id.step_view);
        buttonView.requestFocus();
        for (int x = 0; x < VIEW_PAGE_NUM; x++) {
        	TextView button = (TextView)buttonView.getChildAt(x);
        	button.setVisibility(View.VISIBLE);
	        button.setId(x);
	        button.setText("Step " + (x + 1));
	        button.setOnClickListener(onStepClickListener);
        }
        
        faqDetailPagerAdapter = new FAQDetailPagerAdapter(getApplicationContext(), contentURLList);
        
        faqViewPager = (ViewPager)findViewById(R.id.faq_view);
        //faqViewPager.setAdapter(faqDetailPagerAdapter);
        faqViewPager.setOffscreenPageLimit(VIEW_PAGE_NUM);
        faqViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

			@Override
			public void onPageSelected(int position) {
				pageCheck(position);				
			}
        });

        
		
		loadingProgressBar = (ProgressBar)findViewById(R.id.loading_progress);
        //loadingProgressBar.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.GONE);
		
		Status.NETWORK = Util.checkNetworkStatus(this);
		
		//getContent();
		getContentURL();
    }
    
    OnClickListener onStepClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			nowPage = id;
			pageCheck(nowPage);
			faqViewPager.setCurrentItem(nowPage);
		}
    };
	
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
    
    protected void getContentURL() {
		
		Runnable run = new Runnable() {
			@Override
			public void run() {
				
				String tag;
		        XMLData xmlData = null;
		        
		        try {
		        	
		        	String XMLURL = "http://www.samsungsupport.com/feed/rss/cares.jsp?type=" + TYPE + "&subType=" + SUBTYPE + "&level=" + LEVEL + "&siteCode=" + Status.SITECODE + "&version=" + Util.urlEncoder(Status.VERSION) + "&manufacturer=" + Util.urlEncoder(Status.MANUFACTURER) + "&model=" + Util.urlEncoder(Status.MODEL) + "&serial=" + Util.urlEncoder(Status.SERIAL) + "&phone=" + Util.urlEncoder(Status.PHONE) + "&email=" + Util.urlEncoder(Status.EMAIL) + "&productId=" + PRODUCTID + "&contentId=" + CONTENTID;
		        	Logger.d(XMLURL);
		        	URL url = new URL(XMLURL);
		            
		            XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
		            factory.setNamespaceAware(true); 
		            XmlPullParser xpp = factory.newPullParser(); 
		            
		            InputStream in = url.openStream();
		            xpp.setInput(in, "utf-8");
		         
		            int eventType = xpp.getEventType();
		            int index = 1;
		            
		            while(eventType != XmlPullParser.END_DOCUMENT) { 
		            	if(eventType == XmlPullParser.START_DOCUMENT) {
		            	}
		            	else if(eventType == XmlPullParser.END_DOCUMENT) { 
		            	}
		            	else if(eventType == XmlPullParser.START_TAG) {
		            		
		            		tag = xpp.getName();                  
		                  
		            		if(tag.equals("channel")) {
		            		}  
		            		if(tag.equals("item")) {
		            			xmlData = new XMLData();
		            		}
		            		if(tag.equals("type")) {
		            			xmlData.type = xpp.nextText();		                      
		            		} 
		            		if(tag.equals("subType")) {                      
		            			xmlData.subType = xpp.nextText() ;
		            		}
		            		if(tag.equals("level")) {                      
		            			xmlData.level = xpp.nextText() ;
		            		}
		            		if(tag.equals("count")) {                      
		            			xmlData.count = xpp.nextText() ;
		            		}
		            		if(tag.equals("productId")) {                      
		            			xmlData.productId = xpp.nextText() ;
		            		}
		            		if(tag.equals("contentId")) {                      
		            			xmlData.contentId = xpp.nextText() ;
		            		}
		            		if(tag.equals("contentTitle")) {                      
		            			xmlData.contentTitle = xpp.nextText() ;
		            		}
		            		if(tag.equals("content1URL")) {                      
		            			xmlData.content1URL = xpp.nextText() ;
		            		}
		            		if(tag.equals("content2URL")) {                      
		            			xmlData.content2URL = xpp.nextText() ;
		            		}
		            		if(tag.equals("content3URL")) {                      
		            			xmlData.content3URL = xpp.nextText() ;
		            		}
		            		if(tag.equals("content4URL")) {                      
		            			xmlData.content4URL = xpp.nextText() ;
		            		}
		            		if(tag.equals("content5URL")) {                      
		            			xmlData.content5URL = xpp.nextText() ;
		            		}
		            	}
		            	else if(eventType == XmlPullParser.END_TAG) { 
		            		tag = xpp.getName();
		            		if(tag.equals("item")) {
		            			if(xmlData.content1URL != null) {
		            				contentURLList.add(xmlData.content1URL);
		            				Logger.d("xmlData.content1URL:"+xmlData.content1URL);
		            			}
		            			if(xmlData.content2URL != null) {
		            				contentURLList.add(xmlData.content2URL);
		            			}
		            			if(xmlData.content3URL != null) {
		            				contentURLList.add(xmlData.content3URL);
		            			}
		            			if(xmlData.content4URL != null) {
		            				contentURLList.add(xmlData.content4URL);
		            			}
		            			if(xmlData.content5URL != null) {
		            				contentURLList.add(xmlData.content5URL);
		            			}
		            			/*
		            			int COUNT = Integer.parseInt(xmlData.count);
		            			if(COUNT > 0) {
		            				contentURLList.add(xmlData.content4URL);
		            			}
		            			*/
		            			xmlData = null;
		            			index++;
		            		}
		            	}
		            	else if(eventType == XmlPullParser.TEXT) {
		            	} 
		            	
		            	eventType = xpp.next(); 
		            }
		            
		          //loadingProgressBar.setVisibility(View.GONE);
		            Logger.d("contentURLList.size():"+contentURLList.size());
		            faqDetailPagerAdapter.notifyDataSetChanged();
		            faqViewPager.setAdapter(faqDetailPagerAdapter);
		            //pageListView.setVisibility(View.INVISIBLE);
			        //if(selectedPosition > 0) {
			        //	categorySpinner.setSelection(selectedPosition);
			        //}
			        //if(index > 1) { //there is no category, so layout should not be displayed.
				    //    categoryLayout.setVisibility(View.VISIBLE);
			        //}
		        }
		        catch (Exception e) {
		        	Logger.d("Page - getContentURL - Exception");
		        	//e.printStackTrace();
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
    
    private class FAQDetailPagerAdapter extends PagerAdapter {
    	
    	private ArrayList contentURLList;
    	private LayoutInflater layoutInflater;
    	
    	public FAQDetailPagerAdapter(Context context, ArrayList contentURLList){
            super();
            this.contentURLList = contentURLList;
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

    		/*    		
    		if(position == 0){
    			view = layoutInflater.inflate(R.layout.page_faq_detail, null);
               	//view.findViewById(R.id.iv_one);
               	//view.findViewById(R.id.imageView1).setOnClickListener(mPagerListener);
    			
    			WebView webView = (WebView)view.findViewById(R.id.webView);
    			//webView.getSettings().setJavaScriptEnabled(true);	
    			//String CONTENTURL = "http://www.samsungsupport.com/spstv/rss/cares_content.jsp?siteCode=us&type=" + TYPE + "&manufacturer=" + Util.urlEncoder(Status.MANUFACTURER) + "&model=" + Util.urlEncoder(Status.MODEL) + "&warrantyId=";
    			String CONTENTURL = "http://www.samsungsupport.com/faq/01_ledtv.html";
    			webView.loadUrl(CONTENTURL);
            }
    		else if(position == 1){
    			view = layoutInflater.inflate(R.layout.page_faq_detail, null);
               	//view.findViewById(R.id.iv_one);
               	//view.findViewById(R.id.imageView1).setOnClickListener(mPagerListener);
    			
    			WebView webView = (WebView)view.findViewById(R.id.webView);
    			//webView.getSettings().setJavaScriptEnabled(true);	
    			//String CONTENTURL = "http://www.samsungsupport.com/spstv/rss/cares_content.jsp?siteCode=us&type=" + TYPE + "&manufacturer=" + Util.urlEncoder(Status.MANUFACTURER) + "&model=" + Util.urlEncoder(Status.MODEL) + "&warrantyId=";
    			String CONTENTURL = "http://www.samsungsupport.com/faq/02_ledtv.html";
    			webView.loadUrl(CONTENTURL);
            }
    		else if(position == 2){
    			view = layoutInflater.inflate(R.layout.page_faq_detail, null);
               	//view.findViewById(R.id.iv_one);
               	//view.findViewById(R.id.imageView1).setOnClickListener(mPagerListener);
    			
    			WebView webView = (WebView)view.findViewById(R.id.webView);
    			//webView.getSettings().setJavaScriptEnabled(true);	
    			//String CONTENTURL = "http://www.samsungsupport.com/spstv/rss/cares_content.jsp?siteCode=us&type=" + TYPE + "&manufacturer=" + Util.urlEncoder(Status.MANUFACTURER) + "&model=" + Util.urlEncoder(Status.MODEL) + "&warrantyId=";
    			String CONTENTURL = "http://www.samsungsupport.com/faq/03_ledtv.html";
    			webView.loadUrl(CONTENTURL);
            }
    		else if(position == 3){
    			view = layoutInflater.inflate(R.layout.page_faq_detail, null);
               	//view.findViewById(R.id.iv_one);
               	//view.findViewById(R.id.imageView1).setOnClickListener(mPagerListener);
    			
    			WebView webView = (WebView)view.findViewById(R.id.webView);
    			//webView.getSettings().setJavaScriptEnabled(true);	
    			//String CONTENTURL = "http://www.samsungsupport.com/spstv/rss/cares_content.jsp?siteCode=us&type=" + TYPE + "&manufacturer=" + Util.urlEncoder(Status.MANUFACTURER) + "&model=" + Util.urlEncoder(Status.MODEL) + "&warrantyId=";
    			String CONTENTURL = "http://www.samsungsupport.com/faq/04_ledtv.html";
    			webView.loadUrl(CONTENTURL);
            }
    		else if(position == 4){
    			view = layoutInflater.inflate(R.layout.page_faq_detail, null);
               	//view.findViewById(R.id.iv_one);
               	//view.findViewById(R.id.imageView1).setOnClickListener(mPagerListener);
    			
    			WebView webView = (WebView)view.findViewById(R.id.webView);
    			//webView.getSettings().setJavaScriptEnabled(true);	
    			//String CONTENTURL = "http://www.samsungsupport.com/spstv/rss/cares_content.jsp?siteCode=us&type=" + TYPE + "&manufacturer=" + Util.urlEncoder(Status.MANUFACTURER) + "&model=" + Util.urlEncoder(Status.MODEL) + "&warrantyId=";
    			String CONTENTURL = "http://www.samsungsupport.com/faq/05_ledtv.html";
    			webView.loadUrl(CONTENTURL);
            }
            */
    		view = layoutInflater.inflate(R.layout.page_faq_detail, null);			
			WebView webView = (WebView)view.findViewById(R.id.webView);
			//webView.getSettings().setJavaScriptEnabled(true);	
			Logger.d("URL[" + position + "] : " + (String)contentURLList.get(position));
			webView.loadUrl((String)contentURLList.get(position));    		
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
