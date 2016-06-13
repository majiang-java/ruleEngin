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

import java.util.List;
import java.util.Map;
/**
 * 此类为规则流的apder类
 * @author Johnson Ma
 *
 */
public class RuleEngineFlowAdpter {
	
	private  boolean approve = false;
	public boolean getApprove() {
		return approve;
	}

	public void setApprove(boolean approve) {
		this.approve = approve;
	}
	
	/**
	 * 此方法通过createCommonRule创立的公共规则调用
	 * 此方法的具体逻辑的是规则组每执行到这个方法的时候，list中会记录所有的通过的规则，
	 * 这样的话通过和getRuleGroupCount方法处理过的规则进行比较，然后得出此规则时候通过，如果通过 这会给此类的属性
	 * approve赋值 true,如果不通过则会赋值false.这样规则流中的条件会判断此类中的approve的值,从调用对应节点上的规则组
	 * collectHm会一直记录通过的规则 key 为规则组的代码，value为对应通过的规则列表
	 * @param list 处理每一个规则组执行结果的list
	 * @param map 包含后所有的getRuleGroupCount通过此方法处理过的map
	 * @param collectHm  用于收集结果的map
	 * @param groupName  规则流代码
	 * @return RuleEngineFlowAdpter
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RuleEngineFlowAdpter processRuleFlowResult(List list,Map map,Map collectHm,String groupName){
		Object[] tempList = list.toArray();
		String[] str = new String[tempList.length];
		for (int i = 0; i < tempList.length; i++) {
			str[i] = tempList[i].toString();
		}
		collectHm.put(groupName, str);
		if(list.size() ==Integer.parseInt(map.get(groupName).toString()) ){
			this.approve = true;
		}else{
			this.approve = false;
		}
		System.out.println("collectHm:"+collectHm);
		//初始化list
		list.removeAll(list);
		return this;
	}

}
