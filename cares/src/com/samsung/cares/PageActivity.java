package com.samsung.cares;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.samsung.cares.common.Status;
import com.samsung.cares.common.XMLData;
import com.samsung.cares.custom.CustomListView;
import com.samsung.cares.util.Logger;
import com.samsung.cares.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AbsListView;
import android.widget.Spinner;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemSelectedListener;

public class PageActivity extends Activity implements OnScrollListener {
    
	protected LinearLayout pageListView;
	protected CustomListView listView;
	
	protected HowToCategoryAdapter spinnerAdapter;
    protected PageAdapter pageAdapter;
    protected ProgressBar loadingProgressBar;
    
    protected ArrayList<XMLData> galleryList;
    protected ArrayList<XMLData> categoryList;
    protected ArrayList<XMLData> xmlDataList;    
    protected boolean lockListView;
    
    protected LayoutInflater inflater;   
    protected View headerView;
    protected View footerView;
    
    //protected LinearLayout footerButtonView;
    protected LinearLayout footerLoadingView;
    protected ImageButton homeButton;
	protected ImageButton backButton;
    
    protected ImageView logoView;
	protected LinearLayout contactLayout;
	protected LinearLayout categoryLayout;
    
    protected Spinner categorySpinner;
    private boolean initSpinner;
    
    protected String TYPE = "";
    protected String SUBTYPE = "";
    protected int LEVEL = 0;
    protected int COUNT = 0;
    
    protected String FACEBOOK_URL = "";
    protected String TWITTER_URL = "";
    protected String CHAT_URL = "";
    protected String CALL_NUMBER = "";
    protected String CALL_FULL_NUMBER = "";
    protected String CALL_YN = "";
    protected String WARRANTY_ID = "";
    protected String WARRANTY_URL = "";
    
    protected String PRODUCTID = "";
    
    protected String CONTENTID = "";
    
	protected String CHANNELID = "";
    protected String CATEGORYID = "";
	protected int PAGENO;    

