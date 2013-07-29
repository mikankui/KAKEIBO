package com.housekeepbyline.db;

public class MEISAI extends TABLE_DEFINITION{

	private String TABLE_NAME = "MEISAI";

	public static final String ID = "_id";
	public static final String L_yyyyMMddhhmm = "LINE_yyyyMMddhhmm";
	public static final String L_USER_NAME = "LINE_PERSON_NAME";
	public static final String L_KOUMOKU= "LINE_KOUMOKU";
	public static final String L_COST= "LINE_COST";
	public static final String yyyyMMddhhmm = "yyyyMMddhhmm";
	public static final String USER_NAME = "PERSON_NAME";
	public static final String KOUMOKU= "KOUMOKU";
	public static final String COST= "COST";
	public static final String KUBUN_USER= "KUBUN_USER";
	public static final String KUBUN_AOUT= "KUBUN_AOUT";
	public static final String DELETE_FLAG= "DELETE_FLAG";

	private String CreateQuery = "create table "
			+ TABLE_NAME
			+ " ("
			+ ID + " integer primary key autoincrement,"
			+ L_yyyyMMddhhmm +" integer not null,"
			+ L_USER_NAME +" integer not null,"
			+ L_KOUMOKU +" integer not null,"
			+ L_COST+" integer not null,"
			+ yyyyMMddhhmm + ","
			+ USER_NAME +","
			+ KOUMOKU +","
			+ KUBUN_USER+","
			+ KUBUN_AOUT+","
			+ DELETE_FLAG+","
			+ "UNIQUE( "
			+ L_yyyyMMddhhmm+","
			+ L_USER_NAME+","
			+ L_KOUMOKU+","
			+ L_COST
			+ ")"
			+");";

	@Override
	public String getCreateQuery() {
		return CreateQuery;
	}
}
