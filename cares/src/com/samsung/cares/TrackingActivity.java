package com.samsung.cares;

import com.samsung.cares.common.XMLData;
import com.samsung.cares.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TrackingActivity extends Activity {
	
	private EditText ticketNoText;
	private EditText phoneNoText;
	
	private ImageButton findButton;
	private ImageButton continueButton;
	
	private ImageButton homeButton;
	private ImageButton backButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.tracking);
        
        LinearLayout trackingLayout = (LinearLayout)findViewById(R.id.tracking_layout);
        trackingLayout.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		hideSoftKeyboard();
            }
        });
        
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        
        ticketNoText = (EditText)findViewById(R.id.ticketNo);
        imm.showSoftInput(ticketNoText, InputMethodManager.SHOW_IMPLICIT);
        
        phoneNoText = (EditText)findViewById(R.id.phoneNo);
        
        phoneNoText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if( actionId==EditorInfo.IME_ACTION_DONE || 
						event!=null && event.getKeyCode()==KeyEvent.KEYCODE_ENTER ) {
					continueButton.performClick();
				}				
				return false;
			}
		});
        
        //for testing
        ticketNoText.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	viewDetail("4114489516", "7346042229");
	        }
        });
        phoneNoText.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	viewDetail("4114491356", "5152560250");
	        }
        });
        //////////////////////
        
        findButton = (ImageButton)findViewById(R.id.find_button);
        continueButton = (ImageButton)findViewById(R.id.continue_button);
        homeButton = (ImageButton)findViewById(R.id.home_button);
        backButton = (ImageButton)findViewById(R.id.back_button);
        
        findButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	viewFind();
	        }
        });
        
        continueButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	String ticketNo = ticketNoText.getText().toString().trim();
	        	String phoneNo = phoneNoText.getText().toString().trim();
	        	if(ticketNo == "") {
	        		showResultDialog("Error", "Please input the Transaction Number.");
	        	}
	        	else if(ticketNo.length() != 10) {
	        		showResultDialog("Error", "Transaction Number must be valid.");
	        	}
	        	else if(phoneNo == "") {
	        		showResultDialog("Error", "Please input the Phone Number.");
	        	}
	        	else if(!Util.isNumeric(phoneNo) || phoneNo.length() != 10) {
	        		showResultDialog("Error", "Phone Number must be valid.");
	        	}
	        	else {
	        		viewDetail(ticketNo, phoneNo);
	        	}
	        }
	    });
        
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
    }
	
	private void viewFind() {
    	Intent intent = new Intent(this, TrackingFindActivity.class);
    	startActivity(intent);
    }
	
	private void viewDetail(String ticketNo, String phoneNo) {
    	//Intent intent = new Intent(this, ImageUploadActivity.class);
		Intent intent = new Intent(this, TrackingDetailActivity.class);
		XMLData xmlData = new XMLData();
    	xmlData.ticketNo = ticketNo;
    	xmlData.phoneNo = phoneNo;
    	intent.putExtra("xmlData", xmlData);
    	startActivity(intent);
    }
	
	private void viewMain() {
		Intent intent = new Intent(this, MainActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
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
	
	private void hideSoftKeyboard(){
        if(getCurrentFocus()!=null && getCurrentFocus() instanceof EditText){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(ticketNoText.getWindowToken(), 0);
        }
    }
}