package com.housekeepbyline.main;

import java.io.Serializable;
import java.util.Date;

public class GraphData implements Serializable  {
	
	private static final long serialVersionUID = 1L; 
	
	String tag;
	Date X;
	int Y;
	
	GraphData(String tag,Date x,int y){
		this.tag = tag;
		this.X = x;
		this.Y = y;
	}
	
	
	public void setX(Date x){
		X = x;
	}
	
	public void setY(int y){
		Y = y;
	}

	public Date getX(){
		return X;
	}
	
	public int getY(){
		return Y;
	}
}
