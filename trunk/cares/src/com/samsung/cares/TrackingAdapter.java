package com.samsung.cares;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.samsung.cares.PageAdapter.HowToViewHolder;
import com.samsung.cares.common.XMLData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TrackingAdapter extends BaseAdapter {
    
	private Context context;
	private ArrayList<XMLData> xmlDataList;
	private LayoutInflater inflater = null;

	public TrackingAdapter(Context context, ArrayList<XMLData> xmlDataList) {
		this.context = context;
		this.xmlDataList = xmlDataList;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return xmlDataList.size();
	}

	public XMLData getItem(int position) {
		return(XMLData)xmlDataList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
   
	public static class TransactionHolder {
		public TextView ticketNo;
		public TextView modelCode;
		public TextView requestDate;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
    	View view = convertView;
    	XMLData xmlData = (XMLData)xmlDataList.get(position);
    	
    	view = inflater.inflate(R.layout.page_tracking_transaction, null);
		TransactionHolder holder = new TransactionHolder();
    	holder.ticketNo = (TextView)view.findViewById(R.id.ticketno);
    	holder.modelCode = (TextView)view.findViewById(R.id.modelcode);
    	holder.requestDate = (TextView)view.findViewById(R.id.requestDate);
    		
    	holder.ticketNo.setText(xmlData.ticketNo);
    	holder.modelCode.setText(xmlData.modelCode);
    	holder.requestDate.setText(xmlData.postingDate);
    	
    	view.setTag(holder);
    	
    	return view;
	}



}