package com.housekeepbyline.output;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class OutputDataCheck {
	
	private static DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
	
	/**
	 * 入力日付から年を抽出
	 * @param yyyyMMdd
	 * @return yyyy
	 */
	public static int parseYear(String yyyyMMddhhmmss){
		String[] date = yyyyMMddhhmmss.split("-");
		return Integer.parseInt(date[0]);
	}

	/**
	 * 入力日付から月を抽出
	 * @param yyyyMMdd
	 * @return MM
	 */
	public static int parseMonth(String yyyyMMddhhmmss){
		String[] date = yyyyMMddhhmmss.split("-");
		return Integer.parseInt(date[1])-1;
	}

	/**
	 * 入力日付から日付を抽出
	 * @param yyyyMMdd
	 * @return dd 
	 */
	public static int parseDay(String yyyyMMddhhmmss){
		String[] date = yyyyMMddhhmmss.split(" ")[0].split("-");
		return Integer.parseInt(date[2]);
	}
	
	/**
	 * 入力日付から日付を抽出
	 * @param yyyyMMdd
	 * @return dd 
	 */
	public static int parseHour(String yyyyMMddhhmmss){
		String[] time = yyyyMMddhhmmss.split(" ")[1].split(":");
		return Integer.parseInt(time[0]);
	}
	
	/**
	 * 入力日付から日付を抽出
	 * @param yyyyMMdd
	 * @return dd 
	 */
	public static int parseMin(String yyyyMMddhhmmss){
		String[] time = yyyyMMddhhmmss.split(" ")[1].split(":");
		return Integer.parseInt(time[1]);
	}
	
	/**
	 * 入力日付から日付を抽出
	 * @param yyyyMMdd
	 * @return dd 
	 */
	public static int parseSec(String yyyyMMddhhmmss){
		String[] time = yyyyMMddhhmmss.split(" ")[1].split(":");
		return Integer.parseInt(time[2]);
	}
	
}
