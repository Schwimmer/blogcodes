package com.iclick.spark.buzzads.model;
import java.io.Serializable;

import java.util.Map;
import java.util.HashMap;
public class JPriceInfo implements  Serializable {
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -6034659347477968158L;
	Map<String, Integer>  category_tagid= new HashMap<String, Integer>();
      Map<String, Integer>  category_browser= new HashMap<String, Integer>();
      Map<String, Integer>  category_os= new HashMap<String, Integer>();
      Map<String, Integer>  category_province= new HashMap<String, Integer>();
      Map<String, Integer>  category_hour= new HashMap<String, Integer>();
     
	public  Map<String,Integer>   getcategory_tagid(){
		return  category_tagid;
	}
	public  void setcategory_tagid(Map<String, Integer>  category_tagid){
		this.category_tagid=category_tagid;
	}
	
	public  Map<String,Integer>   getcategory_browser(){
		return  category_browser;
	}
	public  void setcategory_browser(Map<String, Integer>  category_browser){
		this.category_browser=category_browser;
	}
	
	
	public  Map<String,Integer>   getcategory_os(){
		return  category_os;
	}
	public  void setcategory_os(Map<String, Integer>  category_os){
		this.category_os=category_os;
	}
	
	
	public  Map<String,Integer>   getcategory_province(){
		return  category_province;
	}
	public  void setcategory_province(Map<String, Integer>  category_province){
		this.category_province=category_province;
	}
	
	
	public  Map<String,Integer>   getcategory_hour(){
		return  category_hour;
	}
	public  void setcategory_hour(Map<String, Integer>  category_hour){
		this.category_hour=category_hour;
	}
	
	
}
