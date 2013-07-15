package com.happle.gcmclient;

import com.happle.gcmclient.R;
import com.happle.gcmclient.config.Constants;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MessageActivity extends Activity {
	private TextView tvMessage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_message);
	    tvMessage = (TextView) findViewById(R.id.message);
	  }
	  @Override
	  public void onResume() {
	    super.onResume();
	    String msg = getIntent().getStringExtra(Constants.FIELD_MESSAGE);
	    tvMessage.setText(msg);
	  }
}
