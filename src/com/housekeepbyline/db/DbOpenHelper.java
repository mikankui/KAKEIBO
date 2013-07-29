package com.housekeepbyline.db;

import com.housekeepbyline.db.TABLE_DEFINITION;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbOpenHelper  extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "HKBL001";
	private static final int DB_VERSION = 2;
	private static final String[] TABLES = {
		"com.housekeepbyline.db.MEISAI"
		//"com.housekeepbyline.db.RATE",
		//"com.housekeepbyline.db.BUNRUI"
	};

	public DbOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			for(int i=0;i<TABLES.length;i++){
				Log.d(this.getClass().toString(), "TABLES[i] is " + TABLES[i]);
				Class table = Class.forName(TABLES[i]);
				Log.d(this.getClass().toString(), "table is " + table);
				TABLE_DEFINITION td = (TABLE_DEFINITION)table.newInstance();
				Log.d(this.getClass().toString(), "query is " + td.getCreateQuery());
				db.execSQL(td.getCreateQuery());
			}
		}catch (ClassNotFoundException e){
			Log.d(this.getClass().toString(), "�e�[�u���̐V�K�쐬�ɂ����ς����܂����B");
		}catch(IllegalAccessException e){
			Log.d(this.getClass().toString(), "�f�[�^�x�[�X�̃A�N�Z�X�Ɏ��s���܂����B");
		}catch(InstantiationException e){
			Log.d(this.getClass().toString(), "???");
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

}
