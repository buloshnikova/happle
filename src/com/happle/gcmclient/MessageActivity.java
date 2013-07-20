package com.happle.gcmclient;

import com.happle.gcmclient.R;
import com.happle.gcmclient.backendmanager.BackendManager;
import com.happle.gcmclient.config.CommonUtilities;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MessageActivity extends Activity {
	private TextView tvMessage;
	private EditText txtMessage;
	private Button btnSend;
	SendMessageTask smTask;

	private IntentFilter registeredFilter;
	
	private String broadcastMessage;
	private final String TAG = this.getClass().getSimpleName();
	private String regId = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String requestCode = extras.getString("REQUEST_CODE");
			if (Integer.parseInt(requestCode) == CommonUtilities.REQUEST_CODE_UPD) {
				// pull from DB all messages from this conversation
				// display messaged in the right order
			}
		}
		
		initUI();
		
		initReceiver();

		registeredFilter.addAction("GCM_RECEIVED_ACTION");
	}

	private void initUI()
	{
		tvMessage = (TextView) findViewById(R.id.tv_message);
		txtMessage = (EditText) findViewById(R.id.txtMsg);
		btnSend = (Button)findViewById(R.id.btnSend);
		btnSend.setOnClickListener(sendListener);
		
	}
	
	private OnClickListener sendListener = new OnClickListener() {
		public void onClick(View v) {
			if (TextUtils.isEmpty(txtMessage.getText())) {
				Toast.makeText(MessageActivity.this,
						"Please enter a message recipient.",
						Toast.LENGTH_SHORT).show();
				return;
			}
			sendMessageToServer();
		}
	};
	
	private void initReceiver() {
		new GCMReceiver();
		registeredFilter = new IntentFilter();
		registeredFilter.addAction(CommonUtilities.ACTION_ON_REGISTERED);
		registeredFilter.addAction(CommonUtilities.ACTION_ON_NEW_COMMENT);
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
		tvMessage.setText(broadcastMessage);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		String msg = getIntent().getStringExtra(CommonUtilities.FIELD_MESSAGE);
		tvMessage.setText(msg);
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
	public void sendMessageToServer() {
		txtMessage.setEnabled(false);
		smTask = new SendMessageTask();
		smTask.execute();
	}


	class SendMessageTask extends AsyncTask<Void, Void, Integer> {
		Context context;
		@Override
		protected Integer doInBackground(Void... params) {
			int response = CommonUtilities.FAILED;
			try {
				String msg_id = CommonUtilities.generateGUID(context);
				String wave_id = CommonUtilities.generateGUID(context);; 
				response = BackendManager.sendMessage(txtMessage.getText().toString(), msg_id, wave_id, CommonUtilities.MESSAGE_ROOT, regId, CommonUtilities.MSG_STATUS_ACTIVE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			tvMessage.setEnabled(true);
			tvMessage.setText(result);
		}

	}
	private class GCMReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			broadcastMessage = intent.getExtras().getString("gcm");
			if (broadcastMessage != null) {
				// display our received message
				tvMessage.setText(broadcastMessage);
			}
		}
	};
	
	private BroadcastReceiver gcmReceiver = new BroadcastReceiver()  {
		@Override
		public void onReceive(Context context, Intent intent) {

		}
	};
}
