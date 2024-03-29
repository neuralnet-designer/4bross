package com.samsung.cares;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.samsung.cares.common.Status;
import com.samsung.cares.util.Logger;
import com.samsung.cares.util.Util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;

public class LoadingActivity extends Activity {
	
	private ProgressBar loadingProgressBar;
	private int progressCount = 0;
	
	private boolean sentLog = false;
	private boolean forceStop = false;
	
	private static final String PACKAGE_NAME = "com.samsung.cares";
	private static final String MARKET_URI_PREFIX = "market://details?id=";
	//private static final String MARKET_REFERRER_SUFFIX = "&referrer=utm_source%3Dbarcodescanner%26utm_medium%3Dapps%26utm_campaign%3Dscan";
	private static final String MARKET_REFERRER_SUFFIX = "";
	
	private static int SLEEP_TIME = 50;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        
        Status.SITECODE = getSiteCode();
        Status.USERID = getUserId();
        Status.VERSION = Build.VERSION.RELEASE;
        Status.MANUFACTURER = Build.MANUFACTURER;
        Status.MODEL = Build.MODEL;
        /*
         * need android.permission.READ_PHONE_STATE
        TelephonyManager phoneManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        
        Status.SERIAL = phoneManager.getDeviceId();
        Status.PHONE = phoneManager.getLine1Number();
        */
        Status.NETWORK = Util.checkNetworkStatus(this);
        Status.DEVICE = isTabletDevice() ? Status.DEVICE_TABLET : Status.DEVICE_PHONE;
        
        DisplayMetrics metrics = new DisplayMetrics();    
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    
	    Status.DENSITY = metrics.densityDpi;
        
        try {
	        PackageInfo info = getPackageManager().getPackageInfo(PACKAGE_NAME, 0);
	        Status.CURRENT_VERSION_CODE = info.versionCode;
	        Status.CURRENT_VERSION_NAME = info.versionName;
        }
        catch(PackageManager.NameNotFoundException e) {
        }
        
        Account[] accounts = AccountManager.get(this).getAccounts();
        if(accounts.length > 0) {
        	for(int i = 0; i < accounts.length; i++) {
	        	if(Util.isValidEmailAddress(accounts[i].name)) {
	        		Status.EMAIL = accounts[i].name;
	        		break;
	        	}
        	}
        }
        
        /*
        Logger.d("VERSION : " + Status.VERSION);
        Logger.d("MANUFACTURER : " + Status.MANUFACTURER);
        Logger.d("MODEL : " + Status.MODEL);
        Logger.d("SERIAL : " + Status.SERIAL);
        Logger.d("PHONE : " + Status.PHONE);
        Logger.d("EMAIL : " + Status.EMAIL);
        Logger.d("NETWORK : " + Status.NETWORK);
        Logger.d("DEVICE : " + Status.DEVICE);
        Logger.d("DENSITY : " + Status.DENSITY);
        Logger.d("CURRENT_VERSION_CODE : " + Status.CURRENT_VERSION_CODE);
        Logger.d("CURRENT_VERSION_NAME : " + Status.CURRENT_VERSION_NAME);
        */
        
        Status.SCREEN = Status.SCREEN_ON;

        loadingProgressBar = (ProgressBar)findViewById(R.id.loading_progress);
        loadingProgressBar.setVisibility(View.INVISIBLE);
        
        Resources r = Resources.getSystem();
        Configuration config = r.getConfiguration();
        onConfigurationChanged(config);
        
