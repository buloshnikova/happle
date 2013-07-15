package com.happle.gcmclient.config;

public abstract class Constants {
	// This is the project id generated from the Google console when
	// you defined a Google APIs project.
	public static final String PROJECT_NUMBER = "435848802389";
	public static final String PROJECT_ID = "r567pispopd";
	// This tag is used in Log.x() calls
	public static final String TAG = "MainActivity";

	// ======== WEBSERVICE URL===========/
	public static final String URL_GET_MESSAGE = "http://stranas.zapto.org/WebService.asmx/GetMessageFormAndroid";
	public static final String URL_REGISTER = "http://stranas.zapto.org/Registration.asmx/register";
	public static final String URL_TEST = "http://stranas.zapto.org/api.svc/json/GetMessageFormAndroid/";

	public static final String FIELD_MESSAGE = "Message";
	public static final String phoneType = "1"; // android - 1, ios - 2, windows
												// - 3, web app - 0
}
