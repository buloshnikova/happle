package com.happle.gcmclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.happle.gcmclient.R;
import com.happle.gcmclient.config.CommonUtilities;
import com.happle.gcmclient.utility.AlertDialogManager;
import com.happle.gcmclient.utility.NetworkUtility;

public class MainActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	private String regId = "";
	
	SharedPreferences sPref;

	// This intent filter will be set to filter on the string "GCM_RECEIVED_ACTION"
	IntentFilter gcmFilter;
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
	}

	private void initUI() {
		findViewById(R.id.btnAsk).setOnClickListener(askListener);
		gcmFilter = new IntentFilter();
		gcmFilter.addAction("GCM_RECEIVED_ACTION");
		if (CommonUtilities.isRegistered(this)) {
			regId = CommonUtilities.pullPreferences(this,CommonUtilities.REGISTRATION_ID);
		} else { 
			registerClient(); }
		tvRegID = (TextView) findViewById(R.id.tv_registration_id);
		tvRegID.setText(regId);	
	}
	
	private OnClickListener askListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(MainActivity.this, MessageActivity.class);
			intent.putExtra("REG_ID", regId);
			intent.putExtra("REQUEST_CODE", String.valueOf(CommonUtilities.REQUEST_CODE_UPD));
			startActivity(intent);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d(TAG, regId);
	}

	// If our activity is paused, it is important to UN-register any
	// broadcast receivers.
	@Override
	protected void onPause() {
		unregisterReceiver(gcmReceiver);
		super.onPause();
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
		registerReceiver(gcmReceiver, gcmFilter);

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


	// A BroadcastReceiver must override the onReceive() event.
		private BroadcastReceiver gcmReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

			}
		};
}