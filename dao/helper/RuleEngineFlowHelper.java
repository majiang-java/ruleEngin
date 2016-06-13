/**                                               
 * <p>Title: DROOLS RULE ENGINE SYSTEM</p>          
 * <p>Description: 主要用于规则引擎包装的服务类 </p>                           
 * <p>Copyright: Copyright (c) 2012 - 2015</p>    
 * <p>Company: Excel Technology International (Bei Jing) Limited</p>      
 * @author Johnson Ma  
 * @Date 2013/03/15                               
 * @version 1.0                                   
 */ 
package com.ruleEngine.dao.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.springframework.jdbc.support.lob.LobHandler;

import com.ruleEngine.dao.RuleEngineDaoImpl;
import com.ruleEngine.dao.RuleParam;
import com.ruleEngine.db.ConnectionHelper;
/**
 * 此类为规则流部分的辅助类，它内部所封装的规则流所需要的数据。
 * 此类会组织规则流中所有需要用到的方法
 * 此类自己组织单例，其通过单例的方式来调用
 * @author Johnson Ma
 *
 */
public class RuleEngineFlowHelper {
	

	private static final Logger log = Logger.getLogger(RuleEngineFlowHelper.class);
	private static RuleEngineFlowHelper instance;
	/**
	 * 为此类创建单例
	 * @return
	 */
	public static RuleEngineFlowHelper getInstance(){
		
		if(instance == null){
			instance = new RuleEngineFlowHelper();
		}
		return instance;
	}
	/**
	 * 通过规则编号从ls_rule_flow表获取规则流的XML数据，
	 * 并且把对应关系封装到map中
	 * @param ruleParam
	 * @param lobHandler
	 * @return
	 * @throws Exception
	 */
	
	public  Map<String, String> getRuleFlowData(RuleParam ruleParam,LobHandler lobHandler) throws Exception{
		Map<String, String> hm = new HashMap<String, String>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String ruleStr = null;
		try{
			ruleStr = "select t.RULE_FLOW_CDE,t.RULE_FLOW_DATA from ls_rule_flow t where t.RULE_FLOW_CDE =?";
			conn = ConnectionHelper.getConnection();
			ps = conn.prepareStatement(ruleStr);
			ps.setString(1, ruleParam.getGEN_RULE_ID());
			rs = ps.executeQuery();
		
			if(rs.next()){
				
				hm.put("RULE_FLOW_CDE", rs.getString("RULE_FLOW_CDE"));
				hm.put("RULE_FLOW_DATA", (lobHandler.getClobAsString(rs, "RULE_FLOW_DATA")));
			}
		}catch(Exception e){
			log.error("执行 getRuleStr ruleGroup 出现错误",e);
			throw new Exception(e);
		}finally{
			ConnectionHelper.close(rs, ps,null, conn);
		}
		return hm;
		
	}
	/**
	 * 获取通过RuleParam中的规则编号查询规则流中规则组的列表
	 * 此类为
	 * @param ruleParam
	 * @param lobHandler
	 * @return
	 * @throws Exception
	 */
	public  List<String> getRuleFlowGroupList(RuleParam ruleParam,LobHandler lobHandler) throws Exception{
		List<String> list = new CopyOnWriteArrayList<String>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String ruleStr = null;
		try{
			ruleStr = "select t.COMPL_CHKLIST_CODE,t.GEN_RULEGROUP_DATA from LS_COMPL_CHKLIST t where t.compl_chklist_code in( select t.RULE_GROUP_CDE from ls_rule_flow_rulegroup t where t.LS_RULEFLOW_RULEGROUP_SEQ in (select LS_RULE_FLOW_SEQ FROM ls_rule_flow WHERE RULE_FLOW_CDE =?))";
			conn = ConnectionHelper.getConnection();
			ps = conn.prepareStatement(ruleStr);
			ps.setString(1, ruleParam.getGEN_RULE_ID());
			rs = ps.executeQuery();
		
			while(rs.next()){
				list.add(rs.getString("COMPL_CHKLIST_CODE"));
			}
		}catch(Exception e){
			log.error("执行 getRuleStr ruleGroup 出现错误",e);
			throw new Exception(e);
		}finally{
			ConnectionHelper.close(rs, ps,null, conn);
		}
		return list;
	}
	
	/**
	 * 根据规则组的编号查询这个规则组中的规则编号和规则drools数据，
	 * 并且把他们放入map中
	 * @param groupID
	 * @param lobHandler
	 * @return
	 * @throws Exception
	 */
	public  Map<String,String> getRuleData(String groupID,LobHandler lobHandler) throws Exception{
		Map<String, String> hm = new ConcurrentHashMap<String, String>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		StringBuffer ruleStr = new StringBuffer(500);
		try{
			ruleStr.append("select GEN_RULE_CODE, GEN_RULE_DATA FROM LS_GEN_RULE lg WHERE GEN_RULE_CODE in");
			ruleStr.append("  ( select l.COMPL_RULE_CODE from   ls_compl_chklist_rule l where l.LS_COMPL_CHKLIST_SEQ in");
				ruleStr.append("(select t.LS_COMPL_CHKLIST_SEQ from LS_COMPL_CHKLIST t where t.compl_chklist_code = ?))");
			conn = ConnectionHelper.getConnection();
			ps = conn.prepareStatement(ruleStr.toString());
			ps.setString(1, groupID);
			rs = ps.executeQuery();
			while(rs.next()){
				hm.put(rs.getString("GEN_RULE_CODE"), (lobHandler.getClobAsString(rs, "GEN_RULE_DATA")));
			}
		
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception();
		}finally{
			ConnectionHelper.close(rs, ps,null, conn);
		}
		return hm;
	}

