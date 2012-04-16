package com.samsung.cares;

import java.lang.reflect.Method;
import java.util.Calendar;

import com.samsung.cares.common.Status;
import com.samsung.cares.common.XMLData;
import com.samsung.cares.util.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
			else if(id == R.id.more) {
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
    			
    			ImageView contactView = (ImageView)view.findViewById(R.id.contact);
    			ImageView howToView = (ImageView)view.findViewById(R.id.how_to);
    			ImageView faqView = (ImageView)view.findViewById(R.id.faq);
    			ImageView warrantyView = (ImageView)view.findViewById(R.id.warranty);
    			ImageView trackingView = (ImageView)view.findViewById(R.id.tracking);
    			ImageView moreView = (ImageView)view.findViewById(R.id.more);
    			
    			TextView contactTextView = (TextView)view.findViewById(R.id.contact_text);
    			TextView howToTextView = (TextView)view.findViewById(R.id.how_to_text);
    			TextView faqTextView = (TextView)view.findViewById(R.id.faq_text);
    			TextView warrantyTextView = (TextView)view.findViewById(R.id.warranty_text);
    			TextView trackingTextView = (TextView)view.findViewById(R.id.tracking_text);
    			TextView moreTextView = (TextView)view.findViewById(R.id.more_text);
    			
    			contactView.setOnClickListener(contactListener);
    			howToView.setOnClickListener(howToListener);
    			faqView.setOnClickListener(faqListener);
    			warrantyView.setOnClickListener(warrantyListener);
    			trackingView.setOnClickListener(trackingListener);
    			moreView.setOnClickListener(pageListener);
    			
    			contactTextView.setOnClickListener(contactListener);
    			howToTextView.setOnClickListener(howToListener);
    			faqTextView.setOnClickListener(faqListener);
    			warrantyTextView.setOnClickListener(warrantyListener);
    			trackingTextView.setOnClickListener(trackingListener);
    			moreTextView.setOnClickListener(pageListener);
            }
    		else if(position == 1){
               	view = View.inflate(context, R.layout.main_more, null);
               	
               	ImageView privacyView = (ImageView)view.findViewById(R.id.privacy);
               	ImageView legalView = (ImageView)view.findViewById(R.id.legal);
               	ImageView aboutView = (ImageView)view.findViewById(R.id.about);
               	ImageView samsungView = (ImageView)view.findViewById(R.id.samsung);
               	
               	TextView privacyTextView = (TextView)view.findViewById(R.id.privacy_text);
               	TextView legalTextView = (TextView)view.findViewById(R.id.legal_text);
               	TextView aboutTextView = (TextView)view.findViewById(R.id.about_text);
               	TextView samsungTextView = (TextView)view.findViewById(R.id.samsung_text);
               	
               	privacyView.setOnClickListener(privacyListener);
               	legalView.setOnClickListener(legalListener);
               	aboutView.setOnClickListener(aboutListener);
               	samsungView.setOnClickListener(samsungListener);
               	
               	privacyTextView.setOnClickListener(privacyListener);
               	legalTextView.setOnClickListener(legalListener);
               	aboutTextView.setOnClickListener(aboutListener);
               	samsungTextView.setOnClickListener(samsungListener);
               	
               	view.findViewById(R.id.back_button).setOnClickListener(pageListener);
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
	
	protected void showAlertDialog(String title) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(title);
        //alertDialog.setTitle("Attention");
        
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
        if(title.equals("Exit")) {
        	alertDialog.setMessage("Are you sure you want to exit the application?");
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