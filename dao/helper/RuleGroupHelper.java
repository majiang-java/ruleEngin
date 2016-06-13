/**                                               
 * <p>Title: DROOLS RULE ENGINE SYSTEM</p>          
 * <p>Description: 主要用于动态生成编译和加载JavaBean的服务类 </p>                           
 * <p>Copyright: Copyright (c) 2012 - 2015</p>    
 * <p>Company: Excel Technology International (Bei Jing) Limited</p>      
 * @author    
 * @Date 2013/03/15                                
 * @version 1.0                                   
 */
package com.ruleEngine.dao.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ruleEngine.bean.ConditionBean;
import com.ruleEngine.bean.RuleBean;
import com.ruleEngine.bean.RuleGroupBean;
import com.ruleEngine.dao.RuleParam;
import com.ruleEngine.db.ConnectionHelper;

public class RuleGroupHelper {

	
	private static final Logger log = Logger.getLogger(RuleEngineFlowHelper.class);
	private static RuleGroupHelper ruleGroupHelper;
	public static RuleGroupHelper getInstance(){
		if(ruleGroupHelper == null){
			ruleGroupHelper = new RuleGroupHelper();
		}
		return ruleGroupHelper;
	}
	/**
	 * 从数据库中获取规则组数据
	 * @param ruleParam  
	 * @return RuleGroupBean 返回打包好的规则组数据
	 * @throws Exception
	 */
	public RuleGroupBean getRuleGroupFromDB(RuleParam ruleParam) throws Exception{
		RuleGroupBean ruleGroupBean = new RuleGroupBean();
		ruleGroupBean.setName(ruleParam.getGEN_RULE_ID());
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		StringBuffer ruleStr = new StringBuffer(500);
		try{
	//		ruleStr.append("select lg.GEN_RULE_CODE, lg.gen_rule_condn_str,rule_action (select  t.compl_rule_action from ls_compl_chklist_rule t where t.compl_rule_code = lg.GEN_RULE_CODE) FROM LS_GEN_RULE lg WHERE GEN_RULE_CODE in");
			ruleStr.append("   select l.COMPL_RULE_CODE,l.compl_rule_action from   ls_compl_chklist_rule l where l.LS_COMPL_CHKLIST_SEQ in");
			ruleStr.append("(select t.LS_COMPL_CHKLIST_SEQ from LS_COMPL_CHKLIST t where t.compl_chklist_code = ?)");
			conn = ConnectionHelper.getConnection();
			ps = conn.prepareStatement(ruleStr.toString());
			ps.setString(1, ruleParam.getGEN_RULE_ID());
			rs = ps.executeQuery();
			List<RuleBean> ruleList = new ArrayList<RuleBean>();
			while(rs.next()){
				RuleBean ruleBean = new RuleBean();
				ruleBean.setName(rs.getString("COMPL_RULE_CODE"));
				ruleBean.setAction(rs.getString("compl_rule_action"));
				getRuleFromDB(ruleBean);
				getRuleDataFromRuleStr(ruleBean);
				ruleList.add(ruleBean);
			}
			ruleGroupBean.setRuleBeanList(ruleList);
		}catch(Exception e){
			log.error("执行 getRuleGroupFromDB 出现错误",e);
			throw new Exception(e);
		}finally{
			ConnectionHelper.close(rs, ps,null, conn);
		}
		return ruleGroupBean;
		
	} 
	
