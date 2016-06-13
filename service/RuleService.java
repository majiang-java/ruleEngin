/**                                               
 * <p>Title: DROOLS RULE ENGINE SYSTEM</p>          
 * <p>Description: 主要用于执行调用接口的服务类 </p>                           
 * <p>Copyright: Copyright (c) 2012 - 2015</p>    
 * <p>Company: Excel Technology International (Bei Jing) Limited</p>      
 * @author Johnson 
 * @Date 2013/03/15                              
 * @version 1.0                                   
 */
package com.ruleEngine.service;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.drools.runtime.StatefulKnowledgeSession;

import com.ruleEngine.bean.RuleBean;
import com.ruleEngine.bean.RuleGroupBean;
import com.ruleEngine.dao.CommonBussiness;
import com.ruleEngine.dao.HotClassComplier;
import com.ruleEngine.dao.RuleEngineDaoImpl;
import com.ruleEngine.dao.RuleEngineType;
import com.ruleEngine.dao.RuleParam;
import com.ruleEngine.dao.helper.DynamicGenRuleHepler;
import com.ruleEngine.dao.helper.HotClassComplierRuleCreator;
import com.ruleEngine.dao.helper.RuleEngineFlowHelper;
import com.ruleEngine.dao.helper.RuleGroupHelper;


public class RuleService {
	private static final Logger log = Logger.getLogger(RuleService.class);
	private RuleEngineDaoImpl ruleEngineDaoImpl;
	public void setRuleEngineDaoImpl(RuleEngineDaoImpl ruleEngineDaoImpl) {
		this.ruleEngineDaoImpl = ruleEngineDaoImpl;
	}


	/**
	 * 此方法是外部调用的公共接口
	 * 通过调用中枢方法getComplileSession类取得对应的StatefulKnowledgeSession，
	 * 然后插入fact(规则数据)，
	 * ksession.setGlobal("mylist", list); 插入收集规则信息的list
	 * 最后在通过fireAllRules执行规则， 
	 * 
	 * 执行后调用processRuleResult，规则流调用processRuleFlowResult
	 * 把收集到数据的list传入 然后等待processRuleResult返回处理结果，最后返回给外部
	 * @param ruleParam 规则参数
	 * @param map 规则数据
	 * @return
	 * @throws Exception
	 */

	@SuppressWarnings("rawtypes")
	public boolean callRuleEngine(RuleParam ruleParam,Map<String, Object> map) throws Exception{
		log.info("Call method 'callRuleEngine' is beginning"+new SimpleDateFormat("yyyyMMdd hh:MM:ss: SSS").format(Calendar.getInstance().getTime()));
		boolean flag = false;
		try{

			StatefulKnowledgeSession ksession = ruleEngineDaoImpl.getComplileSession(ruleParam);
			List list = new ArrayList();
			Map hm = null;
			ksession.setGlobal("mylist", list);
			
			for (Iterator<Entry<String, Object>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				HotClassComplier.invokeMethod(ksession, "insert", new Object[]{entry.getValue()});
			}
			//ksession.insert(object);
			if(RuleEngineType.RULE_FLOW.equalsIgnoreCase(ruleParam.getRULE_FALG())){
				Map paramHm = RuleEngineFlowHelper.getInstance().getRuleGroupCount(ruleParam,ruleEngineDaoImpl.getLobHandler());
				hm = new HashMap();
				System.out.println(paramHm);
				ksession.setGlobal("map", paramHm);
				ksession.setGlobal("collectHm", hm);
				ksession.startProcess(ruleParam.getGEN_RULE_ID());
			}
			
			ksession.fireAllRules();
			//System.out.println("i:"+i);
			ksession.dispose();
			
			if(RuleEngineType.RULE_FLOW.equalsIgnoreCase(ruleParam.getRULE_FALG())){
				Object obj = HotClassComplier.invokeFieldMethod(map.get("RuleEngineFlowAdpter"), "getApprove", new Object[]{});
				boolean b = Boolean.parseBoolean(obj.toString());
				for (Iterator iterator = hm.entrySet().iterator(); iterator.hasNext();) {
					Map.Entry entey = (Map.Entry) iterator.next();
					System.out.println(Arrays.asList((String[])entey.getValue()));
				}
				ruleEngineDaoImpl.processRuleFlowResult(ruleParam,b,hm);
			}else{
				flag = ruleEngineDaoImpl.processRuleResult(list, ruleParam);
			}
			
		}catch(Exception e){
			log.error("执行接口 callRuleEngine 报错",e);
			flag = false;
			throw new Exception(e);
		}
		log.info("Call method 'callRuleEngine' is end"+new SimpleDateFormat("yyyyMMdd hh:MM:ss: SSS").format(Calendar.getInstance().getTime()));
		return flag;
		
	}
	
	/**
	 * 此方法问调用规则引擎的公用接口
	 * 多条数据规则引擎开发接口
	 * @param param 传入引擎参数
	 * @param hm 传入引擎数据
	 * @return
	 * @throws Exception
	 */
	public static boolean callRuleEngin(RuleParam param, Map<String, Object[]> hm) throws Exception {
		StringBuffer sb = new StringBuffer(1000);
		List<RuleBean> list = new ArrayList<RuleBean>();
		boolean flag = false;
		RuleGroupBean ruleGroupBean = RuleGroupHelper.getInstance().getRuleGroupFromDB(param);
	//	Collections.reverse(ruleGroupBean.getRuleBeanList());
		for (int i = 0; i < ruleGroupBean.getRuleBeanList().size(); i++) {
			String fileName = "GenRule"+CommonBussiness.createFileSeq();
			sb.setLength(0);
            sb.append( DynamicGenRuleHepler.createGenRuleJava(ruleGroupBean.getRuleBeanList().get(i), hm,fileName));
			Map<String,Object> ObjHm = HotClassComplierRuleCreator.hotComplierEngine(fileName,sb.toString());
			HotClassComplier.invokeFieldMethod(ObjHm.get(fileName), "genRule", new Object[]{ruleGroupBean.getRuleBeanList().get(i),hm,list});
		}
		flag = CommonBussiness.processResult(param,list);
		HotClassComplierRuleCreator.deletePath();
		return flag;
		
		
	}
	

	
	
}
