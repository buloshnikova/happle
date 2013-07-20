package com.happle.gcmclient.config;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public abstract class CommonUtilities {
	// This is the project id generated from the Google console when defined a Google APIs project.
	public static final String SENDER_ID = "435848802389";
	public static final String PROJECT_ID = "r567pispopd";
	public static String REGISTRATION_ID = "REG_ID";

	// ======== WEBSERVICE URL=========== //
	public static final String URL_SEND_MESSAGE = "http://stranas.zapto.org/WebService.asmx/GetMessageFormAndroid";
	public static final String URL_REGISTER = "http://stranas.zapto.org/Registration.asmx/register";
	public static final String URL_LOGIN = "http://stranas.zapto.org/Registration.asmx/login";
	public static final String URL_TEST = "http://stranas.zapto.org/api.svc/json/GetMessageFormAndroid/";

	// ============ MESSAGES ================= //
	public static final String FIELD_MESSAGE = "Message";
	public static final int MESSAGE_ROOT = 0;
	public static final int REQUEST_CODE_ADD = 0;
	public static final int REQUEST_CODE_UPD = 1;
	
	// =============== DB ==================== //
	public static final String DATABASE_NAME = "happle.db";
	public static final String TBL_CONVERSATIONS = "tbl_conversations";
	public static final int DATABASE_VERSION = 1;
			
	// ============ SETTINGS ================= //
	public static final String PHONE_TYPE = "1"; // android - 1, ios - 2, webapp - 3, unknown - 0
	
	// ============ FILE NAMES =============== //
	public static final String PREFERENCES = "Preferences";
	
	// ============ STATUSES ============ //
	public static final String STATUS_ON = "1"; // user is online
	public static final String STATUS_OFF = "0"; // user is off-line
	public static final int SUCCESS = 0;
	public static final int FAILED = -1;
	public static final int MSG_STATUS_ACTIVE = 1; //1 - active, 0 - closed
	public static final int MSG_STATUS_CLOSED = 0;
	
	// ============= TEMP ============= //
	public static final String LANGUAGE_ID = "ENG";
	
	// ============ FILTER ACTION =========== //
	public static final String ACTION_ON_REGISTERED = "com.happle.asknshare.ON_REGISTERED";
	public static final String ACTION_ON_NEW_COMMENT = "com.happle.asknshare.ON_NEW_COMMENT";
	
	// ============ FUNCTIONS =============== //
	public static boolean isRegistrationNull() {
		if (REGISTRATION_ID == null || REGISTRATION_ID.equalsIgnoreCase("")) {
			return true;
		} else {
			return false;
		}
	}

	public static String generateGUID() {
		UUID uuid = UUID.randomUUID();
        return uuid.toString();
	}
	
	public static SharedPreferences sPref;
	public static boolean savePreverences(Context context, String prefName, String prefValue) {
		boolean result = false;
		sPref = context.getSharedPreferences(CommonUtilities.PREFERENCES, 0);
	    Editor ed = sPref.edit();
	    ed.putString(prefName, prefValue);
	    result = ed.commit();
		return result;
	}
	
	public static String pullPreferences (Context context, String prefName) {
		sPref = context.getSharedPreferences(CommonUtilities.PREFERENCES,0);
	    String savedText = sPref.getString(prefName, "");
	    return savedText;
	}
	
	public static boolean isRegistered(Context context) {
		boolean result = false;
		sPref = context.getSharedPreferences(CommonUtilities.PREFERENCES, 0);
	    String savedText = sPref.getString(CommonUtilities.REGISTRATION_ID, "");
	    if (savedText != "" && savedText != null)
	    	result = true;
	    return result;
	}
	
	private static String uniqueID = null;
	public static String generateGUID(Context context) {
		uniqueID = UUID.randomUUID().toString();
		return uniqueID;
	}
}
