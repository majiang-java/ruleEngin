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

public class DecisionTableBean {
	
	private String desisonTableName;  //决策表名称
	private List<DeciFieldBean> fieldList; //字段列表
	private List<String> catList;  //类别列表
	public String getDesisonTableName() {
		return desisonTableName;
	}
	public void setDesisonTableName(String desisonTableName) {
		this.desisonTableName = desisonTableName;
	}
	public List<DeciFieldBean> getFieldList() {
		return fieldList;
	}
	public void setFieldList(List<DeciFieldBean> fieldList) {
		this.fieldList = fieldList;
	}
	public List<String> getCatList() {
		return catList;
	}
	public void setCatList(List<String> catList) {
		this.catList = catList;
	}

	

}
