package com.samsung.cares;

import com.samsung.cares.common.XMLData;
import com.samsung.cares.util.Logger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
                			LayoutInflater factory = LayoutInflater.from(ContentActivity.this);
                			final View contentLayout = factory.inflate(R.layout.content_alert, null);
                			TextView contentText = (TextView)contentLayout.findViewById(R.id.alert_text);
                			String text = getString(R.string.msg_contact_alert1) + " <font color=red><b>" + CALL_FULL_NUMBER + "**</b></font> " + getString(R.string.msg_contact_alert2);
                			contentText.setText(Html.fromHtml(text));
                			
                			AlertDialog.Builder builder = new AlertDialog.Builder(ContentActivity.this);
                			builder.setView(contentLayout);
                	        //builder.setTitle(getString(R.string.title_about) + Status.CURRENT_VERSION_NAME);
                	        //builder.setMessage("If you are calling in regards to the phone you are currently using, please call 1-855-775-4755** from an alternate device to that Samsung can provide you with the best troubleshooting experience.");
                	        //builder.setIcon(R.drawable.icon);
                	        builder.setPositiveButton(getString(R.string.button_call), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                	call(CALL_NUMBER);
                                }
                            });
                	        builder.setNegativeButton(getString(R.string.button_cancel), null);
                	        builder.show();
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
		else if(xmlData.type.equals("WARRANTY") && !xmlData.warrantyId.equals("") && !xmlData.warrantyURL.equals("")) {
			Intent intent = new Intent(this, ContentDetailActivity.class);
			xmlData.contentId = xmlData.warrantyId;
			xmlData.contentURL = xmlData.warrantyURL;
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