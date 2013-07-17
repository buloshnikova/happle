package com.happle.gcmclient.utility;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.happle.gcmclient.config.Constants;

public final class ParameterUtil {

	 public static  List<NameValuePair> createLoginRegistrationParams(String registration_id, String username, String user_pwd) {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("registration_id", registration_id));
			parameters.add(new BasicNameValuePair("phone_type", Constants.PHONE_TYPE));
			parameters.add(new BasicNameValuePair("username", username));
			parameters.add(new BasicNameValuePair("user_pwd", user_pwd));
		 	parameters.add(new BasicNameValuePair("user_status", Constants.STATUS_ON));
			return parameters;
		}
	 
	 public static List<NameValuePair> CreateSendMessageParams (String msg_id, String wave_id, int msg_order, String lng_id, String senderID, 
			 String message ,int msg_status) {
		 List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		 parameters.add(new BasicNameValuePair("msg_id", msg_id));
		 parameters.add(new BasicNameValuePair("wave_id", wave_id));
		 parameters.add(new BasicNameValuePair("msg_order", String.valueOf(msg_order)));
		 parameters.add(new BasicNameValuePair("lng_id", lng_id));
		 parameters.add(new BasicNameValuePair("user_id", senderID));
		 parameters.add(new BasicNameValuePair("message", message));
		 parameters.add(new BasicNameValuePair("datetime",String.valueOf(System.currentTimeMillis())));
		 parameters.add(new BasicNameValuePair("int msg_status", String.valueOf(msg_status)));
		 return parameters;
	 }
}
