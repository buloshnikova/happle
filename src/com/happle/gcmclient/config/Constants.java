package com.happle.gcmclient.config;

public abstract class Constants {
	// This is the project id generated from the Google console when
	// you defined a Google APIs project.
	public static final String PROJECT_NUMBER = "435848802389";
	public static final String PROJECT_ID = "r567pispopd";
	// This tag is used in Log.x() calls
	public static final String TAG = "MainActivity";

	// ======== WEBSERVICE URL===========/
	public static final String URL_SEND_MESSAGE = "http://stranas.zapto.org/WebService.asmx/GetMessageFormAndroid";
	public static final String URL_REGISTER = "http://stranas.zapto.org/Registration.asmx/register";
	public static final String URL_LOGIN = "http://stranas.zapto.org/Registration.asmx/login";
	public static final String URL_TEST = "http://stranas.zapto.org/api.svc/json/GetMessageFormAndroid/";

	// ============ MESSAGES =================//
	public static final String FIELD_MESSAGE = "Message";
	public static final int MESSAGE_ROOT = 0;
	
	// ============ SETTINGS =================//
	public static final String PHONE_TYPE = "1"; // android - 1, ios - 2, webapp - 3, unknown - 0
	
	// ============ STATUSES ============ //
	public static final String STATUS_ON = "1"; // user is online
	public static final String STATUS_OFF = "0"; // user is off-line
	public static final int SUCCESS = 0;
	public static final int FAILED = -1;
	public static final int MSG_STATUS_ACTIVE = 1; //1 - active, 0 - closed
	public static final int MSG_STATUS_CLOSED = 0;
	
	// ============= TEMP ============= //
	public static final String LANGUAGE_ID = "ENG";
}
