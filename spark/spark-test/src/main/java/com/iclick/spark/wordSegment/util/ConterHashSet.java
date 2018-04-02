package com.iclick.spark.wordSegment.util;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ConterHashSet implements  Serializable{
	private static final long serialVersionUID = -3903452740942758085L;
	
	private Set<Integer> set=null;
	
	public  ConterHashSet(){
		set=new HashSet<Integer>();
	}
	
	public void addelment(int i){
		set.add(i);
	}
	public  int  getsize(){
		return  set.size();
		
	}
	
	public  Set<Integer>  getHashset() {
		return  set;
	}
	
	public static void main(String[] args) {
		ConterHashSet  counSet=new  ConterHashSet();
		System.out.println(counSet.getsize());
		counSet.addelment(1);
		counSet.addelment(1);
		counSet.addelment(2);
		System.out.println(counSet.getsize());
		System.out.println(counSet.getHashset());	
		
	}
	
}
