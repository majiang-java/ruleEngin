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

public class RuleBean implements Cloneable{
	
	private List<String> cat;    //类别
	private String action; //权限
	private String name;	//名称
	private String ruleStr;  //条件串
	private String genData; //droolsData、
	
	private boolean result = false;//处理结果
	
	
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getGenData() {
		return genData;
	}
	public void setGenData(String genData) {
		this.genData = genData;
	}


	public List<String> getCat() {
		return cat;
	}
	public void setCat(List<String> cat) {
		this.cat = cat;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRuleStr() {
		return ruleStr;
	}
	public void setRuleStr(String ruleStr) {
		this.ruleStr = ruleStr;
	}
	
	public Object clone() throws CloneNotSupportedException{
			return super.clone();
	}

}
