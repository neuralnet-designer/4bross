package com.samsung.cares.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.samsung.cares.common.Status;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Util {
	
	private static final String EMAIL_PATTERN = 
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public static boolean isValidEmailAddress(String emailAddress) {
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(emailAddress);
    	return matcher.matches();
	}
	
	public static boolean isNumeric(String str) {  
	    return java.util.regex.Pattern.matches("\\d+", str);  
	}
	
	public static int checkNetworkStatus(Context context) {
	  
	    final ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
	    final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
	 
	    if(wifi.isAvailable()) {
	    	return Status.NETWORK_WIFI; 
	    } 
	    else if(mobile.isAvailable()) {
	    	return Status.NETWORK_3G; 
	    } 
	    else {
	    	return Status.NETWORK_NONE; 
	    }  
	}
	
	public static String urlEncoder(String url) {
		String encodedURL = url;
		try {
			if(encodedURL != null) {
				encodedURL = URLEncoder.encode(encodedURL, "UTF-8");
			}
			else {
				encodedURL = "";
			}
		}
		catch(UnsupportedEncodingException uee) {
		}
		return encodedURL;
	}
	
	public static String MD5_encode(String targetStr){
		byte[] defaultBytes = targetStr.getBytes();
		StringBuffer hexString = new StringBuffer();
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(defaultBytes);
			byte messageDigest[] = algorithm.digest();

			for (int i = 0; i < messageDigest.length; i++) {
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			}
		}
		catch(NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		}
		return hexString.toString();
	}

	public static int parseInt(String str, int defaultInt) {
		int resultInt = 0;
		try {
			resultInt = Integer.parseInt(str);
		}
		catch(NumberFormatException nfe) {
			resultInt = defaultInt;
		}
		return resultInt;
	}
}