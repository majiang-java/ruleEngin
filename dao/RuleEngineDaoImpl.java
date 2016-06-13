/**                                               
 * <p>Title: DROOLS RULE ENGINE SYSTEM</p>          
 * <p>Description: 主要用于规则引擎包装的服务类 </p>                           
 * <p>Copyright: Copyright (c) 2012 - 2015</p>    
 * <p>Company: Excel Technology International (Bei Jing) Limited</p>      
 * @author Johnson 
 * @Date 2013/03/15                               
 * @version 1.0                                   
 */ 
package com.ruleEngine.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.drools.runtime.StatefulKnowledgeSession;
import org.springframework.jdbc.support.lob.LobHandler;

import com.ruleEngine.dao.helper.RuleEngineFlowHelper;
import com.ruleEngine.db.ConnectionHelper;
import com.ruleEngine.util.RuleResourseUtil;
/**
 * 此类为规则引擎模块引擎部分的中枢类，其内容包括规则的调用逻辑流程，规则组调用逻辑流程，决策表调用逻辑流程
 * 规则流部分调用逻辑流程
 * @author Johnson Ma
 *
 */
public class RuleEngineDaoImpl {
	
	
	private LobHandler lobHandler;

	/**
	 * 从spring接受LobHandler，LobHandler的作用是处理clob字段
	 * @return
	 */
	public LobHandler getLobHandler() {
		return lobHandler;
	}

	private static final Logger log = Logger.getLogger(RuleEngineDaoImpl.class);

    

	/**
	 * 从LS_GEN_RULE业务表中获得相应的规则数据
	 * 其中他为中枢方法，规则，规则组，决策表的查询是在此方法中直接查询出，但是规则流需要借用RuleEngineFlowHelper
	 * 查处
	 * @param ruleParam 传入的参数
	 * @return Map  返回的结果 key为规则编号， value为drools数据　其中规则流中的为 key为规则流编号，value为XML数据
	 * @throws Exception 
	 */
	public Map<String, String> getRuleStr(RuleParam ruleParam) throws Exception{
		
		Map<String, String> hm = new HashMap<String, String>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String ruleStr = null;
		if(RuleEngineType.RULE.equalsIgnoreCase(ruleParam.getRULE_FALG())){
			log.info("get rule start");
			ruleStr = "select * FROM LS_GEN_RULE lg WHERE GEN_RULE_CODE = ?";
			try{
				conn = ConnectionHelper.getConnection();
				ps = conn.prepareStatement(ruleStr);
				ps.setString(1, ruleParam.getGEN_RULE_ID());
				rs = ps.executeQuery();
				rs.getMetaData().toString();
				if(rs.next()){
					
					hm.put("GEN_RULE_CODE", rs.getString("GEN_RULE_CODE"));
					hm.put("GEN_RULE_DATA", lobHandler.getClobAsString(rs, "GEN_RULE_DATA"));
				}
				
			}catch(Exception e){
				log.error("执行 getRuleStr rule 出现错误",e);
				throw new Exception(e);
			}finally{
				ConnectionHelper.close(rs, ps, null,conn);
			}
		}else if(RuleEngineType.RULE_GROUP.equalsIgnoreCase(ruleParam.getRULE_FALG())){
			log.info("get ruleGroup start");
			ruleStr = "select t.COMPL_CHKLIST_CODE,t.GEN_RULEGROUP_DATA from LS_COMPL_CHKLIST t where t.compl_chklist_code =?";
			try{
				conn = ConnectionHelper.getConnection();
				ps = conn.prepareStatement(ruleStr);
				ps.setString(1, ruleParam.getGEN_RULE_ID());
				rs = ps.executeQuery();
			
				if(rs.next()){
					
					hm.put("GEN_RULE_CODE", rs.getString("COMPL_CHKLIST_CODE"));
					hm.put("GEN_RULE_DATA", (lobHandler.getClobAsString(rs, "GEN_RULEGROUP_DATA")));
				}
			}catch(Exception e){
				log.error("执行 getRuleStr ruleGroup 出现错误",e);
				throw new Exception(e);
			}finally{
				ConnectionHelper.close(rs, ps,null, conn);
			}
		}else if(RuleEngineType.DECISION_TAB.equalsIgnoreCase(ruleParam.getRULE_FALG())){
			log.info("get decisionTab start");
			ruleStr = "select t.d_Tab_Name,t.d_tab_url,t.d_tab_desc from decitab t where t.rule_id =?";
			try{
				conn = ConnectionHelper.getConnection();
				ps = conn.prepareStatement(ruleStr);
				ps.setString(1, ruleParam.getGEN_RULE_ID());
				rs = ps.executeQuery();
			
				if(rs.next()){
					
					hm.put("d_Tab_Name", rs.getString("d_Tab_Name"));
					hm.put("d_tab_url", rs.getString("d_tab_url"));
				}
			}catch(Exception e){
				log.error("执行 getRuleStr ruleGroup 出现错误",e);
				throw new Exception(e);
			}finally{
				ConnectionHelper.close(rs, ps,null, conn);
			}
		}else if(RuleEngineType.RULE_FLOW.equalsIgnoreCase(ruleParam.getRULE_FALG())){
			hm = RuleEngineFlowHelper.getInstance().getRuleFlowData(ruleParam, lobHandler);
			
		}
		
		return hm; 	
	}

