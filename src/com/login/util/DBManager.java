package com.login.util;

import java.util.ArrayList;
import java.util.List;

import com.login.weibo.bean.Account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 用于数据库中表的创建，和向表中添加，删除数据、查询数据
 */
public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase mSQLiteDatabase;
	private final String account_insert=
			"INSERT INTO account VALUES(null, ?, ?, ?, ?, ?)";
	private final String account_query="SELECT * FROM account";

	public DBManager(Context context) {
		helper = new DBHelper(context);
		mSQLiteDatabase = helper.getWritableDatabase();
	}

	/**
	 * 向account表中添加数据
	 * @param account 要添加的Account对象数据 
	 */
	public void add(Account account) {
		mSQLiteDatabase.beginTransaction(); // 开始事务
		try {
		  mSQLiteDatabase.execSQL(account_insert,
			new Object[] {account.getUid(),account.getScreen_name(),account.getUrl(),
				 account.getAccess_token(),account.getExpires_in() });

			mSQLiteDatabase.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			mSQLiteDatabase.endTransaction(); // 结束事务
		}
	}

	/**
	 * 关闭数据库
	 */
	public void closeDB() {
		mSQLiteDatabase.close();
	}

	/**
	 * 删除账号
	 * @param account 要删除的账号
	 */
	public void deleteAccount(Account account) {
	    mSQLiteDatabase.delete("account", "uid= ?",new String[] { account.getUid()});
	}

	/**
	 * 删除所有的账号
	 * @param accounts 账号列表
	 */
	public void deleteAccounts(ArrayList<Account> accounts) {
		for (Account account : accounts) {
			mSQLiteDatabase.delete("account", "uid= ?",new String[] { account.getUid()});
		}
	}

	/**
	 * 查询账户
	 * @return List<Account> 账户列表
	 */
	public List<Account> getAccounts() {
		ArrayList<Account> accounts = new ArrayList<Account>();
		Cursor c = queryTheCursor();
		while (c.moveToNext()) {
			Account account = new Account();
			account.setUid(c.getString(c.getColumnIndex("uid")));
			account.setScreen_name(c.getString(c.getColumnIndex("screen_name")));
			account.setUrl(c.getString(c.getColumnIndex("url")));
			account.setAccess_token(c.getString(c.getColumnIndex("access_token")));
		    account.setExpires_in(c.getString(c.getColumnIndex("expires_in")));
			accounts.add(account);
		}
		c.close();
		return accounts;
	}

	/**
	 * 查询账户列表，并返回光标
	 * @return Cursor 光标
	 */
	public Cursor queryTheCursor() {
		Cursor c = mSQLiteDatabase.rawQuery(account_query, null);
		return c;
	}

	/**
	 * 更新account表中的screen_name值
	 * @param account 更新的值
	 */
	public void updateName(Account account) {
		ContentValues cv = new ContentValues();
		cv.put("screen_name", account.getScreen_name());
		mSQLiteDatabase.update("account", cv, "uid = ?",
				new String[] { account.getUid()});
	}
}