	/**
	 * 此方法为上面两个getRuleFlowGroupList和getRuleData的逻辑方法，在此方法中规则会通过
	 * 上面的两个方法查询出来的数据经行整合，最后生成一个map， map的key为规则组编号，value为规则组所包含的规则的map
	 * @param ruleParam
	 * @param lobHandler
	 * @return
	 * @throws Exception
	 */
	public Map<String,Map<String,String>> makeRuleData(RuleParam ruleParam,LobHandler lobHandler) throws Exception{
		Map<String,Map<String,String>> map = new ConcurrentHashMap<String,Map<String,String>>();
		List<String> list = getRuleFlowGroupList(ruleParam,lobHandler);
		for (int i = 0; i < list.size(); i++) {
			map.put(list.get(i),getRuleData(list.get(i).toString(),lobHandler));
		}
		return map;
	}
	/**
	 * 根据makeRuleData中说返回的数据重组规则组drools数据，会返回map
	 * map的key为
	 * @param ruleParam
	 * @param lobHandler
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> makeGroupRuleFlowData(RuleParam ruleParam,LobHandler lobHandler) throws Exception{
		Map<String,String> hm  = new ConcurrentHashMap<String,String>();
		Map<String,Map<String,String>> map = makeRuleData(ruleParam,lobHandler);
		int i = 0;
		for (Iterator<Entry<String, Map<String, String>>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String,Map<String, String>> entry = (Map.Entry<String,Map<String, String>>) iterator.next();
			hm.put(entry.getKey(), genRuleFlowData(entry.getValue(),entry.getKey(), i));
			i++;
		}
		return hm;
		//hm.put(key, value)
	}
	
	/**
	 * 根据前面查询到的数据拼装具体规则流中规则组的drools数据
	 * @param map
	 * @param groupID
	 * @return
	 * @throws Exception
	 */
	public  String genRuleFlowData(Map<String, String> map, String groupID,int flag) throws Exception{
		
		StringBuffer ruleFlowData = new StringBuffer(500);
		int i = 10 ;
		ruleFlowData.append(RuleEngineDaoImpl.genRuleHeader());
		ruleFlowData.append("global java.util.Map collectHm;").append("\n");
		for (Iterator<Entry<String, String>> ruleIter = map.entrySet().iterator(); ruleIter.hasNext();) {
			Map.Entry<String,String> ruleEntry = ruleIter.next();
			ruleFlowData.append(" rule \""+ruleEntry.getKey()+"\"").append("\n");
			ruleFlowData.append(" salience "+(i--)).append("\n");
			ruleFlowData.append(" lock-on-active true").append("\n");
			ruleFlowData.append(" ruleflow-group \""+groupID+"\"").append("\n");
			ruleFlowData.append(ruleEntry.getValue()).append("\n").append("\n");
		}
		ruleFlowData.append(createCommonRule(groupID,flag));
		return ruleFlowData.toString();
	}
	/**
	 * 创建规则流中的公共规则
	 * 此规则的作用就是承接上一个规则和下一个规则的桥梁
	 * 其具体方案就是如《规则说明书》规则流引擎部分一样，此部分会调用RuleEngineFlowHelper的方法
	 * 会给这个类赋值，然后规则流中的XML会根据RuleEngineFlowHelper类中的值经行判断下一步调用哪个规则组
	 * @param groupID
	 * @param flag
	 * @return
	 */
	public String createCommonRule(String groupID, int flag){
		StringBuffer commonRule = new StringBuffer(400);
		commonRule.append(" rule \"commonRule"+flag+""+"\"").append("\n");
		commonRule.append(" salience -100").append("\n");
		commonRule.append(" lock-on-active true").append("\n");
		commonRule.append(" ruleflow-group \""+groupID+"\"").append("\n");
		
		commonRule.append(" when ").append("\n");
		commonRule.append(" $adper:RuleEngineFlowAdpter();").append("\n");
		commonRule.append(" then").append("\n");
		commonRule.append(" $adper = $adper.processRuleFlowResult(mylist,map,collectHm,\""+groupID+"\");").append("\n");
	//	commonRule.append(" System.out.println( \"$adper:\"+$adper.getApprove());").append("\n");
		commonRule.append("  update($adper);").append("\n");
		commonRule.append(" end").append("\n");
		return commonRule.toString();
	}
	
	/**
	 * 产生规则流的规则组中不冲突的规则个数
	 * 此方法的主要功能是通过把和前面有冲突的规则分离出去，并且会返回分离后每个规则个数，
	 * 因为规则引擎原始API中不支持不同的规则组中包含同样的规则，
	 * 它会把后面插入的规则，如果和前面相同，就会把这个规则分离出去
	 * @param ruleParam
	 * @param lobHandler
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> getRuleGroupCount(RuleParam ruleParam, LobHandler lobHandler) throws Exception{
		Map<String,String> countMap = new ConcurrentHashMap<String,String>();
		Map<String, Map<String, String>> dataHm = makeRuleData(ruleParam, lobHandler);
		List<String> temp = new CopyOnWriteArrayList<String>();
		for (Map.Entry<String, Map<String, String>> groupEntry : dataHm.entrySet()) {
			Map<String, String> ruleData = groupEntry.getValue();
			int i = 0;
			for (Map.Entry<String, String> ruleEntry : ruleData.entrySet()) {
				if(!temp.contains(ruleEntry.getKey())){
					temp.add(ruleEntry.getKey());
					i++;
				}
			}
			countMap.put(groupEntry.getKey(), i+"");
		}
		
		return countMap;
		
	}

	
}