	/**
	 * 拼接rule字符串
	 * @param hm map
	 * @return String 返回规则drools数据
	 * @throws Exception 
	 */
	public String spilitRule(Map<String, String> hm) throws Exception{
		StringBuffer sb = new StringBuffer(500);
		sb.append(genRuleHeader());
		
		sb.append(" rule ").append("'"+hm.get("GEN_RULE_CODE").toString()+"'").append("\n");
		sb.append(hm.get("GEN_RULE_DATA")).append("\n");
		
		return sb.toString();
	}
	/**
	 * 产生rule文件头的公共方法
	 * @param hm
	 * @return
	 * @throws Exception
	 */
	public static String genRuleHeader() throws Exception{
		StringBuffer sb = new StringBuffer(500);
		sb.append("package com.ruleEngine.rulegroup;").append("\n");
		List<String> list = genCatData();
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			sb.append("import com.ruleEngine.domain."+iterator.next()+";").append("\n");
		}
		sb.append("import com.ruleEngine.dao.RuleEngineFlowAdpter;").append("\n");
		sb.append("import java.util.Date;").append("\n");
		sb.append("global java.util.List mylist;").append("\n");
		sb.append("global java.util.Map map;").append("\n");
		return sb.toString();
	}
	
	/**
	 * 拼接ruleGroup字符串
	 * @param hm
	 * @return
	 * @throws Exception 
	 */
	public String spilitRuleGroup(Map<String, String> hm) throws Exception{
		StringBuffer sb = new StringBuffer(1000);
		sb.append(genRuleHeader());
		sb.append(hm.get("GEN_RULE_DATA")).append("\n");
		
		return sb.toString();
	}
	/**
	 * 获取编译的Session
	 * 其方法主要为中枢逻辑方法，通过判断不同的类型来调用不同的规则数据库查询方法，从而得出
	 * @param ruleId
	 * @param ruleFlag
	 * @return
	 * @throws Exception 
	 */
	public StatefulKnowledgeSession getComplileSession (RuleParam ruleParam) throws Exception{
		
		StatefulKnowledgeSession ksession = null;
		String ruleStr = null;
		Map<String, String> map =  getRuleStr(ruleParam);
		try{
			if(RuleEngineType.RULE.equalsIgnoreCase(ruleParam.getRULE_FALG())){
				ruleStr = spilitRule(map);
				ksession = RuleResourseUtil.getDrlResourseFromStream(ruleStr);
			}else if(RuleEngineType.RULE_GROUP.equalsIgnoreCase(ruleParam.getRULE_FALG())){
				ruleStr = spilitRuleGroup(map);
				ksession = RuleResourseUtil.getDrlResourseFromStream(ruleStr);
			}else if(RuleEngineType.DECISION_TAB.equalsIgnoreCase(ruleParam.getRULE_FALG())){
				ksession = RuleResourseUtil.getXLSResourse("file:"+map.get("d_tab_url").toString());
			}else if(RuleEngineType.RULE_FLOW.equalsIgnoreCase(ruleParam.getRULE_FALG())){
				Map<String,String> ruleGroupDatahm = RuleEngineFlowHelper.getInstance().makeGroupRuleFlowData(ruleParam, lobHandler);
				ksession = RuleResourseUtil.getDrlAndFlowResourseFromStream(ruleGroupDatahm, map.get("RULE_FLOW_DATA").toString());
			}
	//		System.out.println(ruleStr);
			log.debug("Generate the rule file :"+ruleStr);
			//ksession = RuleResourseUtil.getDrlResourseFromStream(ruleStr);
		}catch(Exception e){
			log.error("获取编译的Session getComplileSession报错",e);
			throw new Exception(e);
		}
		return ksession;
	}
	

	public void setLobHandler(LobHandler lobHandler) {
		this.lobHandler = lobHandler;
	}
	/**
	 * 插入不通过的ruleID
	 * 此方法为中枢方法，任何的插UN_THG_RULES表的时候都需要调用此方法
	 * @param unThgRule
	 * @param ruleParam
	 * @throws Exception 
	 */
	public void insertRule(List<String> unThgRule,RuleParam ruleParam) throws Exception{
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
			sb.append("'1'").append(",");
			sb.append("sysdate").append(",");
			sb.append("'").append(ruleParam.getLAST_MODIFY_USER()).append("'").append(")");
			
			conn = ConnectionHelper.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			if(RuleEngineType.DECISION_TAB.equalsIgnoreCase(ruleParam.getRULE_FALG())){
				
				String getsql = "select d_tab_desc　from decitab where rule_id ='";
				pstmt = conn.prepareStatement((sb.toString().replace("'null'", "NULL")));
				rs = stmt.executeQuery(getsql+ruleParam.getGEN_RULE_ID()+"'");
				pstmt.setString(1, ruleParam.getGEN_RULE_ID().toString());
				if( rs.next() ){
					pstmt.setString(2, rs.getString("d_tab_desc"));
				}else{
					pstmt.setString(2, "");
				}
				pstmt.setString(3, unThgRule.get(0));
				pstmt.execute();
			}else if(RuleEngineType.RULE.equalsIgnoreCase(ruleParam.getRULE_FALG())||
					RuleEngineType.RULE_GROUP.equalsIgnoreCase(ruleParam.getRULE_FALG())){
				String getsql = "select GEN_RULE_PRI_DESC　from ls_gen_rule where gen_rule_code ='";
				pstmt = conn.prepareStatement((sb.toString().replace("'null'", "NULL")));
				for (int i = 0; i < unThgRule.size(); i++) {
					rs = stmt.executeQuery(getsql+unThgRule.get(i)+"'");
					pstmt.setString(1, unThgRule.get(i).toString());
					rs.next();
					pstmt.setString(2, rs.getString("GEN_RULE_PRI_DESC"));
					pstmt.setString(3, "");
					pstmt.addBatch();
					rs.close();
				}
				pstmt.executeBatch();
			}else if(RuleEngineType.RULE_FLOW.equalsIgnoreCase(ruleParam.getRULE_FALG())){
				
				
			}
			conn.commit();
			conn.setAutoCommit(true);
		}catch(Exception e){
			try {
				conn.rollback();
				conn.setAutoCommit(true);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
			} catch (SQLException e1) {
				throw new Exception(e);
			}
			log.error("执行insertRule 方法出错");
			throw new Exception(e);
			
		}finally{
			ConnectionHelper.close(rs, pstmt, stmt, conn);
		}
		
		
	}
	/**
	 * 此方法为处理结果的中枢逻辑方法，其作用是调用不同的
	 * @param thgRule 规则返回的通过的规则
	 * @param ruleParam 传入的参数
	 * @throws Exception
	 */
	public void processUnthgRule(List<String> thgRule,RuleParam ruleParam) throws Exception{
		if(RuleEngineType.RULE.equals(ruleParam.getRULE_FALG())){
			List<String> list = new ArrayList<String>();
			list.add(ruleParam.getGEN_RULE_ID());
			insertRule(list,ruleParam);
			
		}else if(RuleEngineType.RULE_GROUP.equals(ruleParam.getRULE_FALG())){
			List<String> list = processGroupUnThgRule(ruleParam.getGEN_RULE_ID(),thgRule,ruleParam);
			insertRule(list,ruleParam);
		}
	}
	

	/**
	 * 此方法为rulegroup特有的方法，
	 * 通过传入的ruleParam调用数据库查询全部通过的对照列表组，并且调用processList来得到没有通过的规则列表
	 * @param COMP_CHKLIST_CODE
	 * @param thgRule 规则执行后成功的规则列表
	 * @param ruleParam
	 * @return
	 * @throws Exception
	 */

	public List<String> processGroupUnThgRule(String COMP_CHKLIST_CODE,List<String> thgRule,RuleParam ruleParam) throws Exception{
		List<String> allRule =  Collections.synchronizedList(new LinkedList<String>());
		List<String> unThgRule = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String getALLRule = "SELECT ls.COMPL_RULE_CODE FROM LS_COMPL_CHKLIST_RULE ls " +
				"WHERE ls.LS_COMPL_CHKLIST_SEQ in (SELECT LS_COMPL_CHKLIST_SEQ FROM LS_COMPL_CHKLIST WHERE COMPL_CHKLIST_CODE = ?)";
		
		try{
			//String sql = "";
			conn = ConnectionHelper.getConnection();
			pstmt = conn.prepareStatement(getALLRule);
			pstmt.setString(1, COMP_CHKLIST_CODE);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				allRule.add(rs.getString("COMPL_RULE_CODE"));
			}
			unThgRule = processList(allRule,thgRule);
		}catch(Exception e){
			log.info("执行processGroupUnThgRule方法出错",e);
			throw new Exception(e);
		}finally{
			ConnectionHelper.close(rs, pstmt, null, conn);
		}
		return unThgRule;
		
		
	}
	/**
	 * 获取处理没有通过的规则列表
	 * @param allRule 全部的规则列表
	 * @param thgRule 全部通过的规则列表
	 * @return list　没有通过的规则列表
	 */
	public List<String> processList(List<String> allRule, List<String> thgRule){
		List<String> unThgRule = new ArrayList<String>();
		for (int j = 0; j < thgRule.size(); j++) {
			synchronized(allRule) {
				for (int i = 0; i < allRule.size(); i++) {
					if(allRule.get(i).equals(thgRule.get(j))){
						allRule.remove(i);
					}
				}
			}
		}
		unThgRule = allRule;
		return Collections.unmodifiableList(unThgRule);
	}
	/**
	 * 此为规则组的特有方法
	 * 通过数据库得到全部的规则数，用户和规则之后后返回的通过的规则列表比较，从而得出是否为成功全部执行
	 * 取得规则的总数
	 * @param COMP_CHKLIST_CODE
	 * @return
	 * @throws Exception
	 */
	public static int getCountFromRuleGroup(String COMP_CHKLIST_CODE) throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = -1;
		String getALLRule = "SELECT COUNT(*) COUNT FROM LS_COMPL_CHKLIST_RULE ls " +
				"WHERE ls.LS_COMPL_CHKLIST_SEQ in (SELECT LS_COMPL_CHKLIST_SEQ FROM LS_COMPL_CHKLIST WHERE COMPL_CHKLIST_CODE = ?)";
		
		try{
			//String sql = "";
			conn = ConnectionHelper.getConnection();
			pstmt = conn.prepareStatement(getALLRule);
			pstmt.setString(1, COMP_CHKLIST_CODE);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				count = rs.getInt("COUNT");
			}
			//unThgRule = processList(allRule,thgRule);
		}catch(Exception e){
			log.info("执行processGroupUnThgRule方法出错",e);
			throw new Exception(e);
		}finally{
			ConnectionHelper.close(rs, pstmt, null, conn);
		}
		return count;
	}
	
	/**
	 * 此方法为中枢方法，处理规则结果
	 * 通过判断规则类型，从而调用相应处理规则结果的方法
	 * 比如：
	 * <Strong>类型</Strong>为：　RULE 则会判断返回的列表是否是空的，这样就就判断那一条规则是否执行成功
	 * <Strong>类型</Strong>为 ： RULEGROUP 这回和之前getCountFromRuleGroup方法查询出的规则列表总数经行比较，
	 * 若不成功则把不通过的规则通过processUnthgRule调用此方法,然后插入到UN_THG_RULES表中
	 * <Strong>类型</Strong>为：DECISION_TAB 会把list中的分数的相加，然后插入到UN_THG_RULES表中
	 * <Strong>类型</Strong>为：RULE_FLOW 当前设计为把不同过的规则通过调用processRuleFlowResult类，把不通过的规则插入到
	 * UN_THG_RULES表中
	 * @param resultList 规则处理后的list
	 * @param param 参数
	 * @return true/false
	 * @throws Exception
	 */
	public boolean processRuleResult(List resultList,RuleParam param) throws Exception{
		
		boolean flag = false;
		if(RuleEngineType.RULE.equalsIgnoreCase(param.getRULE_FALG())){
			if(resultList.size()>0){
				flag = true;
			}else{
				processUnthgRule(resultList,param);
				flag = false;
			}
			
		}else if(RuleEngineType.RULE_GROUP.equalsIgnoreCase(param.getRULE_FALG())){
			int count = getCountFromRuleGroup(param.getGEN_RULE_ID());
			if(resultList.size() == count){
				flag = true;
			}else{
				processUnthgRule(resultList,param);
				flag = false;
			}
			
		}else if(RuleEngineType.DECISION_TAB.equalsIgnoreCase(param.getRULE_FALG())){
			double result = 0;
			for (int i = 0; i < resultList.size(); i++) {
				result += Double.parseDouble(resultList.get(i)+"");
			}
			List<String> list = new ArrayList<String>();
			list.add(result+"");
			insertRule(list,param);
			flag = true;
		}else if(RuleEngineType.RULE_FLOW.equalsIgnoreCase(param.getRULE_FALG())){
			
		}
		return flag;
		
	}
	/**
	 * 此方法为在生成java类的时候从数据库中查找的方法，
	 * 通过传入的规则类的类型来查找具体的字段和此字段的数据类型
	 * @param cat key为字段的值 ，value为对应字段的数据类型
	 * @return map key
	 * @throws Exception
	 */
	public Map<String, String> genFieldData(String cat) throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
	    Map<String, String> hm = new ConcurrentHashMap<String, String>();
		try{
			String sql = "SELECT GEN_FIELD_COL,GEN_FIELD_DATA_TYPE FROM LS_GEN_FIELD WHERE GEN_FIELD_COL_COND IS NULL AND GEN_FIELD_COMPL_IND = 'Y' AND GEN_FIELD_CAT =?";
			conn = ConnectionHelper.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cat);
			rs = pstmt.executeQuery();
			while(rs.next()){
				hm.put(rs.getString("GEN_FIELD_COL").replace(" ", ""),rs.getString("GEN_FIELD_DATA_TYPE"));
			}
		}catch(Exception e){
			log.info("执行genJavaFile方法出错",e);
			throw new Exception(e);
		}finally{
			ConnectionHelper.close(rs, pstmt, null, conn);
		}
		return hm;
	}

	/**
	 * 取得规则类的类型 例如 APPT APPL
	 * @return
	 * @throws Exception
	 */
	public static List<String> genCatData() throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
	    List<String> list = new ArrayList<String>();
	   // Map hm = new HashMap();
		try{
			String sql = "SELECT COM_CDE FROM COM_CDE WHERE TAB_NAM='FLDCAT'";
			conn = ConnectionHelper.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()){
				list.add(rs.getString("COM_CDE"));
			}
		}catch(Exception e){
			log.info("执行genJavaFile方法出错",e);
			throw new Exception(e);
		}finally{
			ConnectionHelper.close(rs, pstmt, null, conn);
		}
		return list;
	}
	
	/**
	 * 此方法为生成java类取得数据的数据封装的逻辑方法
	 * 通过调用genCatData 取得类型， 通过调用genFieldData 去的字段
	 * 最后把数据类型 和对应的数据类型的所有字段的map 放入一个map中
	 * 处理封装获得的数据
	 * @return map key 例如 APPL， value APPL所对应的所有字段的map
	 * @throws Exception
	 */
	public Map<String, Map<String, String>> processJavaFileData() throws Exception{
		List<String> catList = genCatData();
		Map<String, Map<String, String>> resulthm = new ConcurrentHashMap<String, Map<String, String>>();
		for (int i = 0; i < catList.size(); i++) {
			Map<String, String> fieldHm = genFieldData(catList.get(i).toString());
			resulthm.put(catList.get(i).toString(), fieldHm);
		}
		return resulthm;
	}

	/**
	 * 此方法为处理规则流结果的特有方法
	 * 因为规则流的结果有3个层次 
	 * 第一层次为 规则流
	 * 第二层次为 规则组
	 * 第三层次为规则
	 * 此方法要做的是查处规则流中所有的规则组的所有规则，然后通过迭代把通过makeRuleData得到  
	 * Map<String, Map<String, String>>类型和规则流返回的Map<String,String[]>做比较
	 * 其中规则流返回的map的key是规则流代码，String[] 规则流中是所有的通过的规则的代码
	 * 从而得出
	 * @param ruleParam
	 * @param b 规则流最后结果
	 * @param hm 规则流内部记录的通过的规则的数据
	 * @throws Exception
	 */
	public void processRuleFlowResult(RuleParam ruleParam, boolean b, Map hm) throws Exception {
		Map returnHm = new ConcurrentHashMap();
		Map<String, Map<String, String>> dataHm = RuleEngineFlowHelper.getInstance().makeRuleData(ruleParam, lobHandler);
		for (Iterator dataHmIter = dataHm.entrySet().iterator(), hmIter = hm.entrySet().iterator(); dataHmIter.hasNext()&&hmIter.hasNext();) {
			Map group = new HashMap();
			Map.Entry dataHmEntry = (Map.Entry) dataHmIter.next();
			Map.Entry hmEntry = (Map.Entry) hmIter.next();
			String[] strs = (String[]) hmEntry.getValue();
			List strList = Arrays.asList(strs);
			Map allDataHm = (Map) dataHmEntry.getValue();
			int i = 0;
			for (Object str : allDataHm.keySet()) {
				if(strList.contains(str.toString())){
					
				}
				i++;
			}
		}
	}
	
	
	/**
	 * 
	 */
	
	
	
}
