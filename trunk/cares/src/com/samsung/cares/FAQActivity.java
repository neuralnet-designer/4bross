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

public class FAQActivity extends PageActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        listView.addFooterView(footerView, null, false);
        listView.setOnScrollListener(this);
		listView.setAdapter(pageAdapter);
        
        //if(CONTENTID.equals("")) {
        	addItems();
        //}
    }
	
	protected void viewPage(XMLData xmlData) {
		if(xmlData.subType.equals("product")) {
			Intent intent = new Intent(this, FAQActivity.class);
			intent.putExtra("xmlData", xmlData);
			startActivity(intent);
		}
		else {
			Intent intent = new Intent(this, FAQDetailActivity.class);
			xmlData.subType = "content_detail";
			intent.putExtra("xmlData", xmlData);
			startActivity(intent);
		}
	}
}