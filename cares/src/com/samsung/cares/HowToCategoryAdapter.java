package com.samsung.cares;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.samsung.cares.common.XMLData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HowToCategoryAdapter extends BaseAdapter {
    
	private Context context;
	private ArrayList<XMLData> xmlDataList;
	private LayoutInflater inflater = null;

	public HowToCategoryAdapter(Context context, ArrayList<XMLData> xmlDataList) {
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
   
	public static class ViewHolder {
		public TextView text;
		public ImageView icon;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return createResource(position, convertView, parent, android.R.layout.simple_spinner_item);
		//return createResource(position, convertView, parent,R.layout.spinner_item);
	}

	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return createResource(position, convertView, parent, android.R.layout.simple_spinner_dropdown_item);
	}

	public View createResource(int position, View convertView, ViewGroup parent, int resource) {
		View view = convertView;
		ViewHolder holder;
		if(view == null) {
			view = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.text = (TextView)view.findViewById(android.R.id.text1);
            view.setTag(holder);
		} 
		else {
			holder = (ViewHolder) view.getTag();
		}
		
		if(!getItem(position).count.equals("")) {
			holder.text.setText(delHtmlTag(getItem(position).title) + " (" + getItem(position).count + ")");
		}
		else {
			holder.text.setText(delHtmlTag(getItem(position).title));
		}
		return view;
	}
	
	private String delHtmlTag(String param){
		Pattern p = Pattern.compile("\\<(\\/?)(\\w+)*([^<>]*)>");
		Matcher m = p.matcher(param);
		param = m.replaceAll("").trim();
		return param;
	}
}