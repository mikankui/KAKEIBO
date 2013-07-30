package com.housekeepbyline.main;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.housekeepbyline.output.OutputDataCheck;

public class Kakeibo {

	private Calendar day = Calendar.getInstance();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	private String Name;
	private String Koumoku;
	private String cost;
	
	public Kakeibo(String name, String date ,String Koumoku,String cost){
		this.Name = name;
		setDay(date);
		this.Koumoku = Koumoku;
		this.cost = cost;
	}
	
	public Kakeibo() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public Calendar getDay() {
		return day;
	}
	
	public void setDay(String date) {
		int yyyy = (OutputDataCheck.parseYear(date));
		int MM   = (OutputDataCheck.parseMonth(date));
		int dd   = (OutputDataCheck.parseDay(date));
		int hh   = (OutputDataCheck.parseHour(date));
		int mm   = (OutputDataCheck.parseMin(date));
		int ss   = (OutputDataCheck.parseSec(date));
		
		day.set(yyyy, MM, dd, hh, mm, ss);
	}
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getKoumoku() {
		return Koumoku;
	}
	public void setKoumoku(String koumoku) {
		Koumoku = koumoku;
	}
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}
	
	public String print(){
		return sdf.format(day.getTime()) +" " + Name + ":" + Koumoku + " " + cost;
	}

	
}
