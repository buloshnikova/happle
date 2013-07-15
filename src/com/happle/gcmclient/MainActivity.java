package com.happle.gcmclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.happle.gcmclient.config.Constants;
import com.happle.gcmclient.utility.AlertDialogManager;
import com.happle.gcmclient.utility.NetworkUtility;

public class MainActivity extends Activity {
	// This string will hold the lengthy registration id that comes from
	// GCMRegistrar.register()
	private String regId = "";

	// These strings are hopefully self-explanatory
	private String registrationStatus = "Not yet registered";
	private String broadcastMessage = "No broadcast message";

	// This intent filter will be set to filter on the string
	// "GCM_RECEIVED_ACTION"
	IntentFilter gcmFilter;
	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// textviews used to show the status of our app's registration, and the
	// latest broadcast message.
	TextView tvRegStatusResult;
	TextView tvBroadcastMessage;
	EditText txtMessage;
	Button btnSend;
	SendMessageTask smTask;
	// SendRegistrationTask srTask;
	// This broadcastreceiver instance will receive messages broadcast
	// with the action "GCM_RECEIVED_ACTION" via the gcmFilter

	// A BroadcastReceiver must override the onReceive() event.
	private BroadcastReceiver gcmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			broadcastMessage = intent.getExtras().getString("gcm");
			if (broadcastMessage != null) {
				// display our received message
				tvBroadcastMessage.setText(broadcastMessage);
			}
		}
	};

	// Reminder that the onCreate() method is not just called when an app is
	// first opened,
	// but, among other occasions, is called when the device changes
	// orientation.
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Check if Internet present
		if (!NetworkUtility.getInstance(MainActivity.this).isNetworkAvailable()) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		tvBroadcastMessage = (TextView) findViewById(R.id.tv_message);
		tvRegStatusResult = (TextView) findViewById(R.id.tv_reg_status_result);
		txtMessage = (EditText) findViewById(R.id.txtMsg);
		txtMessage.setText("program started");
		// Create our IntentFilter, which will be used in conjunction with a
		// broadcast receiver.
		gcmFilter = new IntentFilter();
		gcmFilter.addAction("GCM_RECEIVED_ACTION");

		registerClient();
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

	// This registerClient() method checks the current device, checks the
	// manifest for the appropriate rights, and then retrieves a registration id
	// from the GCM cloud. If there is no registration id, GCMRegistrar will
	// register this device for the specified project, which will return a
	// registration id.
	public void registerClient() {
		try {
			// Check that the device supports GCM (should be in a try / catch)
			GCMRegistrar.checkDevice(this);
			// Check the manifest to be sure this app has all the required
			// permissions.
			GCMRegistrar.checkManifest(this);
			// Get the existing registration id, if it exists.
			regId = GCMRegistrar.getRegistrationId(this);

			if (regId.equals("")) {
				registrationStatus = "Registering...";
				tvRegStatusResult.setText(registrationStatus);
				// register this device for this project
				GCMRegistrar.register(this, Constants.PROJECT_NUMBER);
				regId = GCMRegistrar.getRegistrationId(this);
				registrationStatus = "Registration Acquired";
			} else {
				registrationStatus = "Already registered";
			}
			// sendRegistrationToServer();
			Log.d(Constants.TAG, regId);
			TextView tvRegID = (TextView) findViewById(R.id.tv_registration_id);
			tvRegID.setText(regId);

		} catch (Exception e) {
			e.printStackTrace();
			registrationStatus = e.getMessage();

		}

		Log.d(Constants.TAG, registrationStatus);
		tvRegStatusResult.setText(registrationStatus);

		Log.d(Constants.TAG, regId);
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

	class SendMessageTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			String response = "";
			try {
				response = BackendManager.getResultPost(
						Constants.URL_GET_MESSAGE, txtMessage.getText()
								.toString(), "tickerText", "contentTitle",
						regId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			tvBroadcastMessage.setEnabled(true);
			tvBroadcastMessage.setText(result);
		}

	}
}