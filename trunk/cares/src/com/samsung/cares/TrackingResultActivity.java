package com.samsung.cares;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.samsung.cares.common.XMLData;
import com.samsung.cares.custom.CustomListView;
import com.samsung.cares.util.Logger;

public class TrackingResultActivity extends Activity implements OnScrollListener {
	
	private String phoneNo = "";
	private String firstName = "";
	private String lastName = "";
	private String zipCode = "";	
	
	protected ProgressBar loadingProgressBar;
	//protected CustomListView listView;
	protected ListView listView;
	protected TrackingAdapter trAdapter;
	private ArrayList<XMLData> trList;
	
    protected LayoutInflater inflater;   
    protected View headerView;
    protected View footerView;
    
    protected LinearLayout footerButtonView;
    protected LinearLayout footerLoadingView;
    protected ImageButton homeButton;
	protected ImageButton backButton;	

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.page_tracking_result);
        loadingProgressBar = (ProgressBar)findViewById(R.id.loading_progress);
        loadingProgressBar.setVisibility(View.VISIBLE);

        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
        footerView = inflater.inflate(R.layout.page_list_footer, null); 
        
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
        
        listView = (ListView)findViewById(R.id.list);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
        	@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
        		XMLData xmlData = (XMLData)listView.getItemAtPosition(position);
       			viewDetail(xmlData);
			}    
        });        
        
        trList = new ArrayList<XMLData>();
		trAdapter = new TrackingAdapter(this, trList);
		
        Bundle bundle = getIntent().getExtras();
		if(bundle != null) {				
			XMLData xmlData = bundle.getParcelable("xmlData");
			
			if(xmlData != null) {
				phoneNo = xmlData.phoneNo;
				firstName = xmlData.firstName;
				lastName = xmlData.lastName;
				zipCode = xmlData.zipCode;						
				
				listTickets();
			}
		}
		
        listView.addFooterView(footerView, null, false);
    }
	
	
	private void listTickets(){

		Runnable run = new Runnable() {
			@Override
			public void run() {
				
				String tag;
				com.samsung.cares.common.XMLData xmlData = null;
		        
		        try {
		        	String XMLURL = "http://www.samsungsupport.com/feed/rss/cares.jsp?type=tracking_find" 
		        						+ "&phoneNo=" + phoneNo
		        						+ "&firstName=" + firstName
		        						+ "&lastName=" + lastName
		        						+ "&zipCode=" + zipCode
		        						;
		        	
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
		            			xmlData = new com.samsung.cares.common.XMLData();
		            		} 
		            		if(tag.equals("ticketNo")) {
		            			xmlData.ticketNo = xpp.nextText();		                      
		            		} 
		            		if(tag.equals("modelCode")) {
		            			xmlData.modelCode = xpp.nextText();		                      
		            		} 
		            		if(tag.equals("postingDate")) {
		            			xmlData.postingDate = xpp.nextText();
		            		} 
		            	}
		            	else if(eventType == XmlPullParser.END_TAG) { 
		            		tag = xpp.getName();
		            		if(tag.equals("item")) {
		            			trList.add(xmlData);
		            			index++;
		            		}
		            	}
		            	else if(eventType == XmlPullParser.TEXT) {
		            	} 
		            	
		            	eventType = xpp.next(); 
		            }

		        }
		        catch (Exception e) {
		        	Logger.d("Page - getTrackingDetail - Exception");
		        	e.printStackTrace();
		        }
		        
				listView.setAdapter(trAdapter);
		        loadingProgressBar.setVisibility(View.GONE);
			}
		};
		
		Handler handler = new Handler();
		handler.postDelayed(run, 1000);
		
	}
	
	protected void viewMain() {
    	Intent intent = new Intent(this, MainActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    }	
	
    private void viewDetail(XMLData xmlData) {
		Intent intent = new Intent(this, TrackingDetailActivity.class);
		XMLData param = new XMLData();
		param.ticketNo = xmlData.ticketNo;
		param.phoneNo = phoneNo;
    	intent.putExtra("xmlData", param);
    	startActivity(intent);
    }
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}
}