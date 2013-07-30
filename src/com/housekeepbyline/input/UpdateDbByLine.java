package com.housekeepbyline.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Intent;
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
	private final Pattern _DATE_PATTERN = Pattern.compile("^[0-9][0-9][0-9][0-9].[0-9][0-9].[0-9][0-9].*");
	//金額を抽出する正規表現パターン
	private final Pattern _COST_PATTERN = Pattern.compile("[0-9]*.");
	private Matcher match;
	//改行
	//表示用のテキスト領域
	EditText result;
	Spinner userName;
	DatePicker startDay;
	DatePicker endDay;
	EditText et_koumoku;
	Button start;
	
	//タブ文字
	private String _TAB = "	";
	
	//intentから取得するパッケージ名
	private String _PAKAGE_NAME = "jp.naver.line.android";
	//LINEのバックアップファイルパス
	private String _FILE_PATH;
	//LINEのバックアップファイル名
	private String _FILE_NAME;
	//アプリ使用ファイル保存パス（機種依存）
	private String _EX_FILE_DIR = Environment.getExternalStorageDirectory().getPath().toString();
	
	//DB
	private DbAdapter dbAdapter;
	
	public void updateDb(Intent intent,List<String> splitter){
		dbAdapter = new DbAdapter();
		this.splitter = splitter;
		setEnviroment(intent);
		openExternalData();
	}
	
	private void setEnviroment(Intent intent) {
		// ファイルパスの取得
		Uri uri = Uri.parse(intent.getExtras().get(Intent.EXTRA_STREAM).toString());
		// ファイル名の設定
		int len = uri.getLastPathSegment().length();
		_FILE_NAME = uri.getLastPathSegment().substring(0,len -1);
		// ファイルパスの設定
		_FILE_PATH = _EX_FILE_DIR +"/Android/data/"+ _PAKAGE_NAME +"/backup/";
	}
	
	

	private void openExternalData() {
		
		String[] time_name;
		String tmp_koumoku_cost;
		String[] koumoku_cost;
		String time;
		String name;
		String koumoku;
		String cost;
		String line;
		
		try {
			//ファイルのオープン
			File file = new File(_FILE_PATH, _FILE_NAME);
			FileInputStream fis = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
			
			//１行ずつ読み込み
			//集計対象は以下のフォーマット
			//[時刻][TAB][氏名][TAB][項目][区切][金額]
			// (A)そのため、まずTABで分割し、
			//　　[時刻] / [氏名]　/ [項目][区切][金額]
			// (B)その後で、３番目の文字列を区切で分割する
			//　 [時刻] / [氏名]　/ [項目] / [金額]
			
			while( (line = reader.readLine()) != null){
				//投稿日付の取得
				match = _DATE_PATTERN.matcher(line);
				if(match.matches()) YYYYMMDD = line.substring(0, 10);
				//タブによる行の分割
				time_name = line.split(_TAB);
				
				//複数行にわたるコメントの２行目以降は、ユーザ名がはいらないため、タブで行分割しても、項目は１つだけ
				//(A)でないと集計対象外である
				if(time_name.length >= 2){
					tmp_koumoku_cost = time_name[2];
				}else{
					tmp_koumoku_cost = time_name[0];
				}
				
				//分割文字列により行を分割する
				//はじめにセパレートとしてListに登録された文字列を全て"SPLITSTR"に置き換え、
				//その後一気に"SPRITSTRで分割する"
				for(String split:splitter){
					tmp_koumoku_cost = tmp_koumoku_cost.replaceAll(split, splitstr);
				}
				koumoku_cost = tmp_koumoku_cost.split(splitstr);
				
				//集計対象か確認
				if(isTargetLine(time_name,koumoku_cost)){

					time = time_name[0];
					name = time_name[1];
					koumoku = koumoku_cost[0];
					cost = koumoku_cost[1];
					
			    	Uri uri =Uri.parse("content://com.housekeepbyline.db/MEISAI");
			        ContentValues values = new ContentValues();
			    	values.put(MEISAI.L_yyyyMMddhhmm,parseDate(YYYYMMDD, time));
			    	values.put(MEISAI.L_USER_NAME,name);
			    	values.put(MEISAI.L_KOUMOKU,koumoku);
			    	values.put(MEISAI.L_COST,cost);

			    	dbAdapter.insert(uri, values);
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * SQLiteに格納する日付がたにフォーマットを変更する。
	 * SQLiteにはDate型がないが、本フォーマットで登録すると、
	 * 日付関数を使用したSQLが作成可能となる。
	 * 
	 * @param yyyyMMdd 2013/7/30のように"/"で区切られた日付
	 * @param hhmm 4:10のように":"で区切られた時間
	 * @return 2013-07-30 04:10:00で返す
	 */
	private String parseDate(String yyyyMMdd,String hhmm){
		
		//DBへ登録するフォーマット
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String[] date = yyyyMMdd.split("/");
		String[] time = hhmm.split(":");
		
		//変換
		int year = Integer.parseInt(date[0]);
		int month = Integer.parseInt(date[1])-1;
		int day = Integer.parseInt(date[2]);
		int hour = Integer.parseInt(time[0]);
		int min = Integer.parseInt(time[1]);
		int sec = 0;
		Calendar c = Calendar.getInstance();
				 c.set(year,month,day,hour,min,sec);
		return  df.format(c.getTime());
		
	}
	
	/**
	 * 集計対象は以下のフォーマット
	 * [時刻][空白][氏名][TAB][項目][区切][金額]
	 * 解析している行が集計対象が判断する
	 * 
	 * @return 集計する場合は true
	 */
	private boolean isTargetLine(String[] time_name,String[] koumoku_cost){
		boolean flag = false;
		if(time_name.length >= 2 && koumoku_cost.length >=2 && _COST_PATTERN.matcher(koumoku_cost[1]).matches() && check(koumoku_cost[1])){
			flag = true;
		}
		return flag;
	}
	
	
	/**
	 * 文字列を数値に変換できるか確認する
	 * @param s 数値へ変換したい文字列
	 * @return
	 */
	private boolean check(String s){
		try{
			Integer.parseInt(s);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
}
