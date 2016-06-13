/**                                               
 * <p>Title: DROOLS RULE ENGINE SYSTEM</p>          
 * <p>Description: 此类的主要功能是动态调用 </p>                           
 * <p>Copyright: Copyright (c) 2012 - 2015</p>    
 * <p>Company: Excel Technology International (Bei Jing) Limited</p>      
 * @author    
 * @Date 2013/03/15                                
 * @version 1.0                                   
 */
package com.ruleEngine.dao.helper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.runtime.StatefulKnowledgeSession;
import org.jfree.util.Log;

import com.ruleEngine.bean.RuleBean;
import com.ruleEngine.dao.HotClassComplier;
import com.ruleEngine.util.RuleResourseUtil;

public class DynamicGenRuleHepler {

	public static final String PACKAGE_NAME="com.ruleEngine.dao.dynamic";
	private static DynamicGenRuleHepler dynamicGenRuleHepler;
	public static DynamicGenRuleHepler getInstance(){
		if(dynamicGenRuleHepler != null){
			dynamicGenRuleHepler = new DynamicGenRuleHepler();
		}
		return dynamicGenRuleHepler;
	}
	
	
	/**
	 * 动态生成调用规则的方法
	 * @param ruleBean  规则数据
	 * @param hm  数据
	 * @param className 类名
	 * @return String 返回生成的调用java生成的方法
	 */
	public static String createGenRuleJava(RuleBean ruleBean,Map<String,Object[]> hm,String className){
		StringBuffer sb = new StringBuffer();
		
		sb.append("package "+PACKAGE_NAME+";").append("\n");
		sb.append("public class "+className+"{").append("\n");
		sb.append("public void genRule(com.ruleEngine.bean.RuleBean ruleBean,java.util.HashMap<String,Object[]> hm,java.util.ArrayList list){").append("\n");
		for (int i = 0; i < ruleBean.getCat().size(); i++) {
			sb.append("for(int i"+i+" =0;i"+i+"<"+hm.get(ruleBean.getCat().get(i)).length+";i"+i+"++ ){").append("\n");
		}
		sb.append("java.util.Map<String,Object> createHm = new java.util.HashMap<String,Object>();").append("\n");
		for (int i = 0; i < ruleBean.getCat().size(); i++) {
			
			sb.append("createHm.put(\""+ruleBean.getCat().get(i)+"\",hm.get(\""+ruleBean.getCat().get(i)+"\")[i"+i+"]);").append("\n");
			sb.append("System.out.println(hm.get(\""+ruleBean.getCat().get(i)+"\")[i"+i+"]);").append("\n");
		}
		
		sb.append("com.ruleEngine.dao.helper.DynamicGenRuleHepler.callRuleEngine(ruleBean,createHm,list);");
		for (int i = 0; i < ruleBean.getCat().size(); i++) {
			sb.append("}").append("\n");
		}
		sb.append("}}");
		return sb.toString();
	}
	
	/**
	 * 
	 * @param ruleBean 传入的规则数据
	 * @param hm  数据
	 * @param list 需返回的数据集
	 * @return
	 * @throws Exception
	 */
	public static boolean callRuleEngine(RuleBean ruleBean,Map<String,Object> hm, List<RuleBean> list){
		try {
			StatefulKnowledgeSession ksession = RuleResourseUtil.getDrlResourseFromStream(ruleBean.getGenData());
			RuleBean newRuleBean = (RuleBean) ruleBean.clone();
			ksession.setGlobal("ruleBean", newRuleBean);
			
			for (Iterator<Entry<String, Object>> iterator = hm.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				HotClassComplier.invokeMethod(ksession, "insert", new Object[]{entry.getValue()});
			}
			ksession.fireAllRules();
			ksession.dispose();
			list.add(newRuleBean);
		} catch (Exception e) {
			Log.error("执行规则错误",e);
		}
		return true;
	}
	
	
}
