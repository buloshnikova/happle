package com.happle.gcmclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.happle.gcmclient.R;
import com.happle.gcmclient.backendmanager.BackendManager;
import com.happle.gcmclient.config.CommonUtilities;
import com.happle.gcmclient.utility.AlertDialogManager;
import com.happle.gcmclient.utility.NetworkUtility;

public class MainActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	private String regId = "";
	
	SharedPreferences sPref;

	// This intent filter will be set to filter on the string "GCM_RECEIVED_ACTION"
	GCMReceiver mGCMReceiver;
	IntentFilter registeredFilter;
	// Alert dialog manager
	private AlertDialogManager alert = new AlertDialogManager();

	TextView tvRegID;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Check if Internet connection
		if (!NetworkUtility.getInstance(MainActivity.this).isNetworkAvailable()) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}		
		initUI();
		
		if (CommonUtilities.SENDER_ID == null) {
			Log.d(TAG, "Missing SENDER_ID");
			return;
		}

		registeredFilter.addAction("GCM_RECEIVED_ACTION");
	}

	private void initUI() {
		findViewById(R.id.btnAsk).setOnClickListener(askListener);
		if (isRegistered()) {
			regId = getPreferences(CommonUtilities.REGISTRATION_ID);
		} else { 
			registerClient(); }
		tvRegID = (TextView) findViewById(R.id.tv_registration_id);
		tvRegID.setText(regId);	
	}
	
	private OnClickListener askListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(MainActivity.this, MessageActivity.class);
			startActivityForResult(intent, CommonUtilities.REQUEST_CODE_ADD);
			intent.putExtra("REG_ID", regId);
			intent.putExtra("REQUEST_CODE", String.valueOf(CommonUtilities.REQUEST_CODE_UPD));
			startActivityForResult(intent, CommonUtilities.REQUEST_CODE_UPD);
		}
	};
	
	public void registerClient() {
		try {
			// Check that the device supports GCM (should be in a try / catch)
			GCMRegistrar.checkDevice(this);
			// Check the manifest to be sure this app has all the required permissions.
			GCMRegistrar.checkManifest(this);
			// Get the existing registration id, if it exists.
			regId = GCMRegistrar.getRegistrationId(this);

			if (regId.equals("")) {
				// register this device for this project
				GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
				regId = GCMRegistrar.getRegistrationId(this);
			}
			savePreverences(CommonUtilities.REGISTRATION_ID, regId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d(TAG, regId);
	}

	private boolean savePreverences(String prefName, String prefValue) {
		boolean result = false;
		sPref = getSharedPreferences("UtilPref", MODE_PRIVATE);
	    Editor ed = sPref.edit();
	    ed.putString(prefName, prefValue);
	    ed.commit();
		return result;
	}
	
	private String getPreferences (String prefName) {
		sPref = getPreferences(MODE_PRIVATE);
	    String savedText = sPref.getString(prefName, "");
	    return savedText;
	}
	private boolean isRegistered() {
		boolean result = false;
		sPref = getPreferences(MODE_PRIVATE);
	    String savedText = sPref.getString(CommonUtilities.REGISTRATION_ID, "");
	    if (savedText != "" && savedText != null)
	    	result = true;
	    return result;
	}
	

	// If our activity is paused, it is important to UN-register any
	// broadcast receivers.
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGCMReceiver);
	}

	// When an activity is resumed, be sure to register any
	// broadcast receivers with the appropriate intent
	@Override
	protected void onResume() {
		super.onResume();
		if (!NetworkUtility.getInstance(MainActivity.this).isNetworkAvailable()) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		registerReceiver(mGCMReceiver, registeredFilter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	// NOTE the call to GCMRegistrar.onDestroy()
	@Override
	public void onDestroy() {
		GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}


	private class GCMReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			
		}
	};
}