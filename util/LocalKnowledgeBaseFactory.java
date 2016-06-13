/**                                               
 * <p>Title: DROOLS RULE ENGINE SYSTEM</p>          
 * <p>Description: 主要用于执行调用接口的服务类 </p>                           
 * <p>Copyright: Copyright (c) 2012 - 2015</p>    
 * <p>Company: Excel Technology International (Bei Jing) Limited</p>      
 * @author Johnson 
 * @Date 2013/03/15                              
 * @version 1.0                                   
 */
package com.ruleEngine.util;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;

public class LocalKnowledgeBaseFactory {
	
	 private static KnowledgeBase kbase;
	 /**
	  * 生成KnowledgeBase 单例
	  * @return
	  */
	 public static KnowledgeBase getKnowledgeBaseInstance(){
		 if(kbase == null){
			 kbase = KnowledgeBaseFactory.newKnowledgeBase();
		 }
		 return kbase;
	 }

}
