package com.samsung.cares;

import java.lang.reflect.Method;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;

public class WarrantyActivity extends PageActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /*
        if(TYPE.equals("channel")) {
	        categorySpinner.setAdapter(spinnerAdapter);	        
	        listView.addFooterView(footerView, null, false);
	        listView.setOnScrollListener(this);
	        
	        setCategory();
        }
        */
        
		listView.setAdapter(pageAdapter);
        
        //if(PRODUCTGROUPID.equals("") || PRODUCTTYPEID.equals("")) {
        	addItems();
        //}
    }
	
	protected void viewPage(XMLData xmlData) {
		if(xmlData.subType.equals("product")) {
			Intent intent = new Intent(this, WarrantyActivity.class);
			intent.putExtra("xmlData", xmlData);
			startActivity(intent);
		}
		else {
			Intent intent = new Intent(this, WarrantyDetailActivity.class);
			xmlData.subType = "content_detail";
			intent.putExtra("xmlData", xmlData);
			startActivity(intent);
		}
	}
}