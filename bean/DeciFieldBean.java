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

public class DeciFieldBean {

	private String genFieldPriDesc; //描述
	private String genFieldCol;  //字段
	private String genFieldCat;   //字段类别
	private String genFieldDataType; //类型
	private String genFieldSeq;  //字段序列
	public String getGenFieldSeq() {
		return genFieldSeq;
	}
	public void setGenFieldSeq(String genFieldSeq) {
		this.genFieldSeq = genFieldSeq;
	}
	private List<DataElement>  dataElement;  //字段所包含的元素

	
	public List<DataElement> getDataElement() {
		return dataElement;
	}
	public void setDataElement(List<DataElement> dataElement) {
		this.dataElement = dataElement;
	}
	public String getGenFieldPriDesc() {
		return genFieldPriDesc;
	}
	public void setGenFieldPriDesc(String genFieldPriDesc) {
		this.genFieldPriDesc = genFieldPriDesc;
	}
	public String getGenFieldCol() {
		return genFieldCol;
	}
	public void setGenFieldCol(String genFieldCol) {
		this.genFieldCol = genFieldCol;
	}
	public String getGenFieldCat() {
		return genFieldCat;
	}
	public void setGenFieldCat(String genFieldCat) {
		this.genFieldCat = genFieldCat;
	}
	public String getGenFieldDataType() {
		return genFieldDataType;
	}
	public void setGenFieldDataType(String genFieldDataType) {
		this.genFieldDataType = genFieldDataType;
	}
	
}
