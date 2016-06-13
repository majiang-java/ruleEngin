/**                                               
 * <p>Title: DROOLS RULE ENGINE SYSTEM</p>          
 * <p>Description: 主要用于动态生成编译和加载JavaBean的服务类 </p>                           
 * <p>Copyright: Copyright (c) 2012 - 2015</p>    
 * <p>Company: Excel Technology International (Bei Jing) Limited</p>      
 * @author    
 * @Date 2013/03/15                                
 * @version 1.0                                   
 */
package com.ruleEngine.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Random;

import com.ruleEngine.bean.RuleBean;
import com.ruleEngine.db.ConnectionHelper;

public class CommonBussiness {

	/**
	 * 处理规则产生的结果
	 * @param ruleParam 传入的参数
	 * @param list  规则返回的结果
	 * @return Boolean 返回在当前数据下使用此规则能否通过
	 * @throws Exception
	 */
	public static boolean processResult(RuleParam ruleParam,List<RuleBean> list) throws Exception {
		insertRuleTab(ruleParam,list);
		return processRuturnResult(ruleParam,list);
	}
	/**
	 * 统一处理结果
	 * @param ruleParam 传入的参数
	 * @param list  规则返回的结果
	 * @return 
	 * @throws Exception
	 */
	private static boolean processRuturnResult(RuleParam ruleParam,List<RuleBean> list) throws Exception {
		boolean flag = false;
	//	int count = RuleEngineDaoImpl.getCountFromRuleGroup(ruleParam.getGEN_RULE_ID());
		for (int i = 0; i < list.size(); i++) {
			RuleBean ruleBean = list.get(i);
			if(list.get(i).isResult() == false){
				if(ruleBean.getAction() == null||ruleBean.getAction().equals("REJT")||ruleBean.getAction().equals("")
				){
					flag = false;
				}else{
					flag = true;
				}
			}else{
				flag = true;
			}
		}
		
		return flag;
	}


	/**
	 * 统一插入UN_THG_RULES表
	 * @param ruleParam  传入的规则数据
	 * @param list  从规则中收集的结果
	 * @throws Exception
	 */
	private static void insertRuleTab(RuleParam ruleParam,List<RuleBean> list) throws Exception{
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
		
		try{
			StringBuffer sb = new StringBuffer(400);
			sb.append("INSERT INTO UN_THG_RULES (");
			sb.append("BUSINESS_ID");      
			sb.append(",FUNCTION_ID");     
			sb.append(",PROCESS_ID");      
			sb.append(",RULE_FALG"); 
			sb.append(",UN_THG_RULE_ID");
			sb.append(",OPERATE"); 
			sb.append(",RULE_DESC");  
			sb.append(",PROCESS_RESULT");
			sb.append(",PARAM_ID"); 
			sb.append(",VER");             
			sb.append(",LAST_MODIFY_DATA");
			sb.append(",LAST_MODIFY_USER) ");
			sb.append(" VALUES( ");
			sb.append("'").append(ruleParam.getBUSINESS_ID()).append("'").append(",");
			sb.append("'").append(ruleParam.getFUNCTION_ID()).append("'").append(",");
			sb.append("'").append(ruleParam.getPROCESS_ID()).append("'").append(",");
			sb.append("'").append(ruleParam.getRULE_FALG()).append("'").append(",");
			sb.append("?").append(",");
			sb.append("'").append(ruleParam.getOPERATION()).append("'").append(",");
			sb.append("?,");
			sb.append("?,");
			sb.append("?,");
			sb.append("'1'").append(",");
			sb.append("sysdate").append(",");
			sb.append("'").append(ruleParam.getLAST_MODIFY_USER()).append("'").append(")");
			
			conn = ConnectionHelper.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			pstmt = conn.prepareStatement((sb.toString().replace("'null'", "NULL")));
			for (int i = 0; i < list.size(); i++) {
				RuleBean ruleBean= list.get(i);
				String getsql = "select GEN_RULE_PRI_DESC　from ls_gen_rule where gen_rule_code ='";
				rs = stmt.executeQuery(getsql+ruleBean.getName()+"'");
				pstmt.setString(1, ruleBean.getName());
				rs.next();
				pstmt.setString(2, rs.getString("GEN_RULE_PRI_DESC"));
				pstmt.setString(3, ruleBean.isResult()+"");
				pstmt.setString(4, ruleParam.getGEN_RULE_ID());
				pstmt.addBatch();
				rs.close();
			}
			pstmt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		}catch(Exception e){
			conn.rollback();
			conn.setAutoCommit(true);
			throw new Exception(e);
		}finally{
			ConnectionHelper.close(rs, pstmt, stmt, conn);
		}

	}
	
	/**
	 * 产生48位16进制序列
	 * @return
	 */
	public static String createFileSeq(){
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			sb.append(Integer.toHexString(random.nextInt()));
		}
		return sb.toString();
	}
}
