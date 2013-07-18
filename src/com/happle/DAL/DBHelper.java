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
					"_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					"name TEXT, " + 
					"content TEXT," +
					"date TEXT," +
					"created TEXT," +
					"is_checked int);";		
	        db.execSQL(create);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + CommonUtilities.TBL_CONVERSATIONS);
			onCreate(db);
		}
}
