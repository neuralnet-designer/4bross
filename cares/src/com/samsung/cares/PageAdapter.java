package com.samsung.cares;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.samsung.cares.common.XMLData;
import com.samsung.cares.util.ImageLoader;
import com.samsung.cares.util.Logger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.webkit.WebView;

public class PageAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<XMLData> xmlDataList;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader; 
    
    public PageAdapter(Activity activity, ArrayList<XMLData> xmlDataList) {
        this.activity = activity;
        this.xmlDataList = xmlDataList;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext(), R.drawable.thumbnail_bg);
    }

    public int getCount() {
        return xmlDataList.size();
    }

    public XMLData getItem(int position) {
        return (XMLData)xmlDataList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public static class ProductViewHolder {
    	public ImageView productImage;
        public TextView productName;
    }
    
    public static class HowToViewHolder {
    	public ImageView thumbnail;
        public TextView title;
        public TextView count;
        public TextView description;
        public TextView scheduleDate;
        public ImageView info;
        public ImageView scheduleImage;
        public TextView scheduleDay;
        public TextView scheduleTime;
        public ImageView remindButton;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        
    	View vi = convertView;
    	XMLData xmlData = (XMLData)xmlDataList.get(position);
    	
    	Logger.d("--------------------------");
    	Logger.d("[" + position + "] type : " + xmlData.type);
    	Logger.d("[" + position + "] orgType : " + xmlData.orgType);
    	Logger.d("[" + position + "] subType : " + xmlData.subType);
    	Logger.d("[" + position + "] contentId : " + xmlData.contentId);
    	Logger.d("[" + position + "] scheduleId : " + xmlData.scheduleId);
    	
    	if(xmlData.type.equals("HOWTO")) {
    		
    		if(xmlData.scheduleId.equals("")) {
    		
	    		vi = inflater.inflate(R.layout.page_list_title, null);
		    	HowToViewHolder holder = new HowToViewHolder();
		    	holder.title = (TextView)vi.findViewById(R.id.title);
		    	holder.count = (TextView)vi.findViewById(R.id.count);
		    	vi.setTag(holder);
		    		
		    	holder.title.setText(delHtmlTag(xmlData.title));
		    	if(!xmlData.count.equals("")) {
		    		holder.count.setText("(" + delHtmlTag(xmlData.count) + ")");
		    	}
    		}
    		else {
    	
		    	vi = inflater.inflate(R.layout.page_list_thumbnail, null);
		    			
		    	HowToViewHolder holder = new HowToViewHolder();
		    	holder.thumbnail = (ImageView)vi.findViewById(R.id.thumbnail);
		    	holder.scheduleDate = (TextView)vi.findViewById(R.id.schedule_date); 
		    			
		    	holder.thumbnail.setTag(xmlData.thumbnail);    			
		    	if(!xmlData.thumbnail.equals("")) {
		    		//holder.thumbnail.setBackgroundResource(R.drawable.border_thumbnail_selected);
		    	   	imageLoader.displayImage(xmlData.thumbnail, activity, holder.thumbnail);
		    	}
		    	holder.scheduleDate.setText(delHtmlTag(xmlData.scheduleDate));
		    	holder.info = (ImageView)vi.findViewById(R.id.info);
		    			
		    	holder.info.setOnClickListener(new View.OnClickListener() {
		    		public void onClick(View v) {
		    			viewDetail((XMLData)xmlDataList.get(position));
		    	    }
		    	});
		    		
			    holder.title = (TextView)vi.findViewById(R.id.title);
			    holder.description = (TextView)vi.findViewById(R.id.description);
			        
				holder.title.setText(delHtmlTag(xmlData.title));
				holder.description.setText(delHtmlTag(xmlData.description));
				  
			    vi.setTag(holder);
    		}
    	}
    	else {
    		
    		if(xmlData.subType.equals("PRODUCT")) {
    		
	    		vi = inflater.inflate(R.layout.page_list_product, null);
					
		    	ProductViewHolder holder = new ProductViewHolder();
		    	holder.productImage = (ImageView)vi.findViewById(R.id.thumbnail);
		    	holder.productName = (TextView)vi.findViewById(R.id.title); 
		    			
		    	holder.productImage.setTag(xmlData.productImage);    			
		    	if(!xmlData.productImage.equals("")) {
		    		//holder.thumbnail.setBackgroundResource(R.drawable.border_thumbnail_selected);
		    	   	imageLoader.displayImage(xmlData.productImage, activity, holder.productImage);
		    	}
		    		
			    holder.productName.setText(delHtmlTag(xmlData.productName));
				    
				vi.setTag(holder);
    		}
    		else {
    			
    			vi = inflater.inflate(R.layout.page_list_faq, null);
				
		    	ProductViewHolder holder = new ProductViewHolder();
		    	holder.productName = (TextView)vi.findViewById(R.id.title); 
				holder.productName.setText(delHtmlTag(xmlData.title));
				    
				vi.setTag(holder);
    		}
    	}
        
        return vi;
    }
    
    private void viewDetail(XMLData xmlData) {
    	
    	Intent intent = new Intent(activity, HowToDetailActivity.class);
    	intent.putExtra("xmlData", xmlData);
    	
    	activity.startActivity(intent);
    	activity.overridePendingTransition(R.anim.fade, R.anim.hold);
    }
    
    private String delHtmlTag(String param){
 	   Pattern p = Pattern.compile("\\<(\\/?)(\\w+)*([^<>]*)>");
 	   Matcher m = p.matcher(param);
 	   param = m.replaceAll("").trim();
 	   return param;
    } 
}