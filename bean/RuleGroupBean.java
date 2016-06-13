/**                                               
  * <p>Title: DROOLS RULE ENGINE SYSTEM</p>          
 * <p>Description: 装载规则组的数据结构 </p>                           
 * <p>Copyright: Copyright (c) 2012 - 2015</p>    
 * <p>Company: Excel Technology International (Bei Jing) Limited</p>      
 * @author    
 * @Date 2013/03/15                                
 * @version 1.0                                   
 */
package com.ruleEngine.bean;

import java.util.List;

public class RuleGroupBean {
	
	private String name;           //规则组名称
	private List<RuleBean> ruleBeanList;  //规则列表
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<RuleBean> getRuleBeanList() {
		return ruleBeanList;
	}
	public void setRuleBeanList(List<RuleBean> ruleBeanList) {
		this.ruleBeanList = ruleBeanList;
	}

	
}
