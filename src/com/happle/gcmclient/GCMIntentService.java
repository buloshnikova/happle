package com.happle.gcmclient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.happle.gcmclient.R;
import com.happle.gcmclient.backendmanager.BackendManager;
import com.happle.gcmclient.config.CommonUtilities;
import com.happle.gcmclient.utility.WakeLocker;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	private String regId = "";
	private boolean isLogin = true;
	SendRegistrationTask srTask;
	SendUnregisterTask suTask;

	SharedPreferences sPref;

	public GCMIntentService() {
		super(CommonUtilities.SENDER_ID);
		Log.d(TAG, "GCMIntentService init");
	}

	@Override
	protected void onError(Context ctx, String sError) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Error: " + sError);
	}

	@Override
	protected void onMessage(Context ctx, Intent intent) {
		Log.d(TAG, "Message Received");
		String message = intent.getStringExtra("message");
		sendGCMIntent(ctx, message);
		// displayMessage(ctx, message);
		// Waking up mobile if it is sleeping
		WakeLocker.acquire(getApplicationContext());
		// notifies user
		generateNotification(ctx, message);
		vibrate(ctx);
	}
	private void vibrate(Context ctx) {
		Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(1000);
	}

	private void sendGCMIntent(Context ctx, String message) {

		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("GCM_RECEIVED_ACTION");

		broadcastIntent.putExtra("gcm", message);

		ctx.sendBroadcast(broadcastIntent);

	}

	@Override
	protected void onRegistered(Context ctx, String regId) {
		//savePreverences(CommonUtilities.REGISTRATION_ID, regId);
		int tryToSave = 0;
		do {
			if (CommonUtilities.savePreverences(ctx, CommonUtilities.REGISTRATION_ID, regId))
				tryToSave = 5;
			tryToSave++;
		} while (tryToSave < 5);
		isLogin = false;
		// send regId to your server
		srTask = new SendRegistrationTask(isLogin, regId);
		srTask.execute();
		Log.d(TAG, regId);
	}

	class SendRegistrationTask extends AsyncTask<Void, Boolean, Integer> {
		private String regId = "";
		private boolean isLogin = true;

		public SendRegistrationTask(Boolean IsLogin, String regId) {
			this.regId = regId;
			this.isLogin = IsLogin;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			int error = CommonUtilities.FAILED;
			try {
				BackendManager bManager = new BackendManager();
				error = bManager.sendLoginRegistration(CommonUtilities.URL_REGISTER, regId, isLogin);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(TAG, String.valueOf(error));
			return error;
		}
	}

	@Override
	protected void onUnregistered(Context ctx, String regId) {
		// send notification to your server to remove that regId
		suTask = new SendUnregisterTask();
		suTask.execute();
		Log.d(TAG, regId);
	}

	class SendUnregisterTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			int error = CommonUtilities.FAILED;
			try {
				BackendManager bManager = new BackendManager();
				error = bManager.sendUnregisterLogout(CommonUtilities.URL_REGISTER, regId, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(TAG, String.valueOf(error));
			return error;
		}
	}

	public static void generateNotification(Context context, String message) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		String title = context.getString(R.string.app_name);
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(context, MessageActivity.class);
		// Set a unique data uri for each notification to make sure the activity
		// gets updated
		intent.setData(Uri.parse(message));
		intent.putExtra(CommonUtilities.FIELD_MESSAGE, message);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, title, message, pendingIntent);
		nm.notify(R.id.notification_id, notification);
	}

}