	/**
	 * 
	 * @param ruleBean
	 * @return
	 * @throws Exception
	 */
	private RuleBean getRuleFromDB(RuleBean ruleBean) throws Exception{
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		StringBuffer ruleStr = new StringBuffer(500);
		try{
			ruleStr.append("select lg.GEN_RULE_CODE, lg.gen_rule_condn_str FROM LS_GEN_RULE lg WHERE GEN_RULE_CODE =?");
		
			conn = ConnectionHelper.getConnection();
			ps = conn.prepareStatement(ruleStr.toString());
			ps.setString(1, ruleBean.getName());
			rs = ps.executeQuery();
			while(rs.next()){
				
				ruleBean.setRuleStr(rs.getString("gen_rule_condn_str"));
			}
		}catch(Exception e){
			log.error("执行 getRuleFromDB 出现错误",e);
			throw new Exception(e);
		}finally{
			ConnectionHelper.close(rs, ps,null, conn);
		}  
		return ruleBean;
		
	}
	/**
	 * 
	 * @param ruleBean
	 * @return
	 * @throws Exception
	 */
	private RuleBean getRuleDataFromRuleStr(RuleBean ruleBean) throws Exception{
		
	   	String strGenRuleCondnStr = null;
    	String temp = strGenRuleCondnStr = ruleBean.getRuleStr();
		if(strGenRuleCondnStr.indexOf("&")>-1)
			strGenRuleCondnStr = strGenRuleCondnStr.replaceAll("&&",",");
		if(strGenRuleCondnStr.indexOf("|")>-1)
			strGenRuleCondnStr = strGenRuleCondnStr.replace("||",",");
		if(strGenRuleCondnStr.indexOf("(")>-1)
			strGenRuleCondnStr = strGenRuleCondnStr.replaceAll("\\(","");
		if(strGenRuleCondnStr.indexOf(")")>-1)
			strGenRuleCondnStr = strGenRuleCondnStr.replaceAll("\\)","");
		if(strGenRuleCondnStr.indexOf(" ")>-1)
			strGenRuleCondnStr = strGenRuleCondnStr.replaceAll(" ","");
		String[] strCodes = strGenRuleCondnStr.split(",");
		//OHDbAccess ohPostCommit = new OHDbAccess(this,getConnection(),getUserProfile());
		Map<String, ConditionBean> hm = getConditionData(strCodes);
	
		ruleBean.setCat(getRuleCat(hm));
		int a = 0 ;
		for (int i = 0; i < strCodes.length; i++) {
			if (strGenRuleCondnStr.indexOf(strCodes[i])!= strGenRuleCondnStr.lastIndexOf(strCodes[i])) {
				a=1;
				break;
			}
		}
		if(a == 1){
			throw new Exception("禁止重复的条件代码");
		}
		for (int i = 0; i < strCodes.length; i++) {
			if(temp.contains(strCodes[i])){
				if(hm.get(strCodes[i]) == null || "".equals(hm.get(strCodes[i]))){
					throw new Exception("条件："+strCodes[i]+"在库中没有对应的RULEDATA");
				}
				temp = temp.replace(strCodes[i], hm.get(strCodes[i]).getDroolsDate());
			}
		}
		StringBuffer sbRule = new StringBuffer(300);
		sbRule.append(createRuleHead(ruleBean));
		sbRule.append(" rule ").append("'"+ruleBean.getName()+"'").append("\n");
		sbRule.append(" when ").append("\n");
		
		for (int i = 0; i < ruleBean.getCat().size(); i++) {
			sbRule.append("$"+ruleBean.getCat().get(i)+":"+ruleBean.getCat().get(i)+"(); ").append("\n");
		}
		sbRule.append("eval(").append(temp).append(");").append("\n");
		sbRule.append(" then ").append("\n");
		
	//	sbRule.append("mylist.add(\""+param.get("GEN_RULE_CODE").toString()+"\");").append("\n");
	//	sbRule.append(" RuleBean ruleBean = new RuleBean();").append("\n");
		sbRule.append(" ruleBean.setResult(true);").append("\n");
	//	sbRule.append(" mylist.add(ruleBean);").append("\n");
		sbRule.append(" end").append("\n");
		
		ruleBean.setGenData(sbRule.toString());
	
		return ruleBean;
	}
	
	
	/**
	 * 从条件中获取类别
	 * @param hm 传入条件数据
	 * @return
	 */
	private List<String> getRuleCat(Map<String, ConditionBean> hm){
		
		List<String> cache = new ArrayList<String>();
		for (Map.Entry<String,ConditionBean> entry : hm.entrySet()) {
			List<String> list = entry.getValue().getCats();
			for (String str : list) {
				if(!cache.contains(str)){
					cache.add(str);
				}
			}
		}
		return cache;
		
	}
	/**
	 * 从数据库中获得条件数据
	 * @param strs 传入的条件列表
	 * @return
	 * @throws Exception
	 */
	private Map<String, ConditionBean> getConditionData(String[] strs) throws Exception{
		Map<String, ConditionBean> hm = new HashMap<String, ConditionBean>();
	//	List<ConditionBean> condiList = new ArrayList<ConditionBean>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT t.GEN_CONDN_CODE,t.GEN_CONDN_OPP1_CAT,GEN_CONDN_OPP2_CAT,t.GEN_CONDN_DATA FROM LS_GEN_CONDN t WHERE t.GEN_CONDN_CODE in (");
				for (int i = 0; i < strs.length; i++) {
					if(i!=strs.length-1){
						sb.append("'").append(strs[i]).append("'").append(",");
					}else{
						sb.append("'").append(strs[i]).append("'");
					}
				}
				sb.append(")");
			//	System.out.println(sb.toString());
		Connection conn = ConnectionHelper.getConnection();	
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			pstmt = conn.prepareStatement(sb.toString());
			rs = pstmt.executeQuery();
			while(rs.next()){
				ConditionBean conditionBean = new ConditionBean();
				//System.out.println(rs.getString("GEN_CONDN_DATA"));
				for(int i = 0; i <strs.length; i++){
					if(strs[i].equals(rs.getString("GEN_CONDN_CODE"))){
						conditionBean.setName(strs[i]);
						conditionBean.setDroolsDate(rs.getString("GEN_CONDN_DATA"));
						conditionBean.setCats(getCatFromRs(rs));
						hm.put(strs[i], conditionBean);
					}
				}
			}
		}catch(Exception e){
			log.error("执行 getRuleFromDB 出现错误",e);
			throw new Exception(e);
		}finally{
			ConnectionHelper.close(rs, pstmt,null, conn);
		}
		return hm;
	}
	
	
	/**
	 * 从ResultSet 中拿去类别
	 * @param rs 
	 * @return  list 返回类别列表
	 * @throws SQLException
	 */
	private List<String> getCatFromRs(ResultSet rs) throws SQLException{
		
		String cat1 = rs.getString("GEN_CONDN_OPP1_CAT");
		String cat2 = rs.getString("GEN_CONDN_OPP2_CAT");
		String[] strCat1 = null;
		String[] strCat2 = null;
		if(cat1 != null && cat1!=""){
			strCat1 = cat1.split(",");
		}else{
			strCat1 = new String[0];
		}
		if(cat2 != null && cat2!=""){
			strCat2 = cat2.split(",");
		}else{
			strCat2 = new String[0];
		}
		List<String> cache = new ArrayList<String>();
		for(int i = 0;i<strCat1.length;i++){
			if(!cache.contains(strCat1[i])){
				cache.add(strCat1[i]);
			}
		}
		for(int j = 0;j<strCat2.length;j++){
			if(!cache.contains(strCat2[j])){
				cache.add(strCat2[j]);
			}
		}
		//System.out.println("1"+cache);
		return cache;
	}
	
	/**
	 * 拼装规则头
	 * @param ruleBean 规则数据
	 * @return String 拼接成的字符串
	 */
	private static String createRuleHead(RuleBean ruleBean){
		StringBuffer sb = new StringBuffer(500);
		sb.append("package com.ruleEngine.rulegroup;").append("\n");
		List<String> list = ruleBean.getCat();
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			sb.append("import com.ruleEngine.domain."+iterator.next()+";").append("\n");
		}
		sb.append("import com.ruleEngine.dao.RuleEngineFlowAdpter;").append("\n");
		//sb.append("import com.ruleEngine.bean.RuleBean;").append("\n");
		sb.append("import java.util.Date;").append("\n");
		//sb.append("global java.util.List mylist;").append("\n");
		sb.append("global com.ruleEngine.bean.RuleBean ruleBean;").append("\n");
		return sb.toString();
	}
	

//	public static void main(String[] args) {
//		System.out.println("".split(","));
//	}
}
