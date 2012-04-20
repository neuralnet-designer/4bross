package com.samsung.cares;

import com.samsung.cares.common.XMLData;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ContentActivity extends PageActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //listView.addFooterView(footerView, null, false);
        listView.setOnScrollListener(this);
		listView.setAdapter(pageAdapter);
        
        addItems();
        if(TYPE.equals("CONTACT") && SUBTYPE.equals("CONTENT")) {
           	contactLayout.setVisibility(View.VISIBLE);
            	
           	ImageView facebookView = (ImageView)findViewById(R.id.facebook);
        	ImageView twitterView = (ImageView)findViewById(R.id.twitter);
        	ImageView chatView = (ImageView)findViewById(R.id.chat);
        	ImageView callView = (ImageView)findViewById(R.id.call);
        		
        	facebookView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	if(FACEBOOK_URL != null && !FACEBOOK_URL.equals("")) {
                		//insert log
           	        	openBrowser(FACEBOOK_URL);
                	}
                }
            });
        		
        	twitterView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	if(TWITTER_URL != null && !TWITTER_URL.equals("")) {
                		//insert log
           	        	openBrowser(TWITTER_URL);
                	}
                }
            });
        		
        	chatView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	if(CHAT_URL != null && !CHAT_URL.equals("")) {
                		//insert log
           	        	openBrowser(CHAT_URL);
                	}
                }
            });
        		
        	callView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	if(CALL_NUMBER != null && !CALL_NUMBER.equals("")) {
                		if(CALL_YN != null && CALL_YN.equals("Y")) {
		       	        	//insert log
		       	        	call(CALL_NUMBER);
                		}
                		else {
                			//show intent
                		}
                	}
                }
            });
        }
    }
	
	protected void viewPage(XMLData xmlData) {
		if(!xmlData.contentId.equals("") && !xmlData.contentURL.equals("")) {
			Intent intent = new Intent(this, ContentDetailActivity.class);
			intent.putExtra("xmlData", xmlData);
			startActivity(intent);
		}
		else {
			Intent intent = new Intent(this, ContentActivity.class);
			intent.putExtra("xmlData", xmlData);
			startActivity(intent);
		}
	}
}