package com.happle.gcmclient.backendmanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.happle.gcmclient.config.Constants;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class BackendManager {
	private static final String DISPLAY_MESSAGE_ACTION = "BackendManager";
	private static final String MESSAGE = "MESSAGE";
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();

	static InputStream getResultGet(String url) {
		Log.d("BackendManager.getResult", "starting request = " + url);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(url);
		try {
			HttpResponse getResponse = client.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				Log.w(DISPLAY_MESSAGE_ACTION, "Error " + statusCode
						+ " for URL " + url);
				return null;
			}
			return getResponse.getEntity().getContent();

		} catch (Exception e) {
			getRequest.abort();
			Log.w(DISPLAY_MESSAGE_ACTION, "Error for URL " + url, e);
		}
		return null;
	}

	public static String getResultPost(String url, String message,
			String tickerText, String contentTitle, String senderID) {
		Log.d("BackendManager.getResultPost", "starting request = " + url);
		String res = "-101";
		DefaultHttpClient httpClient = new DefaultHttpClient();

		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("message", message));
		pairs.add(new BasicNameValuePair("tickerText", tickerText));
		pairs.add(new BasicNameValuePair("contentTitle", contentTitle));
		pairs.add(new BasicNameValuePair("senderID", senderID));
		pairs.add(new BasicNameValuePair("phoneType", Constants.phoneType));
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(post);
			HttpEntity httpentity = response.getEntity();
			res = EntityUtils.toString(httpentity);
			res = res.trim();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return res;
	}

	public String getRegistrationPost(String url, String senderID) {
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		String res = "-101";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("senderID", senderID));
		pairs.add(new BasicNameValuePair("phoneType", Constants.phoneType));
		// Url Encoding the POST parameters
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs));
		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();
		}
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(DISPLAY_MESSAGE_ACTION, "Attempt #" + i + " to register");
			// Making HTTP Request
			try {
				HttpResponse response = httpClient.execute(post);
				HttpEntity httpentity = response.getEntity();
				res = EntityUtils.toString(httpentity);
				res = res.trim();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				Log.e(DISPLAY_MESSAGE_ACTION, "Failed to register on attempt "
						+ i + ":" + e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(DISPLAY_MESSAGE_ACTION, "Sleeping for " + backoff
							+ " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(DISPLAY_MESSAGE_ACTION,
							"Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return res;
				}
			}
			// increase backoff exponentially
			backoff *= 2;
		}
		return res;
	}

	public String sendUnregister(String url, String senderID) {
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		String res = "-101";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("senderID", senderID));
		pairs.add(new BasicNameValuePair("phoneType", Constants.phoneType));
		// Url Encoding the POST parameters
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs));
		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();
		}
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(DISPLAY_MESSAGE_ACTION, "Attempt #" + i + " to unregister");
			// Making HTTP Request
			try {
				HttpResponse response = httpClient.execute(post);
				HttpEntity httpentity = response.getEntity();
				res = EntityUtils.toString(httpentity);
				res = res.trim();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				Log.e(DISPLAY_MESSAGE_ACTION,
						"Failed to unregister on attempt " + i + ":" + e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(DISPLAY_MESSAGE_ACTION, "Sleeping for " + backoff
							+ " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(DISPLAY_MESSAGE_ACTION,
							"Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return res;
				}
			}
			// increase backoff exponentially
			backoff *= 2;
		}
		return res;
	}

	static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(MESSAGE, message);
		context.sendBroadcast(intent);
	}
}
