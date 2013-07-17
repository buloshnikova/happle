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

	private String registrationStatus = "Not yet registered";
	private String broadcastMessage = "No broadcast message";

	// This intent filter will be set to filter on the string "GCM_RECEIVED_ACTION"
	private GCMReceiver mGCMReceiver;
	private IntentFilter registeredFilter;
	
	SharedPreferences sPref;
	
	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	TextView tvRegStatusResult;
	TextView tvBroadcastMessage;
	TextView tvRegID;
	EditText txtMessage;
	Button btnSend;
	SendMessageTask smTask;

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
		
		initReceiver();
		
		if (CommonUtilities.SENDER_ID == null) {
			Log.d(TAG, "Missing SENDER_ID");
			return;
		}

		registeredFilter.addAction("GCM_RECEIVED_ACTION");

		if (isRegistered()) {
			regId = getPreferences(CommonUtilities.REGISTRATION_ID);
		} else { 
			registerClient(); }
		tvRegID = (TextView) findViewById(R.id.tv_registration_id);
		tvRegID.setText(regId);		
	}

	private void initUI() {
		tvBroadcastMessage = (TextView) findViewById(R.id.tv_message);
		tvRegStatusResult = (TextView) findViewById(R.id.tv_reg_status_result);
		txtMessage = (EditText) findViewById(R.id.txtMsg);
		txtMessage.setText("program started");
		btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (TextUtils.isEmpty(txtMessage.getText())) {
					Toast.makeText(MainActivity.this,
							"Please enter a message recipient.",
							Toast.LENGTH_SHORT).show();
					return;
				}
				sendMessageToServer();
			}
		});
	}
	
	private void initReceiver() {
		mGCMReceiver = new GCMReceiver();
		registeredFilter = new IntentFilter();
		registeredFilter.addAction(CommonUtilities.ACTION_ON_REGISTERED);
		registeredFilter.addAction(CommonUtilities.ACTION_ON_NEW_COMMENT);
	}
	
	public void registerClient() {
		try {
			// Check that the device supports GCM (should be in a try / catch)
			GCMRegistrar.checkDevice(this);
			// Check the manifest to be sure this app has all the required permissions.
			GCMRegistrar.checkManifest(this);
			// Get the existing registration id, if it exists.
			regId = GCMRegistrar.getRegistrationId(this);

			if (regId.equals("")) {
				registrationStatus = "Registering...";
				tvRegStatusResult.setText(registrationStatus);
				// register this device for this project
				GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
				regId = GCMRegistrar.getRegistrationId(this);
				registrationStatus = "Registration Acquired";
			} else {
				registrationStatus = "Already registered";
			}
			savePreverences(CommonUtilities.REGISTRATION_ID, regId);
		} catch (Exception e) {
			e.printStackTrace();
			registrationStatus = e.getMessage();

		}

		Log.d(TAG, registrationStatus);
		tvRegStatusResult.setText(registrationStatus);

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
	
	public void sendMessageToServer() {
		txtMessage.setEnabled(false);
		smTask = new SendMessageTask();
		smTask.execute();
	}

	// If the user changes the orientation of his phone, the current activity
	// is destroyed, and then re-created. This means that our broadcast message
	// will get wiped out during re-orientation.
	// So, we save the broadcast message during an onSaveInstanceState()
	// event, which is called prior to the destruction of the activity.
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putString("BroadcastMessage", broadcastMessage);

	}

	// When an activity is re-created, the os generates an
	// onRestoreInstanceState()
	// event, passing it a bundle that contains any values that you may have put
	// in during onSaveInstanceState()
	// We can use this mechanism to re-display our last broadcast message.

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);

		broadcastMessage = savedInstanceState.getString("BroadcastMessage");
		tvBroadcastMessage.setText(broadcastMessage);
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
			
			broadcastMessage = intent.getExtras().getString("gcm");
			if (broadcastMessage != null) {
				// display our received message
				tvBroadcastMessage.setText(broadcastMessage);
			}
		}
	};

	class SendMessageTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			int response = CommonUtilities.FAILED;
			try {
				response = BackendManager.sendMessage(txtMessage.getText().toString(), "1", "1", CommonUtilities.MESSAGE_ROOT, regId, CommonUtilities.MSG_STATUS_ACTIVE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			tvBroadcastMessage.setEnabled(true);
			tvBroadcastMessage.setText(result);
		}

	}
}