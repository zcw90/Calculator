package com.zcw.calculator.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	//数据库版本好
	private static final int DATABASE_VERSION=1;
	
	private static final String DATABASE_NAME="db_history";			//数据库名
	public static final String TABLE_NAME="table_history";			//设备表名

	//带全部参数的构造函数，此构造函数必不可少
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	//带三个参数的构造函数，调用的是带所有参数的构造函数
	public DatabaseHelper(Context context,String name,int version){
		this(context, name,null,version);
	}
	
	//带两个参数的构造函数，调用的其实是带三个参数的构造函数
	public DatabaseHelper(Context context,int version){
		this(context,DATABASE_NAME,version);
	}
	
	//带一个参数的构造函数，调用的其实是带三个参数的构造函数
	public DatabaseHelper(Context context){
		this(context,DATABASE_NAME,DATABASE_VERSION);
	}

	//创建数据库
	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建设备表名的sql语句
		String sql_device="CREATE TABLE IF NOT EXISTS [" +
				TABLE_NAME +
				"] (" +
				"[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
				"[expression] VARCHAR(100)," +
				"[result] VARCHAR(32)," +
				"[date] VARCHAR(32))";

		db.execSQL(sql_device);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
