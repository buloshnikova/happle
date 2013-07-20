package com.happle.DAL;

import com.happle.gcmclient.config.CommonUtilities;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
	// Constructor, defines the DB name & version
		public DBHelper(Context context) {
			super(context, CommonUtilities.DATABASE_NAME, null, CommonUtilities.DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			String create = "CREATE TABLE " + CommonUtilities.TBL_CONVERSATIONS + "( "  + 
					"msg_id_local INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"wave_id INTEGER NOT NULL, " + 
					"ask_status NUMERIC," +
					"message TEXT," +
					"lng_id NUMERIC," +
					"datetime NUMERIC," +
					"msg_status NUMERIC," +
					"msg_order INTEGER," +
					"msg_order INTEGER," +
					"is_sent INTEGER);";		
	        db.execSQL(create);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + CommonUtilities.TBL_CONVERSATIONS);
			onCreate(db);
		}
}