        insertLog();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }

	@Override
	protected void onStart() {
		super.onStart();
		startProgress();
	}
    
	@Override
	protected void onResume() {  	
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
    protected void OnActivityResult(int requestCode, int resultCode, Intent data) {
    	
    }
    
    Handler activityHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		super.handleMessage(msg);
    		Logger.d("progressCount : " + progressCount);
    		if(progressCount >= SLEEP_TIME) {
    			if(sentLog) {
	    			loadingProgressBar.setVisibility(View.GONE);
	    			if(!isLastVersion()) {
	    				AlertDialog.Builder builder = new AlertDialog.Builder(LoadingActivity.this);
	    			    builder.setTitle(R.string.msg_install);
	    			    builder.setMessage(R.string.msg_install_new_version);
	    			    builder.setIcon(R.drawable.icon);
	    			    builder.setPositiveButton(R.string.button_update,
	    			    	new DialogInterface.OnClickListener() {
	    		                public void onClick(DialogInterface dialog, int which) {
	    		                	dialog.dismiss();
	    		                	launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URI_PREFIX + PACKAGE_NAME + MARKET_REFERRER_SUFFIX)));
	    		                }
	    		            }
	    			    );
	    			    builder.setNegativeButton(R.string.button_exit,
		    			   	new DialogInterface.OnClickListener() {
		    		            public void onClick(DialogInterface dialog, int which) {
		    		               	dialog.dismiss();
		    		               	finish();
		    		            }
		    		        }
		    			);
	    			    builder.show();
	    			    return;
	    			}
	    			else if(!forceStop) {
	    				Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
	    				startActivityForResult(intent, 10);
	    				finish();
	    			}
    			}
    			else {
    				SLEEP_TIME += 10;
        	        activityHandler.sendEmptyMessageDelayed(0, SLEEP_TIME);
    			}
    		}
    	    else {
    	    	if(progressCount == 20) {
    	    		loadingProgressBar.setVisibility(View.VISIBLE);
    	    	}
    	    	progressCount++;
    	        //progressBar.incrementProgressBy(2);
    	        //setProgress(progressBar.getProgress() * SLEEP_TIME);
    	        activityHandler.sendEmptyMessageDelayed(0, SLEEP_TIME);
    	    }
    	}
    };
    
    private void insertLog() {
    	
    	if(Status.NETWORK == Status.NETWORK_NONE) {
    		//Status.LAST_VERSION_CODE = Status.CURRENT_VERSION_CODE;
    		//sentLog = true;
    		forceStop = true;
    		showAlertDialog("Connection");
    	}
    	else {
		
			Runnable run = new Runnable() {
				@Override
				public void run() {
					
					String tag;
			        
			        try {
			        	
			        	//String XMLURL = "http://www.samsungsupport.com/feed/rss/cares.jsp?type=USER&siteCode=" + Status.SITECODE + "&version=" + Util.urlEncoder(Status.VERSION) + "&manufacturer=" + Util.urlEncoder(Status.MANUFACTURER) + "&model=" + Util.urlEncoder(Status.MODEL) + "&serial=" + Util.urlEncoder(Status.SERIAL) + "&phone=" + Util.urlEncoder(Status.PHONE) + "&email=" + Util.urlEncoder(Status.EMAIL);
			        	String XMLURL = "http://www.samsungsupport.com/feed/rss/cares.jsp?type=USER&siteCode=" + Status.SITECODE + "&userId=" + Status.USERID + "&manufacturer=" + Util.urlEncoder(Status.MANUFACTURER) + "&model=" + Util.urlEncoder(Status.MODEL) + "&version=" + Util.urlEncoder(Status.VERSION);
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
			                  
			            		if(tag.equals("channel")) {
			            			String strStatus = xpp.getAttributeValue(0);
			            			String strMessage = xpp.getAttributeValue(1);
			            		}
			            		if(tag.equals("userId")) {
			            			String strUserId = xpp.nextText();
			            			if(strUserId != null && !strUserId.equals(Status.USERID)) {
			            				setUserId(strUserId);
			            			}
			            		}
			            		if(tag.equals("lastVersionCode")) {
			            			String strLastVersion = xpp.nextText();
			            			try {
			            				Status.LAST_VERSION_CODE = Integer.parseInt(strLastVersion);
			            			}
			            			catch(NumberFormatException nfe) {
			            			}
			            		}
			            		if(tag.equals("privacyURL")) {
			            			Status.PRIVACY_URL = xpp.nextText();
			            		}
			            		if(tag.equals("legalURL")) {
			            			Status.LEGAL_URL = xpp.nextText();
			            		}
			            		if(tag.equals("aboutURL")) {
			            			Status.ABOUT_URL = xpp.nextText();
			            		}
			            		if(tag.equals("samsungURL")) {
			            			Status.SAMSUNG_URL = xpp.nextText();
			            		}
			            	}
			            	else if(eventType == XmlPullParser.END_TAG) { 
			            		tag = xpp.getName();
			            		isNetworkError = false;
			            	}
			            	else if(eventType == XmlPullParser.TEXT) {
			            	}
			            	
			            	eventType = xpp.next(); 
			            }
			            
			            if(isNetworkError) {
			            	forceStop = true;
			            	showAlertDialog("Network");
			            }
			        }
			        catch(Exception e) {
			        	forceStop = true;
			        	showAlertDialog("Network");
			        	e.printStackTrace();
			        }
			        
			        sentLog = true;
				}
			};
			
			Handler handler = new Handler();
			handler.postDelayed(run, 1000);
    	}
	}

    private void startProgress() {
    	activityHandler.sendEmptyMessage(SLEEP_TIME);    	
    }
    
    private boolean isLastVersion() {
    	if (Status.CURRENT_VERSION_CODE < Status.LAST_VERSION_CODE) {
    		return false;
        }
    	else {
    		return true;
    	}
    }
    
    private boolean isTabletDevice() {
    	if(Build.VERSION.SDK_INT >= 11) { // honeycomb
    		//test screen size, use reflection because isLayoutSizeAtLeast is
    		//only available since 11
    	    Configuration con = getResources().getConfiguration();
    	    try {
    	    	Method mIsLayoutSizeAtLeast = con.getClass().getMethod(
    	    			"isLayoutSizeAtLeast", int.class);
    	    	boolean r = (Boolean) mIsLayoutSizeAtLeast.invoke(con, 0x00000004); // Configuration.SCREENLAYOUT_SIZE_XLARGE
    	    	return r;
    	    }
    	    catch (Exception e) {
    	    	e.printStackTrace();
    	    	return false;
    	    }
    	}
    	
    	return false;
    }
    
    private String getSiteCode() {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	return prefs.getString("SAMSUNG_CARES_SITECODE", "us");
    }
    
    private void setSiteCode(String siteCode) {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit().putString("SAMSUNG_CARES_SITECODE", siteCode).commit();
		Status.SITECODE = siteCode;
    }
    
    private String getUserId() {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	return prefs.getString("SAMSUNG_CARES_USERID", "");
    }
    
    private void setUserId(String userId) {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit().putString("SAMSUNG_CARES_USERID", userId).commit();
		Status.USERID = userId;
    }
    
    private void launchIntent(Intent intent) {
        if (intent != null) {
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
          //Log.d(TAG, "Launching intent: " + intent + " with extras: " + intent.getExtras());
          try {
            startActivity(intent);
          } catch (ActivityNotFoundException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.msg_intent_failed);
            builder.setPositiveButton(R.string.button_ok, null);
            builder.show();
          }
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
    
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	    	//showAlertDialog("Exit");
	    	//return false;
	    	forceStop = true;
	    	finish();
	    }
	    else if(keyCode == KeyEvent.KEYCODE_SEARCH) {
	        return true;
	    }
	    
	    return super.onKeyDown(keyCode, event);
	}
}
