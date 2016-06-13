/**                                               
 * <p>Title: DROOLS RULE ENGINE SYSTEM</p>          
 * <p>Description: 主要用于调用接口的参数类 </p>                           
 * <p>Copyright: Copyright (c) 2012 - 2015</p>    
 * <p>Company: Excel Technology International (Bei Jing) Limited</p>      
 * @author Johnson 
 * @Date 2013/03/15                                 
 * @version 1.0                                   
 */
package com.ruleEngine.dao;

public class RuleParam {
	private String BUSINESS_ID;  
	private String FUNCTION_ID;
	private String PROCESS_ID;
	private String RULE_FALG;
	private String GEN_RULE_ID;
	private String OPERATION;
	private String LAST_MODIFY_USER;
	/**
	 * 取得业务代码
	 * @return
	 */
	public String getBUSINESS_ID() {
		return BUSINESS_ID;
	}
	public void setBUSINESS_ID(String bUSINESS_ID) {
		BUSINESS_ID = bUSINESS_ID;
	}
	/**
	 * 取得取得功能ID
	 * @return
	 */
	public String getFUNCTION_ID() {
		return FUNCTION_ID;
	}
	public void setFUNCTION_ID(String fUNCTION_ID) {
		FUNCTION_ID = fUNCTION_ID;
	}
	/**
	 * 取得流程ID
	 * @return
	 */
	public String getPROCESS_ID() {
		return PROCESS_ID;
	}
	public void setPROCESS_ID(String pROCESS_ID) {
		PROCESS_ID = pROCESS_ID;
	}
	/**
	 * 取得规则类型
	 * @return
	 */
	public String getRULE_FALG() {
		return RULE_FALG;
	}
	public void setRULE_FALG(String rULE_FALG) {
		RULE_FALG = rULE_FALG;
	}
	/**
	 * 取得规则编号
	 * @return
	 */
	public String getGEN_RULE_ID() {
		return GEN_RULE_ID;
	}
	public void setGEN_RULE_ID(String gEN_RULE_ID) {
		GEN_RULE_ID = gEN_RULE_ID;
	}
	/**
	 * 取得操作结果
	 * @return
	 */
	public String getOPERATION() {
		return OPERATION;
	}
	public void setOPERATION(String oPERATION) {
		OPERATION = oPERATION;
	}
	/**
	 * 取得最后修改的用户
	 * @return
	 */
	public String getLAST_MODIFY_USER() {
		return LAST_MODIFY_USER;
	}
	public void setLAST_MODIFY_USER(String lAST_MODIFY_USER) {
		LAST_MODIFY_USER = lAST_MODIFY_USER;
	}
	
	

}
