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

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DroolsParserException;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
/**
 * 
 * 
 * 此类为封装原始接口的方法
 * 此方法几乎所有的方法的是调用drools引擎自带的API，经行编译处理，生成StatefulKnowledgeSession
 * 此类的方法都会被 getComplileSession方法来调用生成对应的StatefulKnowledgeSession
 * 
 *@author Johnson Ma
 */
public class RuleResourseUtil {
	
	
	/**
	 * 从DRL文件中获取规则
	 * @param path
	 * @return
	 */
	public static  StatefulKnowledgeSession  getDRLResourse(String path){
		
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		kbuilder.add(ResourceFactory.newClassPathResource(path), ResourceType.DRL);
		 if ( kbuilder.hasErrors() ) {
	            System.out.println( kbuilder.getErrors().toString() );
		            throw new RuntimeException( "Unable to compile \"." ); 
		  }
		 Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();
		 KnowledgeBase kbase =  LocalKnowledgeBaseFactory.getKnowledgeBaseInstance();
		 kbase.addKnowledgePackages(pkgs);
		 StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		
		 return ksession;
	}
	/**
	 * 从决策表中获取规则
	 * @param path
	 * @return
	 */
	public static StatefulKnowledgeSession getXLSResourse(String url){

		DecisionTableConfiguration dtableconfiguration =
				KnowledgeBuilderFactory.newDecisionTableConfiguration();
				dtableconfiguration.setInputType( DecisionTableInputType.XLS );
				
	        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	        kbuilder.add(ResourceFactory.newUrlResource(url),
	                ResourceType.DTABLE, dtableconfiguration);
	        if ( kbuilder.hasErrors() ) {
	            System.out.println( kbuilder.getErrors().toString() );
		            throw new RuntimeException( "Unable to compile \"." ); 
	        }
	        KnowledgeBase kbase = LocalKnowledgeBaseFactory.getKnowledgeBaseInstance();
	        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
	        StatefulKnowledgeSession ksession =  kbase.newStatefulKnowledgeSession();
	        return ksession;
	}
	/**
	 * 从字符流中获取规则
	 * @param path
	 * @return
	 */
	public static StatefulKnowledgeSession getDrlResourseFromStream(String orig) throws Exception{
		StringReader sReader = new StringReader(orig);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		kbuilder.add(ResourceFactory.newReaderResource(sReader), ResourceType.DRL);
	     if ( kbuilder.hasErrors() ) {
	            System.out.println( kbuilder.getErrors().toString() );
		            throw new RuntimeException( "Unable to compile \"." ); 
	    }
	     KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
	     kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
	     StatefulKnowledgeSession ksession =  kbase.newStatefulKnowledgeSession();
	    return ksession;
	}
	/**
	 * 
	 * 验证规则是否编译通过
	 * @param orig
	 * @return
	 * @throws Exception
	 */
	
	public static String validateRules(String orig) throws Exception{
		StringReader sReader = new StringReader(orig);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		kbuilder.add(ResourceFactory.newReaderResource(sReader), ResourceType.DRL);
	     if ( kbuilder.hasErrors() ) {
	            System.out.println( kbuilder.getErrors().toString() );
		            throw new RuntimeException( "Unable to compile \"." ); 
	    }
		 return "Compiled victory";
	}
	
	/**
	 * 从字符流中获取和编译规则
	 * @throws IOException 
	 * @throws DroolsParserException 
	 */
	public static StatefulKnowledgeSession getDrlAndFlowResourseFromStream(Map<String,String> ruleGroupHm,String ruleFlowXML) throws DroolsParserException, IOException{
		
		StringReader ruleFlowXMLReader = new StringReader(ruleFlowXML);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		for (Map.Entry<String,String> element : ruleGroupHm.entrySet()) {
			StringReader ruleGroupReader = new StringReader(element.getValue().toString());
			kbuilder.add(ResourceFactory.newReaderResource(ruleGroupReader), ResourceType.DRL);
		}
		
		kbuilder.add(ResourceFactory.newReaderResource(ruleFlowXMLReader), ResourceType.DRF);
	     if ( kbuilder.hasErrors() ) {
	            System.out.println( kbuilder.getErrors().toString() );
		            throw new RuntimeException( "Unable to compile \"." ); 
	    }
        KnowledgeBase kbase = LocalKnowledgeBaseFactory.getKnowledgeBaseInstance();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession =  kbase.newStatefulKnowledgeSession();
        return ksession;
		
	}
	

}
