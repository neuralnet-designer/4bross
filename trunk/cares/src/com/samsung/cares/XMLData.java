package com.samsung.cares;

import android.os.Parcel;
import android.os.Parcelable;

public class XMLData implements Parcelable {
	
	//for common
	public String type = "";
	public String subType = "";
	public String level = "";
	public String count = "";
	
	//for product category
	public String productId = "";
	public String productLevel = "";
	public String productName = "";
	public String productImage = "";
	public String facebookURL = "";
	public String twitterURL = "";
	public String chatURL = "";
	public String callNumber = "";
	public String callYN = "";
	public String lastYN = "";
	
	//for content
	public String contentId = "";
	public String contentTitle = "";
	public String content1URL = "";
	public String content2URL = "";
	public String content3URL = "";
	public String content4URL = "";
	public String content5URL = "";
	
	//for tracking
	public String ticketNo = "";
	public String phoneNo = "";
	public String company = "";
	public String serviceType = "";
	public String status = "";
	public String statusDesc = "";
	public String delayReason = "";
	public String delayReasonDesc = "";
	public String ascNo = "";
	public String ascName = "";
	public String ascPhone = "";
	public String postingDate = "";
	public String scheduleDate = "";
	public String completeDate = "";
	public String receiveDate = "";
	public String shipDate = "";
	public String trackingNo = "";
	public String trackingURL = "";
	public String receiptFileName = "";
	
	//for how-to video
	public String channelGroup = "";
	public String channelTitle = "";
	public String channelId = "";
	public String categoryId = "";
	public String scheduleId = "";
	public String scheduleImage = "";
	public String title = "";
	public String fileURL = "";
	public String HQFileURL = "";
	public String description = "";   	 	
	public String JPG = "";
	public String thumbnail = "";   	 	
	//public String scheduleDate = "";
	public String dayBetween = "";
	public String scheduleDay = "";
	public String scheduleTime = "";
	public String scheduleMonth = "";
	
	public XMLData() {
		
	}
	
	public XMLData(Parcel in) {
		
		//for common
		type = in.readString();
		subType = in.readString();
		level = in.readString();
		count = in.readString();

		//for product category
		productId = in.readString();
		productLevel = in.readString();
		productName = in.readString();
		productImage = in.readString();
		facebookURL = in.readString();
		twitterURL = in.readString();
		chatURL = in.readString();
		callNumber = in.readString();
		callYN = in.readString();
		lastYN = in.readString();
			
		//for content
		contentId = in.readString();
		contentTitle = in.readString();
		content1URL = in.readString();
		content2URL = in.readString();
		content3URL = in.readString();
		content4URL = in.readString();
		content5URL = in.readString();
			
		//for tracking
		ticketNo = in.readString();
		phoneNo = in.readString();
		company = in.readString();
		serviceType = in.readString();
		status = in.readString();
		statusDesc = in.readString();
		delayReason = in.readString();
		delayReasonDesc = in.readString();
		ascNo = in.readString();
		ascName = in.readString();
		ascPhone = in.readString();
		postingDate = in.readString();
		scheduleDate = in.readString();
		completeDate = in.readString();
		receiveDate = in.readString();
		shipDate = in.readString();
		trackingNo = in.readString();
		trackingURL = in.readString();
		receiptFileName = in.readString();
			
		//for how-to video
		channelGroup = in.readString();
		channelTitle = in.readString();
		channelId = in.readString();
		categoryId = in.readString();
		scheduleId = in.readString();
		scheduleImage = in.readString();
		title = in.readString();
		fileURL = in.readString();
		HQFileURL = in.readString();
		description = in.readString();   	 	
		JPG = in.readString();
		thumbnail = in.readString();   	 	
		//scheduleDate = in.readString();
		dayBetween = in.readString();
		scheduleDay = in.readString();
		scheduleTime = in.readString();
		scheduleMonth = in.readString();
	}

	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel out, int flags) {
		
		//for common
		out.writeString(type);
		out.writeString(subType);
		out.writeString(level);
		out.writeString(count);

		//for product category
		out.writeString(productId);
		out.writeString(productLevel);
		out.writeString(productName);
		out.writeString(productImage);
		out.writeString(facebookURL);
		out.writeString(twitterURL);
		out.writeString(chatURL);
		out.writeString(callNumber);
		out.writeString(callYN);
		out.writeString(lastYN);
			
		//for content
		out.writeString(contentId);
		out.writeString(contentTitle);
		out.writeString(content1URL);
		out.writeString(content2URL);
		out.writeString(content3URL);
		out.writeString(content4URL);
		out.writeString(content5URL);
			
		//for tracking
		out.writeString(ticketNo);
		out.writeString(phoneNo);
		out.writeString(company);
		out.writeString(serviceType);
		out.writeString(status);
		out.writeString(statusDesc);
		out.writeString(delayReason);
		out.writeString(delayReasonDesc);
		out.writeString(ascNo);
		out.writeString(ascName);
		out.writeString(ascPhone);
		out.writeString(postingDate);
		out.writeString(scheduleDate);
		out.writeString(completeDate);
		out.writeString(receiveDate);
		out.writeString(shipDate);
		out.writeString(trackingNo);
		out.writeString(trackingURL);
		out.writeString(receiptFileName);
			
		//for how-to video
		out.writeString(channelGroup);
		out.writeString(channelTitle);
		out.writeString(channelId);
		out.writeString(categoryId);
		out.writeString(scheduleId);
		out.writeString(scheduleImage);
		out.writeString(title);
		out.writeString(fileURL);
		out.writeString(HQFileURL);
		out.writeString(description);   	 	
		out.writeString(JPG);
		out.writeString(thumbnail);   	 	
		//out.writeString(scheduleDate);
		out.writeString(dayBetween);
		out.writeString(scheduleDay);
		out.writeString(scheduleTime);
		out.writeString(scheduleMonth);
	}
	
	public static final Parcelable.Creator<XMLData> CREATOR
		= new Parcelable.Creator<XMLData>() {
		public XMLData createFromParcel(Parcel in) {
			return new XMLData(in);
		}
		
		public XMLData[] newArray(int size) {
			return new XMLData[size];
		}
	};     
}
