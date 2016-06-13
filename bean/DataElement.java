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
//评分卡每个字段的取值范围
public class DataElement {
	
	private String highValue; //高值
	private String lowValue;  //低值
	private String score;    //分数
	public String getScore() {
		return score;
	}
	
	public void setScore(String score) {
		this.score = score;
	}
	public String getHighValue() {
		return highValue;
	}
	public void setHighValue(String highValue) {
		this.highValue = highValue;
	}
	public String getLowValue() {
		return lowValue;
	}
	public void setLowValue(String lowValue) {
		this.lowValue = lowValue;
	}

}
