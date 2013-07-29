package com.housekeepbyline.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.housekeepbyline.R;
import com.housekeepbyline.db.DbAdapter;
import com.housekeepbyline.db.MEISAI;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * @author kentarokira
 *
 */
public class MainActivity extends Activity {

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
	//表示する家計簿リスト
	private ArrayList<Kakeibo> printKakeibo = new ArrayList<Kakeibo>();
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
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dbAdapter = new DbAdapter();
		// インテントを取得する
		Intent intent = getIntent();
		result = (EditText) findViewById(R.id.multiAutoCompleteTextView1);
		setUserName();
		startDay = (DatePicker)findViewById(R.id.DP_START_DAY);
		endDay = (DatePicker)findViewById(R.id.DP_END_DAY);
		et_koumoku = (EditText) findViewById(R.id.ET_KOUMOKU);
		start = (Button) findViewById(R.id.BTN_START);
		
		// 行を分解(Splite)する文字列を設定
		setSplitter(" ","　");
		// パッケージ名
		PAKAGE_NAME = intent.getPackage();
		// インテントの解析
		
		if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())) {
			// ファイルパスの取得
			uri = Uri.parse(getIntent().getExtras().get(Intent.EXTRA_STREAM).toString());
			// ファイルを読み込み 家計簿の集計
			int len = uri.getLastPathSegment().length();
			String filename = uri.getLastPathSegment().substring(0, len-1);
			readExternallStoragePublic(filename);
			
		} else {
		}
		
		/**
		 * 「集計」ボタン押下時の動作
		 */
		start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				result.setText("");
				String sDay = startDay.getYear() + "/" + (startDay.getMonth() + 1) + "/" + startDay.getDayOfMonth();
				String eDay = endDay.getYear() + "/" + (endDay.getMonth() + 1) + "/" + endDay.getDayOfMonth();
				String uName = userName.getSelectedItem().toString();
				String koumoku = et_koumoku.getText().toString();
				//項目に値が入っていない場合は全件検索
				if(koumoku.isEmpty())koumoku="*";
				searchKakeibo(uName,koumoku,sDay,eDay);
				print();				
			}

		});

	};

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

	/**
	 * @param splitter 行の分割文字列
	 */
	public void setSplitter(String... splitter){
		this.splitter = new ArrayList<String>();
		for(String split:splitter){
			this.splitter.add(split);
		}
	}
	
	/**
	 * DBから検索条件に一致するデータを抽出
	 * @param name ユーザ名
	 * @param koumoku 項目
	 * @param startDay 開始日
	 * @param endDay 終了日
	 */
	public void searchKakeibo(String name,String koumoku,String startDay, String endDay){
		
		//現在の抽出済みデータをクリア
		printKakeibo.clear();
		Calendar sCal = Calendar.getInstance();
		Calendar eCal = Calendar.getInstance();
		
		//開始日を準備
		
		int syyyy = (InputDataCheck.parseYear(startDay));
		int sMM   = (InputDataCheck.parseMonth(startDay));
		int sdd   = (InputDataCheck.parseDay(startDay));
		int shh = 0;
		int smm  = 0;
		
		//終了日を準備
		int eyyyy = (InputDataCheck.parseYear(endDay));
		int eMM   = (InputDataCheck.parseMonth(endDay));
		int edd   = (InputDataCheck.parseDay(endDay));
		int ehh = 23;
		int emm  = 59;
		
		//カレンダー型で用意
		sCal.set(syyyy, sMM, sdd, shh, smm, 0);
		eCal.set(eyyyy, eMM, edd, ehh, emm, 0);
		
		//DBからデータベースを全件取得
		Uri uri = Uri.parse("content://db/MEISAI");
		Cursor c = dbAdapter.query(uri, null, null, null, null);
		
		//データベースから取得する値
		String dbName = "";
		String dbDate = "";
		String dbKoumoku = "";
		String dbCost = "";

		//全データに対する処理
		while (c.moveToNext()) {
				dbName = c.getString(2);
				dbDate = c.getString(1);
				dbKoumoku= c.getString(3);
				dbCost = c.getString(4);
				//DB内データの「項目」に画面で指定した文字列が入っていればTRUE,
				//また、画面で指定された文字列が空ならTURE
				boolean koumokuFlag;
				if(koumoku.equals("*")){
					koumokuFlag = true;
				}else{
					koumokuFlag = dbKoumoku.indexOf(koumoku)>=0;
				}
				Kakeibo k = new Kakeibo(dbName,dbDate,dbKoumoku,dbCost);
				//条件に一致する場合は表示対象
				if(k.getName().equals(name) && koumokuFlag && k.getDay().after(sCal) && k.getDay().before(eCal)){
					printKakeibo.add(k);
				}
		}
	}

	/**
	 * ユーザ名をプルダウンで表示するため、DBよりユーザ名を取得しSpineerへ設定
	 */
	private void setUserName() {
		
		//DBよりユーザ名を抽出する
		Uri uri = Uri.parse("content://db/MEISAI");
		Cursor c = dbAdapter.query(uri, null, null, null, null);

		//ユーザ名の重複を排除するため、Set型に入れる
		TreeSet<String> uNameSet = new TreeSet<String>();
		while (c.moveToNext()) {
				uNameSet.add(c.getString(2));
		}
		
		//画面に表示するSpinnerへ登録するListの用意
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		for(String s:uNameSet){
			adapter.add(s);
		}
		
		userName = (Spinner) findViewById(R.id.SP_USERNAME);
		userName.setAdapter(adapter);
	}
	
	/**
	 * 画面に結果表示
	 */
	private void print() {
		String s = "*---------------------*"+BR;
		int sum = 0;
		for(Kakeibo pk : printKakeibo){
			if(check(pk.getCost())){
				sum = sum + Integer.parseInt(pk.getCost());
				s = s + pk.print() + BR;
			}
		}
		result.setText("合計："+sum+BR+s);
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