	protected int ROWSPERPAGE;
	protected int PAGECOUNT;
	protected int TOTALCOUNT;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.page_list);
        
        galleryList = new ArrayList<XMLData>();
        categoryList = new ArrayList<XMLData>();
        xmlDataList = new ArrayList<XMLData>();        
        lockListView = true;
        
        pageListView = (LinearLayout)findViewById(R.id.page_list);           
        listView = (CustomListView)findViewById(R.id.list);
        pageAdapter = new PageAdapter(this, xmlDataList);
        
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
        //headerView = inflater.inflate(R.layout.page_list_header, null); 
        //headerView.setVisibility(View.VISIBLE);
        footerView = inflater.inflate(R.layout.page_list_footer, null); 
        //footerView.setVisibility(View.GONE);
        
        //footerButtonView = (LinearLayout)footerView.findViewById(R.id.footer_button);
        footerLoadingView = (LinearLayout)footerView.findViewById(R.id.footer_loading);
        
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
        
        listView.setOnItemClickListener(new OnItemClickListener() {
        	@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
        		
        		XMLData xmlData = (XMLData)listView.getItemAtPosition(position);
        		
        		//if(!xmlData.fileURL.equals("")) {     
        		if(!xmlData.fileURL.equals("") && xmlData.scheduleTime.equals("")) {
        			viewVideo(xmlData);
        		}
				else if(!xmlData.scheduleId.equals("")) {        		
        			viewDetail(xmlData);
        		}
        		else {
        			viewPage(xmlData);
        		}
			}    
        });
        
        loadingProgressBar = (ProgressBar)findViewById(R.id.loading_progress);
        loadingProgressBar.setVisibility(View.VISIBLE);
		
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
				
				if(TYPE.equals("CONTACT") || TYPE.equals("FAQ") || TYPE.equals("WARRANTY")) {
					
					PRODUCTID = xmlData.productId;
					
					if(SUBTYPE.equals("PRODUCT")) {
						if(LEVEL > 1 && COUNT == 0) {
							if(TYPE.equals("CONTACT") || TYPE.equals("FAQ")) {
								SUBTYPE = "CONTENT";
								if(TYPE.equals("CONTACT")) {
			            			FACEBOOK_URL = xmlData.facebookURL;
			            			TWITTER_URL = xmlData.twitterURL;
			            			CHAT_URL = xmlData.chatURL;
			            			CALL_NUMBER = xmlData.callNumber;
			            			CALL_FULL_NUMBER = xmlData.callFullNumber;
			            			CALL_YN = xmlData.callYN;
								}
							}
						}
						else {
							LEVEL++;
						}
					}
				}
				else if(TYPE.equals("HOWTO")) {				
					CHANNELID = xmlData.channelId;
					CATEGORYID = xmlData.categoryId;
				}
				
				setTitle(xmlData.channelTitle);
			}
		}
		
		contactLayout = (LinearLayout)findViewById(R.id.contact_layout);     
		contactLayout.setVisibility(View.GONE);
		
		categoryLayout = (LinearLayout)findViewById(R.id.category_layout);     
		categoryLayout.setVisibility(View.GONE);
		categorySpinner = (Spinner)findViewById(R.id.category_spinner);     
        //categorySpinner.setVisibility(View.GONE);
        spinnerAdapter = new HowToCategoryAdapter(this, categoryList);
        
        categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		if(initSpinner) {
	        		Logger.d("Spinner execute!!");
	        		XMLData xmlData = (XMLData)categoryList.get(position);
	        		CHANNELID = xmlData.channelId;
	        		CATEGORYID = xmlData.categoryId;
	        		Logger.d("CATEGORYID:"+CATEGORYID);
	        		loadingProgressBar.setVisibility(View.VISIBLE);
	        		//listView.addFooterView(footerView, null, false);
	        		//footerButtonView.setVisibility(View.GONE);
	        		footerLoadingView.setVisibility(View.GONE);
	        		clearItems();
	                addItems();
        		}
        		else {
        			initSpinner = true;
        		}
        	}
        	
        	public void onNothingSelected(AdapterView<?> arg0) {
        	
        	}
        });
        
        listView.addFooterView(footerView, null, false);
        //listView.setOnScrollListener(this);
        //listView.setAdapter(adapter);
        
        //addItems();
    }
	
	@Override
	public void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int count = totalItemCount - visibleItemCount;
		if(firstVisibleItem >= count && totalItemCount != 0 && PAGECOUNT > PAGENO && lockListView == false) {
			addItems();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	protected void viewMain() {

    	Intent intent = new Intent(this, MainActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    }
    
    protected void viewPage(XMLData xmlData) {
    	
    	Intent intent = new Intent(this, PageActivity.class);
    	intent.putExtra("xmlData", xmlData);
    	
    	startActivity(intent);
    	//overridePendingTransition(R.anim.fade, R.anim.hold);
    }

    protected void viewDetail(XMLData xmlData) {
    	
    	Intent intent = new Intent(this, HowToDetailActivity.class);
    	intent.putExtra("xmlData", xmlData);
    	
    	startActivity(intent);
    	//overridePendingTransition(R.anim.fade, R.anim.hold);
    }
    
    protected void viewVideo(XMLData xmlData) {
    	
    	Logger.d("PageActivity:viewVideo");
    	
    	Intent intent = new Intent(this, PlayerActivity.class);
    	
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
                | Intent.FLAG_ACTIVITY_CLEAR_TOP 
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);    	
    	intent.putExtra("xmlData", xmlData);
    	
    	Status.SCREEN = Status.SCREEN_ON;
    	
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    	startActivity(intent);
    	//overridePendingTransition(R.anim.fade, R.anim.hold);
    }
    
    @Override
    public void onDestroy() {
        pageAdapter.imageLoader.stopThread();
        listView.setAdapter(null);
        super.onDestroy();
    }
    
    protected void setHowToCategory() {
    	
    	Status.NETWORK = Util.checkNetworkStatus(this);
  		
  		if(Status.NETWORK == Status.NETWORK_NONE) {
  			showAlertDialog("Connection");
  		}
  		else {
		
			Runnable run = new Runnable() {
				@Override
				public void run() {
					
					String tag;
			        XMLData xmlData = null;
			        int selectedPosition = 0;
			        
			        try {
			        	
			        	String XMLURL = "http://www.samsungsupport.com/feed/rss/cares.jsp?type=HOWTO_CATEGORY&siteCode=" + Status.SITECODE + "&channelId=" + CHANNELID;
			        	//Logger.d(XMLURL);
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
			            		if(tag.equals("channelId")) {
			            			xmlData.channelId = xpp.nextText();		                      
			            		} 
			            		if(tag.equals("categoryId")) {
			            			xmlData.categoryId = xpp.nextText();
			            			if( !xmlData.channelId.equals("") && CHANNELID.equals(xmlData.channelId) &&
			            				!xmlData.categoryId.equals("") && CATEGORYID.equals(xmlData.categoryId)) {
			            				selectedPosition = index;
			            			}
			            		} 
			            		if(tag.equals("title")) {                      
			            			xmlData.title = xpp.nextText() ;
			            		}
			            		if(tag.equals("count")) {                      
			            			xmlData.count = xpp.nextText() ;
			            		}
			            	}
			            	else if(eventType == XmlPullParser.END_TAG) { 
			            		tag = xpp.getName();
			            		if(tag.equals("item")) {
			            			categoryList.add(xmlData);
			            			xmlData = null;
			            			index++;
			            		}
			            	}
			            	else if(eventType == XmlPullParser.TEXT) {
			            	} 
			            	
			            	eventType = xpp.next(); 
			            }
			            
			          //loadingProgressBar.setVisibility(View.GONE);
				        spinnerAdapter.notifyDataSetChanged();
				        pageListView.setVisibility(View.INVISIBLE);
				        if(selectedPosition > 0) {
				        	categorySpinner.setSelection(selectedPosition);
				        }
				        if(index > 1) { //there is no category, so layout should not be displayed.
					        categoryLayout.setVisibility(View.VISIBLE);
					        //categorySpinner.setVisibility(View.VISIBLE);
				        }
			        }
			        catch (Exception e) {
			        	Logger.d("Page - setHowToCategory - Exception");
			        	//e.printStackTrace();
			        }
				}
			};
			
			Handler handler = new Handler();
			handler.postDelayed(run, 1000);
  		}
	}
	
	protected void addItems() {
		
		Status.NETWORK = Util.checkNetworkStatus(this);
  		
  		if(Status.NETWORK == Status.NETWORK_NONE) {
  			showAlertDialog("Connection");
  		}
  		else {
  			
			lockListView = true;
			PAGENO++;
			
			Runnable run = new Runnable() {
				@Override
				public void run() {
					
					String tag;
			        XMLData xmlData = null;
			        
			        try {
	
			            String XMLURL = "http://www.samsungsupport.com/feed/rss/cares.jsp?type=" + TYPE + "&subType=" + SUBTYPE + "&level=" + LEVEL + "&siteCode=" + Status.SITECODE + "&version=" + Util.urlEncoder(Status.VERSION) + "&manufacturer=" + Util.urlEncoder(Status.MANUFACTURER) + "&model=" + Util.urlEncoder(Status.MODEL) + "&serial=" + Util.urlEncoder(Status.SERIAL) + "&phone=" + Util.urlEncoder(Status.PHONE) + "&email=" + Util.urlEncoder(Status.EMAIL) + "&pageNo=" + PAGENO + "&productId=" + PRODUCTID + "&channelId=" + CHANNELID + "&categoryId=" + CATEGORYID;
			            Logger.d(XMLURL);
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
			            			String strTotalCount = xpp.getAttributeValue(1);
			            			String strRowsPerPage = xpp.getAttributeValue(2);
			            			String strPageCount = xpp.getAttributeValue(3);
			                	  
			            			try {
			            				TOTALCOUNT = Integer.parseInt(strTotalCount);
			            			}
			            			catch(NumberFormatException nfe) {
			            			}
			                	  
			            			try {
			            				ROWSPERPAGE = Integer.parseInt(strRowsPerPage);
			            			}
			            			catch(NumberFormatException nfe) {
			            			}
			                	  
			            			try {
			            				PAGECOUNT = Integer.parseInt(strPageCount);
			            			}
			            			catch(NumberFormatException nfe) {
			            			}
			            			
			            			Logger.d("-------------------------------------");
			            			Logger.d("PAGENO : " + PAGENO);
			            			Logger.d("TOTALCOUNT : " + TOTALCOUNT);
			            			Logger.d("ROWSPERPAGE : " + ROWSPERPAGE);
			            			Logger.d("PAGECOUNT : " + PAGECOUNT);
			            		}    
			            		if(tag.equals("item")) {
			            			xmlData = new XMLData();
			            		} 
			            		
			            		if(tag.equals("type")) {
			            			xmlData.type = xpp.nextText();
			            		}
			            		if(tag.equals("subType")) {
			            			xmlData.subType = xpp.nextText();		                      
			            		}
			            		if(tag.equals("level")) {
			            			xmlData.level = xpp.nextText();		                      
			            		}  
			            		if(tag.equals("count")) {
			            			xmlData.count = xpp.nextText();		                      
			            		}
			            		
			            		if(tag.equals("orgType")) {
			            			xmlData.orgType = xpp.nextText();		                      
			            		}
			            		if(tag.equals("orgContentId")) {
			            			xmlData.orgContentId = xpp.nextText();		                      
			            		}
			            			
			            		if(tag.equals("productId")) {
			            			xmlData.productId = xpp.nextText();		                      
			            		} 
			            		if(tag.equals("productName")) {
			            			xmlData.productName = xpp.nextText();		                      
			            		} 
			            		if(tag.equals("productImage")) {
			            			xmlData.productImage = xpp.nextText();		                      
			            		} 
			            		if(tag.equals("facebookURL")) {
			            			xmlData.facebookURL = xpp.nextText();	
			            		}
			            		if(tag.equals("twitterURL")) {
			            			xmlData.twitterURL = xpp.nextText();	
			            		} 
			            		if(tag.equals("chatURL")) {
			            			xmlData.chatURL = xpp.nextText();
			            		} 
			            		if(tag.equals("callNumber")) {
			            			xmlData.callNumber = xpp.nextText();			            			
			            		} 
			            		if(tag.equals("callFullNumber")) {
			            			xmlData.callFullNumber = xpp.nextText();			            			
			            		} 
			            		if(tag.equals("callYN")) {
			            			xmlData.callYN = xpp.nextText();
			            		} 
			            		if(tag.equals("warrantyId")) {
			            			xmlData.warrantyId = xpp.nextText();
			            		} 
			            		if(tag.equals("warrantyURL")) {
			            			xmlData.warrantyURL = xpp.nextText();
			            		} 
			            		
			            		if(tag.equals("contentId")) {
			            			xmlData.contentId = xpp.nextText();		                      
			            		} 
			            		if(tag.equals("title")) {
			            			xmlData.title = xpp.nextText();		                      
			            		}
			            		if(tag.equals("contentURL")) {
			            			xmlData.contentURL = xpp.nextText();		                      
			            		}
			            		if(tag.equals("stepCount")) {
			            			xmlData.stepCount = xpp.nextText();		                      
			            		}
			            		
			            		if(tag.equals("channelGroup")) {
			            			xmlData.channelGroup = xpp.nextText();		                      
			            		} 
			            		if(tag.equals("channelTitle")) {
			            			xmlData.channelTitle = xpp.nextText();		                      
			            		} 
			            		if(tag.equals("channelId")) {
			            			xmlData.channelId = xpp.nextText();		                      
			            		} 
			            		if(tag.equals("categoryId")) {
			            			xmlData.categoryId = xpp.nextText();
			            		} 
			            		if(tag.equals("scheduleId")) {
			            			xmlData.scheduleId = xpp.nextText();
			            		}
			            		if(tag.equals("scheduleImage")) {
			            			xmlData.scheduleImage = xpp.nextText();
			            		}
			            		//if(tag.equals("title")) {                      
			            		//	xmlData.title = xpp.nextText() ;
			            		//}
			            		if(tag.equals("fileURL")) {
			            			xmlData.fileURL = xpp.nextText();
			            		}
			            		if(tag.equals("HQFileURL")) {
			            			xmlData.HQFileURL = xpp.nextText();
			            		}
			            		if(tag.equals("description")) {
			            			xmlData.description = xpp.nextText();
			            		}  
			            		if(tag.equals("JPG")) {
			            			xmlData.JPG = xpp.nextText();
			            		}  
			            		if(tag.equals("thumbnail")) {
			            			xmlData.thumbnail = xpp.nextText();
			            		}
			            		if(tag.equals("scheduleDate")) {
			            			/*
			            			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			            			Date date = new Date(xpp.nextText());
			            			String dateString = sdf.format(date);
			            			xmlData.scheduleDate = dateString;
			            			*/
			            			xmlData.scheduleDate = xpp.nextText();
			            		}
			            		if(tag.equals("scheduleTime")) {
			            			xmlData.scheduleTime = xpp.nextText();
			            		}
			            	}
			            	else if(eventType == XmlPullParser.END_TAG) { 
			            		tag = xpp.getName();
			            		if(tag.equals("item")) {
			            			xmlDataList.add(xmlData);
			            			xmlData = null;
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
			        	Logger.d("Page - addItems - Exception");
			        	showAlertDialog("Network");
			        	e.printStackTrace();
			        }
					
			        if(PAGENO == 1) {					
						loadingProgressBar.setVisibility(View.GONE);
						if(PAGECOUNT > PAGENO) {
							//footerView.setVisibility(View.VISIBLE);
							footerLoadingView.setVisibility(View.VISIBLE);
						}
						else {
							//listView.removeFooterView(footerView);
							footerLoadingView.setVisibility(View.GONE);
						}
					}
					else if(PAGECOUNT <= PAGENO) {
						//listView.removeFooterView(footerView);
						footerLoadingView.setVisibility(View.GONE);
					}
			        
			        //footerButtonView.setVisibility(View.VISIBLE);
					
			        pageAdapter.notifyDataSetChanged();
			        pageListView.setVisibility(View.VISIBLE);
					lockListView = false;
				}
			};
			
			Handler handler = new Handler();
			handler.postDelayed(run, 1000);
  		}
	}
	
	protected void clearItems() {
		PAGENO = 0;
		//footerView.setVisibility(View.GONE);
		xmlDataList.clear();
		pageAdapter.notifyDataSetChanged();
	}

    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	//Logger.d("keyCode ===" + keyCode + "////////// KeyEvent == " + event.toString());
	    
	    //4 : BACK
	    //82 : CONTROL
	    //84 : SEARCH
	    
	    /*
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
	    	Logger.d("event.KEYCODE_BACK");
	    	if(TYPE.equals("howto")) {
	    		showAlertDialog("Exit");
	    	}
	    }
	    */
	    
	    return super.onKeyDown(keyCode, event);
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
	
	protected void openBrowser(String url) {
    	
    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    	startActivity(intent);
    }
	
	protected void call(String phoneNumber) {
    	
    	Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
    	startActivity(intent);
    	//overridePendingTransition(R.anim.fade, R.anim.hold);
    }
}