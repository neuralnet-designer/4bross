package com.samsung.cares;

import com.samsung.cares.common.Status;
import com.samsung.cares.common.XMLData;

import android.content.Intent;
import android.os.Bundle;

public class HowToActivity extends PageActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) { //HONEYCOMB
        if(Status.DEVICE == Status.DEVICE_TABLET) {
        	//logoView.setBackgroundDrawable(getResources().getDrawable(R.drawable.logo_bg_1280));
		}
        
        if(!CHANNELID.equals("")) {
	        categorySpinner.setAdapter(spinnerAdapter);	        
	        //listView.addFooterView(footerView, null, false);
	        listView.setOnScrollListener(this);
	        
	        setHowToCategory();
        }
        
		listView.setAdapter(pageAdapter);
        
        if(CATEGORYID.equals("")) {
        	addItems();
        }
    }
	
	protected void viewDetail(XMLData xmlData) {
		
		Intent intent = new Intent(this, HowToDetailActivity.class);
		intent.putExtra("xmlData", xmlData);
        startActivity(intent);
	}
	
	protected void viewPage(XMLData xmlData) {
		
		Intent intent = new Intent(this, HowToActivity.class);
		intent.putExtra("xmlData", xmlData);
        startActivity(intent);
	}
}