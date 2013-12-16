package com.login.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 用于数据库的创建与更新
 * @version 1.0 
 */
public class DBHelper extends SQLiteOpenHelper {
	private static final String ACCOUNT_DATABASE_NAME = "account.db";
	private static final int ACCOUNT_DATABASE_VERSION = 1;
	
    private final String table="CREATE TABLE IF NOT EXISTS account"
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, uid VARCHAR,screen_name VARCHAR,"
			+ "url VARCHAR,access_token VARCHAR, expires_in VARCHAR)";
    
    private final String drop_table="DROP TABLE IF EXISTS account";
    
	public DBHelper(Context context) {
		super(context, ACCOUNT_DATABASE_NAME, null, ACCOUNT_DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(drop_table);
		onCreate(db);
	}
}
