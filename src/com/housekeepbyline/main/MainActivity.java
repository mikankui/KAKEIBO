package com.housekeepbyline.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.housekeepbyline.R;
import com.housekeepbyline.db.DbAdapter;
import com.housekeepbyline.db.MEISAI;
import com.housekeepbyline.graph.LineBarGraph;
import com.housekeepbyline.input.UpdateDbByLine;

/**
 * @author kentarokira
 *
 */
public class MainActivity extends Activity implements OnClickListener{

	//1行を項目と金額に分割する文字列
	private List<String> splitter;
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
	Button BTN_DAY_SUM;
	Button BTN_DETAIL;
	Button BTN_GRAPH;
	//日付フォーマット
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//DB
	private DbAdapter dbAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dbAdapter = new DbAdapter();

		result = (EditText) findViewById(R.id.multiAutoCompleteTextView1);
		setUserName();
		startDay = (DatePicker)findViewById(R.id.DP_START_DAY);
		endDay = (DatePicker)findViewById(R.id.DP_END_DAY);
		et_koumoku = (EditText) findViewById(R.id.ET_KOUMOKU);
		BTN_DAY_SUM = (Button) findViewById(R.id.BTN_DAY_SUM);
		BTN_DETAIL = (Button) findViewById(R.id.BTN_DETAIL);
		BTN_GRAPH= (Button) findViewById(R.id.BTN_GRAPH);
		BTN_DAY_SUM.setOnClickListener(this);
		BTN_DETAIL.setOnClickListener(this);
		BTN_GRAPH.setOnClickListener(this);
		
		// 行を分解(Splite)する文字列を設定
		setSplitter(" ","　");

		// インテントの解析
		
		if (Intent.ACTION_SEND_MULTIPLE.equals(getIntent().getAction())) {
			UpdateDbByLine udbl = new UpdateDbByLine();
			udbl.updateDb(getIntent(), splitter);
			
		} else {
		}
		


	};

	/**
	 * @param splitter 行の分割文字列
	 */
	public List<String> setSplitter(String... splitter){
		this.splitter = new ArrayList<String>();
		for(String split:splitter){
			this.splitter.add(split);
		}
		return this.splitter;
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
	
	public void calcDay(String name,String startDay, String endDay){
		
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
		String[] projection = {MEISAI.L_USER_NAME , "sum("+MEISAI.L_COST+")"};
		String selection =  MEISAI.L_USER_NAME +"=? and "+ MEISAI.L_yyyyMMddhhmm + " between ? and ? ";
		String[] selectionArgs = new String[3];
		String groupBy = MEISAI.L_USER_NAME;
		String having = null;
		String orderBy = null;

		
		//データベースから取得する値
		String dbName = "";
		String dbCost = "";
		String s = "*---------------------*"+BR;
		
		//検索条件
		selectionArgs[0] = name;
		int sum = 0;
		
		while(sCal.before(eCal)){
			selectionArgs[1] = 	sdf.format(sCal.getTime());
			sCal.add(Calendar.DAY_OF_MONTH, 1);
			selectionArgs[2] = 	sdf.format(sCal.getTime());
			Cursor c = dbAdapter.queryraw(uri, projection, selection, selectionArgs, groupBy, having, orderBy);
			while (c.moveToNext()) {
				dbName = c.getString(0);
				dbCost = c.getString(1);
				sum = sum + Integer.valueOf(dbCost);
				s = s + selectionArgs[1].substring(0,10) + " " + dbName + " " + dbCost + BR;
			}
		}
		result.setText("合計:" + sum + BR + s);
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

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		
		result.setText("");
		String sDay = startDay.getYear() + "/" + (startDay.getMonth() + 1) + "/" + startDay.getDayOfMonth();
		String eDay = endDay.getYear() + "/" + (endDay.getMonth() + 1) + "/" + endDay.getDayOfMonth();
		String uName = userName.getSelectedItem().toString();
		String koumoku = et_koumoku.getText().toString();
		
		switch(v.getId()){
		case R.id.BTN_DAY_SUM:
			calcDay(uName,sDay,eDay);
			break;
		case R.id.BTN_DETAIL:
			//項目に値が入っていない場合は全件検索
			if(koumoku.isEmpty())koumoku="*";
			searchKakeibo(uName,koumoku,sDay,eDay);
			print();	
			break;
		case R.id.BTN_GRAPH:
			Intent intent = new Intent(this,LineBarGraph.class);
			intent.setAction(Intent.ACTION_VIEW);
			//intent.putExtra("data", )
			startActivity(intent);
		}
	}
	
}
