package com.samsung.cares;

import com.samsung.cares.common.XMLData;
import com.samsung.cares.util.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.samsung.cares.util.Util;

public class TrackingFindActivity extends Activity {
	
	private EditText homePhoneText;
	private EditText firstNameText;
	private EditText lastNameText;
	private EditText zipCodeText;
	
	private ImageButton homeButton;
	private ImageButton backButton;
	private ImageButton continueButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.tracking_find);
        
        LinearLayout trackingLayout = (LinearLayout)findViewById(R.id.tracking_layout);
        trackingLayout.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		hideSoftKeyboard();
            }
        });
        
        homePhoneText = (EditText)findViewById(R.id.phoneNo);
        firstNameText = (EditText)findViewById(R.id.firstName);
        lastNameText = (EditText)findViewById(R.id.lastName);
        zipCodeText = (EditText)findViewById(R.id.zipCode);
        
        //for testing
        /*
        homePhoneText.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	findTickets("9206993358", "matt", "wilson", "53549");
	        }
        });
        */
        //////////////////////
        
        homeButton = (ImageButton)findViewById(R.id.home_button);
        backButton = (ImageButton)findViewById(R.id.back_button);
        continueButton = (ImageButton)findViewById(R.id.continue_button);
        
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
        
        continueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
	        	String phoneNo = homePhoneText.getText().toString().trim();
	        	String firstName = firstNameText.getText().toString().trim();
	        	String lastName = lastNameText.getText().toString().trim();
	        	String zipCode = zipCodeText.getText().toString().trim();

	        	if( phoneNo.equals("") ) {
	        		showResultDialog("Error", "Please input Home Phone Number.");
	        	} else if(!Util.isNumeric(phoneNo) || phoneNo.length() != 10) {
		        	showResultDialog("Error", "Home Phone Number must be valid.");
	        	} else if( firstName.equals("") ) {
	        		showResultDialog("Error", "Please input First Name.");
	        	} else if( lastName.equals("") ) {
	        		showResultDialog("Error", "Please input Last Name.");
	        	} else if( zipCode.equals("") ){
	        		showResultDialog("Error", "Please input Zip Code.");
	        	} else {
	        		findTickets(phoneNo, firstName, lastName, zipCode);
	        	}		
			}
		});
    }
	
	private void viewMain() {
		Intent intent = new Intent(this, MainActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    }
	
	private void findTickets(String homePhoneNo, String firstName, String lastName, String zipCode) {
		Intent intent = new Intent(this, TrackingResultActivity.class);
		XMLData xmlData = new XMLData();
    	xmlData.phoneNo = homePhoneNo;
    	xmlData.firstName = firstName;
    	xmlData.lastName = lastName;
    	xmlData.zipCode = zipCode;
    	intent.putExtra("xmlData", xmlData);
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
            imm.hideSoftInputFromWindow(homePhoneText.getWindowToken(), 0);
        }
    }
}