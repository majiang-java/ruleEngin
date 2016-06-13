/**                                               
 * <p>Title: DROOLS RULE ENGINE SYSTEM</p>          
 * <p>Description: 主要用于动态生成编译和加载JavaBean的服务类 </p>                           
 * <p>Copyright: Copyright (c) 2012 - 2015</p>    
 * <p>Company: Excel Technology International (Bei Jing) Limited</p>      
 * @author    
 * @Date 2013/03/15                                
 * @version 1.0                                   
 */
package com.ruleEngine.bean;

import java.util.List;
/**
 * 
 * @author Johnson Ma
 *
 */
public class ConditionBean {
	
	private List<String> cats; //类别
	private String name; //名称
	private String droolsDate; //drools数据

	

	public List<String> getCats() {
		return cats;
	}
	public void setCats(List<String> cats) {
		this.cats = cats;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDroolsDate() {
		return droolsDate;
	}
	public void setDroolsDate(String droolsDate) {
		this.droolsDate = droolsDate;
	}
	

}
