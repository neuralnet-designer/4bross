package com.samsung.cares;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Calendar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.samsung.cares.common.Status;
import com.samsung.cares.common.XMLData;
import com.samsung.cares.util.Logger;
import com.samsung.cares.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class MainActivity extends Activity {
	
    public final int VIEW_PAGE_NUM = 2;
    private int nowPage = 0;
    
    private ViewPager mainViewPager = null;
    private MainPagerAdapter mainPageAdapter = null;
    private Context context = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        context = this;
        
        mainPageAdapter = new MainPagerAdapter(getApplicationContext());
        mainViewPager = (ViewPager)findViewById(R.id.main_view);
        mainViewPager.setAdapter(mainPageAdapter);
        mainViewPager.setOffscreenPageLimit(VIEW_PAGE_NUM);
        
        mainViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

			@Override
			public void onPageSelected(int position) {
				pageCheck(position);				
			}
        });
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
	
	private final View.OnClickListener contactListener = new View.OnClickListener() {
    	@Override
    	public void onClick(View v) {
	       	Intent intent = new Intent(MainActivity.this, ContentActivity.class);
	       	XMLData xmlData = new XMLData();
	       	xmlData.type = "CONTACT";
	       	xmlData.subType = "PRODUCT";
	       	xmlData.level = "0";
	       	intent.putExtra("xmlData", xmlData);
	       	startActivity(intent);
    	}
    };
    
    private final View.OnClickListener howToListener = new View.OnClickListener() {
    	@Override
    	public void onClick(View v) {
	       	Intent intent = new Intent(MainActivity.this, HowToActivity.class);
	       	XMLData xmlData = new XMLData();
	       	xmlData.type = "HOWTO";
	       	intent.putExtra("xmlData", xmlData);
	       	startActivity(intent);
    	}
    };
    
    private final View.OnClickListener faqListener = new View.OnClickListener() {
    	@Override
    	public void onClick(View v) {
	       	Intent intent = new Intent(MainActivity.this, ContentActivity.class);
	       	XMLData xmlData = new XMLData();
	       	xmlData.type = "FAQ";
	       	xmlData.subType = "PRODUCT";
	       	xmlData.level = "0";
	       	intent.putExtra("xmlData", xmlData);
	       	startActivity(intent);
    	}
    };
    
    private final View.OnClickListener warrantyListener = new View.OnClickListener() {
    	@Override
    	public void onClick(View v) {
	       	Intent intent = new Intent(MainActivity.this, ContentActivity.class);
	       	XMLData xmlData = new XMLData();
	       	xmlData.type = "WARRANTY";
	       	xmlData.subType = "PRODUCT";
	       	xmlData.level = "0";
	       	intent.putExtra("xmlData", xmlData);
	       	startActivity(intent);
    	}
    };
    
    private final View.OnClickListener trackingListener = new View.OnClickListener() {
    	@Override
    	public void onClick(View v) {
    		Intent intent = new Intent(MainActivity.this, TrackingActivity.class);
    		startActivity(intent);
    	}
    };
    
    private final View.OnClickListener pageListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == R.id.back_button) {
				nowPage--;
				pageCheck(nowPage);				
			}
			else if(id == R.id.home_button) {
				nowPage--;
				pageCheck(nowPage);				
			}
			else if(id == R.id.more_layout) {
				nowPage++;
				pageCheck(nowPage);
			}
			mainViewPager.setCurrentItem(nowPage);
		}
    };
    
    private final View.OnClickListener privacyListener = new View.OnClickListener() {
    	@Override
    	public void onClick(View v) {
    		XMLData xmlData = new XMLData();
        	xmlData.type = "PRIVACY";
        	xmlData.contentURL = Status.PRIVACY_URL;
        	viewContentDetail(xmlData);
    	}
    };
    
    private final View.OnClickListener legalListener = new View.OnClickListener() {
    	@Override
    	public void onClick(View v) {
    		XMLData xmlData = new XMLData();
        	xmlData.type = "LEGAL";
        	xmlData.contentURL = Status.LEGAL_URL;
        	viewContentDetail(xmlData);
    	}
    };
    
    private final View.OnClickListener aboutListener = new View.OnClickListener() {
    	@Override
    	public void onClick(View v) {
    		XMLData xmlData = new XMLData();
        	xmlData.type = "ABOUT";
        	xmlData.contentURL = Status.ABOUT_URL;
        	viewContentDetail(xmlData);
    	}
    };
    
    private final View.OnClickListener samsungListener = new View.OnClickListener() {
    	@Override
    	public void onClick(View v) {
    		setContentLog("SAMSUNG", "", "", "", "");
    		openBrowser(Status.SAMSUNG_URL);
    	}
    };
    
    private class MainPagerAdapter extends PagerAdapter{
    	
    	private LayoutInflater layoutInflater;
    	
    	public MainPagerAdapter(Context c){
            super();
            layoutInflater = LayoutInflater.from(c);
        }
                
    	@Override
    	public int getCount() {
    		return VIEW_PAGE_NUM;
    	}

    	@Override
        public Object instantiateItem(View collection, int position) {
    		View view = null;
    		if(position == 0){
    			view = layoutInflater.inflate(R.layout.main_first, null);
    			
    			View contactView = view.findViewById(R.id.contact_layout);
    			View howToView = view.findViewById(R.id.how_to_layout);
    			View faqView = view.findViewById(R.id.faq_layout);
    			View warrantyView = view.findViewById(R.id.warranty_layout);
    			View trackingView = view.findViewById(R.id.tracking_layout);
    			View moreView = view.findViewById(R.id.more_layout);
    			
    			contactView.setOnClickListener(contactListener);
    			howToView.setOnClickListener(howToListener);
    			faqView.setOnClickListener(faqListener);
    			warrantyView.setOnClickListener(warrantyListener);
    			trackingView.setOnClickListener(trackingListener);
    			moreView.setOnClickListener(pageListener);
            }
    		else if(position == 1){
               	view = View.inflate(context, R.layout.main_more, null);
               	
               	View privacyView = view.findViewById(R.id.privacy_layout);
               	View legalView = view.findViewById(R.id.legal_layout);
               	View aboutView = view.findViewById(R.id.about_layout);
               	View samsungView = view.findViewById(R.id.samsung_layout);
               	
               	privacyView.setOnClickListener(privacyListener);
               	legalView.setOnClickListener(legalListener);
               	aboutView.setOnClickListener(aboutListener);
               	samsungView.setOnClickListener(samsungListener);
               	
               	view.findViewById(R.id.back_button).setOnClickListener(pageListener);
               	view.findViewById(R.id.home_button).setOnClickListener(pageListener);
            }
    		
            ((ViewPager) collection).addView(view, position);
                   
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
    
    protected void viewContentDetail(XMLData xmlData) {

    	Intent intent = new Intent(this, ContentDetailActivity.class);
    	intent.putExtra("xmlData", xmlData);
    	startActivity(intent);
    	setContentLog(xmlData.type, xmlData.productId, xmlData.contentId, xmlData.orgType, xmlData.orgContentId);
    }
	
	private void openBrowser(String url) {
    	
    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    	startActivity(intent);
    }
    
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	//Logger.d("keyCode ===" + keyCode + "////////// KeyEvent == " + event.toString());
	    
	    //4 : BACK
	    //82 : CONTROL
	    //84 : SEARCH
	    
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	    	Logger.d("event.KEYCODE_BACK");
	    	if(nowPage == 0) {
	    		showAlertDialog("Exit");
	    	}
	    	else {
	    		nowPage--;
				pageCheck(nowPage);
				mainViewPager.setCurrentItem(nowPage);
				return false;
	    	}
	    }
	    
	    return super.onKeyDown(keyCode, event);
	}
    
    protected void setContentLog(final String contentType, final String productId, final String contentId, final String orgContentType, final String orgContentId) {
		
		Status.NETWORK = Util.checkNetworkStatus(this);
  		
  		if(Status.NETWORK != Status.NETWORK_NONE) {
		
			Runnable run = new Runnable() {
				@Override
				public void run() {
			        
			        try {
			        	
			        	String XMLURL = "http://www.samsungsupport.com/feed/rss/cares.jsp?type=CONTENT_LOG&siteCode=" + Status.SITECODE + "&userId=" + Status.USERID + "&contentType=" + contentType + "&productId=" + productId + "&contentId=" + contentId + "&orgContentType=" + orgContentType + "&orgContentId=" + orgContentId;
			        	//Logger.d(XMLURL);
	
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
	
    protected void showAlertDialog(String title) {

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
	
	protected void showResultDialog(String title, String message) {

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