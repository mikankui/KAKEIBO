package com.housekeepbyline.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.housekeepbyline.db.DbAdapter;
import com.housekeepbyline.db.MEISAI;

public class UpdateDbByLine {

	//1行を項目と金額に分割する文字列
	private List<String> splitter;
	//splitstrを一時的に置き換える文字列
	private final String splitstr = "SPRITSTR";
	//年月日
	private String YYYYMMDD;
	//年月日を抽出する正規表現パターン
	private final Pattern yyyymmdd = Pattern.compile("^[0-9][0-9][0-9][0-9].[0-9][0-9].[0-9][0-9].*");
	//金額を抽出する正規表現パターン
	private final Pattern cost = Pattern.compile("[0-9]*.");
	private Matcher match;
	//改行
	private String BR = System.getProperty("line.separator");
	//表示用のテキスト領域
	EditText result;
	Spinner userName;
	DatePicker startDay;
	DatePicker endDay;
	EditText et_koumoku;
	Button start;
	//日付フォーマット
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	//ファイルの取得先
	Uri uri;
	//家計簿の集計結果
	String ALL_DATA = "";
	//intentから取得するパッケージ名
	private String PAKAGE_NAME;
	//DB
	private DbAdapter dbAdapter;
	
	public boolean updateDb(){
		setEnviroment();
		openExternalData();
		insertDb();
		return false;
	}
	private void insertDb() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	private void openExternalData() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	private void setEnviroment() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	/**
	 * ファイルからデータを取得し、データベースへ格納する。
	 * @param filename 家計簿を集計する元データのファイル名
	 * @return 家計簿
	 */
	public String readExternallStoragePublic(String filename) {
		
		//LINEのパッケージ名
		String packageName = "jp.naver.line.android";
		File ExFileDir = Environment.getExternalStorageDirectory();
		String path = ExFileDir.getPath().toString() +"/Android/data/"+ packageName +"/backup/";
		StringBuffer s = new StringBuffer();
		String[] parts;
		String tmp_koumoku_cost;
		String[] koumoku_cost;
		try {
			//ファイルのオープン
			File file = new File(path, filename);
			FileInputStream fis = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
			String line;
			//DBのオープン
			
			//１行ずつ読み込み
			while( (line = reader.readLine()) != null){
				//投稿日付の取得
				match = yyyymmdd.matcher(line);
				if(match.matches()) YYYYMMDD = line;
				//タブによる行の分割
				parts = line.split("	");
				//複数行にわたるコメントの２行目以降は、ユーザ名がはいらないため、タブで行分割しても、項目は１つだけ
				if(parts.length >= 2){
					tmp_koumoku_cost = parts[2];
				}else{
					tmp_koumoku_cost = parts[0];
				}
				//分割文字列により行を分割する
				for(String split:splitter){
					tmp_koumoku_cost = tmp_koumoku_cost.replaceAll(split, splitstr);
				}
				koumoku_cost = tmp_koumoku_cost.split(splitstr);
				if(parts.length >= 2 && koumoku_cost.length >=2 && cost.matcher(koumoku_cost[1]).matches()){

			    	Uri uri =Uri.parse("content://com.housekeepbyline.db/MEISAI");
			        ContentValues values = new ContentValues();
			    	values.put(MEISAI.L_yyyyMMddhhmm,YYYYMMDD + " " + parts[0]);
			    	values.put(MEISAI.L_USER_NAME,parts[1]);
			    	values.put(MEISAI.L_KOUMOKU,koumoku_cost[0]);
			    	values.put(MEISAI.L_COST,koumoku_cost[1]);
					Log.d(this.toString(),"uri is " + uri);

			    	dbAdapter.insert(uri, values);
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return s.toString();
	}
}
