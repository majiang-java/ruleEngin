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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
/**
 * @desc 该类是根据数据库中的具体数据生成java类
 * @author Johnson Ma
 *
 */
public class HotClassComplier{
	private static final Logger log = Logger.getLogger(HotClassComplier.class);

	private static Map<String, Object> hm  = null;
	private String javaFileURL;
	private String classFileURL;
	/**
	 * 设定java类的生成路径
	 * @param javaFileURL
	 */
	public void setJavaFileURL(String javaFileURL) {
		this.javaFileURL = javaFileURL;
	}
	/**
	 * 设定class的生成路径
	 * @param classFileURL
	 */
	public void setClassFileURL(String classFileURL) {
		this.classFileURL = classFileURL;
	}
	private RuleEngineDaoImpl ruleEngineDaoImpl;
	
	public void setRuleEngineDaoImpl(RuleEngineDaoImpl ruleEngineDaoImpl) {
		this.ruleEngineDaoImpl = ruleEngineDaoImpl;
	} 
	
	public void initData() throws Exception{
		hm = createDynamicFile();
	}
	/**
	 * 此类为逻辑方法，通过数据库查询业务类数据
	 * 调用拼接方法经行拼装
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> processGenJavaFileData() throws Exception{
		Map<String, String> hm = new ConcurrentHashMap<String, String>();
		Map<String, Map<String, String>> resultHm = ruleEngineDaoImpl.processJavaFileData();
//		List<String> catList = RuleEngineDaoImpl.genCatData();
//		for (int i = 0; i < catList.size(); i++) {
		for (Iterator<String> iterator = resultHm.keySet().iterator(); iterator.hasNext();) {
//			if(catList.get(i).equals(iterator.next())){
			String cat = iterator.next();
			String javaFileStr = genJavaFileData(cat,resultHm.get(cat));
			hm.put(cat, javaFileStr);
		//	}
		}
	//	}
		return hm;
	}
	/**
	 * 根据从表中查询的数据拼接java文件
	 * @param cat
	 * @param fieldMap
	 * @return
	 */
	private String genJavaFileData(String cat, Map<String, String> fieldMap){
		StringBuffer sb = new StringBuffer(5000);
		sb.append("package com.ruleEngine.domain;").append("\n");
		sb.append("public class ").append(cat).append(" implements Cloneable{").append("\n");
		for (Iterator<Entry<String, String>> iterator = fieldMap.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String,String> entry = iterator.next();
			
			sb.append("  private ").append(getJavaTypeFromDBType(entry.getValue()))
			.append(" ").append(entry.getKey()).append(";").append("\n");
			sb.append("  public void set").append(entry.getKey()).append("("+getJavaTypeFromDBType(entry.getValue())+" "+entry.getKey().toLowerCase()+"){ ").append("\n");
			sb.append("    this.").append(entry.getKey()).append(" = ").append(entry.getKey().toLowerCase()+";").append("\n");
			sb.append("  }").append("\n");
			sb.append("  public "+getJavaTypeFromDBType(entry.getValue())+" get").append(entry.getKey()).append("(){ ").append("\n");
			sb.append("    return ").append(entry.getKey()).append(";").append("\n");
			sb.append("  }").append("\n");
			
		}
		sb.append(" public Object clone() throws java.lang.CloneNotSupportedException{").append("\n");
		sb.append(" return super.clone();").append("\n");
		sb.append(" }").append("\n");
		sb.append("}\n");
		if(log.isDebugEnabled()){
			log.debug("Gen java file :"+sb.toString());
		}
		//System.out.println(sb.toString());
		return sb.toString();
	}
	
	/**
	 * 在生成java类的时候的类型转换
	 * @param type 传入类型
	 * @return 
	 */
	private String getJavaTypeFromDBType(String type){
		String returnTyope= null;
		if("NUMBER".equalsIgnoreCase(type)){
			returnTyope = "java.lang.Double";
		}else if("VARCHAR2".equalsIgnoreCase(type)){
			returnTyope = "java.lang.String";
		}else if("DATE".equalsIgnoreCase(type)){
			returnTyope = "java.util.Date";
		}
		return returnTyope;
	}
	
	/**
	 * 构造，编译，并且热加载，所需要的javaBean，并且封装成map
	 * key 为类名,  value为具体类的对象
	 * @return
	 * @throws Exception
	 */
	
	private  Map<String, Object> createDynamicFile() throws Exception {
		Map<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
        //得到写文件路径         
		String filePath = javaFileURL;
        //得到文件名        
        File file = new File(filePath);
        //如果目录不存在，则创建目录      
        if(!file.exists()){
        	file.mkdirs();
        }
        Map<String, String> javaFileMap = processGenJavaFileData();
        
        //编译状态
        int status = -1;
        String path = classFileURL;
        for (Iterator<Entry<String, String>> iterator = javaFileMap.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String,String> entry = iterator.next();
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filePath + "//" + entry.getKey()+".java"),"utf-8");
	        out.write(entry.getValue());
	        out.flush();
	        out.close();
	        String[] args = new String[] { 
	                "-d",
	                path,
	                "-classpath",
	                "%classpath%;"
	                		+ path
	                        + ";",
	                        "-encoding", "utf-8", filePath + "//" + entry.getKey() +".java" };
	        status = com.sun.tools.javac.Main.compile(args); 
	
		}
        
		if (status != 0) {
			log.info("没有成功编译!" + status);
		} else {
			OutputStreamWriter out = null;
			try{
		        out = new OutputStreamWriter(new FileOutputStream(filePath + "//tempDroolsContext.xml"),"utf-8");
		        out.write(makeDroolsTempXML(javaFileMap));
		        out.flush();
		        ApplicationContext ac = new FileSystemXmlApplicationContext(filePath + "//tempDroolsContext.xml");
		        for (Iterator<String> iterator = javaFileMap.keySet().iterator(); iterator
						.hasNext();) {
		        	String cat = iterator.next();
		        	resultMap.put(cat, ac.getBean(cat.toLowerCase()));
		        }
		        resultMap.put("RuleEngineFlowAdpter", ac.getBean("adpter"));
			}catch(Exception e){
				throw new Exception(e);
			}finally{
				 out.close();
			}

		}
		return resultMap;
   }

	/**
	 * 获得初始化的数据
	 * @return
	 */
	public static Map<String, Object> getInitDataMap(){
		return hm;
	}
	
	/**
	 * 产生规则
	 * @param field
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public static Object[] getFieldArray(String field, int count) throws Exception{
		Object[] array = new Object[count];
		for(int i = 0;i<count;i++){
			array[i] = invokeFieldMethod(hm.get(field),"clone",new Object[]{});
		}
		return array;
	}
	/**
	 * 根据需要的生成的数据生成spring配置文件
	 * @param javaFileMap
	 * @return
	 */
	private String makeDroolsTempXML(Map<String,String> javaFileMap){
		StringBuffer header = new StringBuffer(400);
		header.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n");
		header.append("<beans xmlns=\"http://www.springframework.org/schema/beans\"").append("\n");
		header.append("	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"").append("\n");
		header.append("	xmlns:tx=\"http://www.springframework.org/schema/tx\"").append("\n");
		header.append("	xsi:schemaLocation=\"http://www.springframework.org/schema/beans ").append("\n");
		header.append("	                    http://www.springframework.org/schema/beans/spring-beans-2.0.xsd").append("\n");
		header.append("	                    http://www.springframework.org/schema/tx").append("\n");
		header.append("	                    http://www.springframework.org/schema/tx/spring-tx-2.0.xsd\">").append("\n");
		
		for (Iterator<String> iterator = javaFileMap.keySet().iterator(); iterator.hasNext();) {
			String cat = iterator.next();
			header.append("  <bean id=\""+cat.toLowerCase()+"\" class=\"com.ruleEngine.domain."+cat+"\"/>");
		}
		header.append(" <bean id=\"adpter\" class=\"com.ruleEngine.dao.RuleEngineFlowAdpter\"/>");
		header.append("</beans>");
		return header.toString();
	}
	/**
	 * 反射执行对象中的方法（传入参数）
	 * 
	 * @param owner
	 * @param methodName
	 * @param args
	 * @return
	 * @throws Exception
	 */
	 @SuppressWarnings("rawtypes")
	public static Object invokeMethod(Object owner, String methodName, Object[] args) throws Exception {   
	     Class<?> ownerClass = owner.getClass();   
		 Class[] argsClass = new Class[args.length];   
	     for (int i = 0, j = args.length; i < j; i++) {   
	    	 argsClass[i] = args[i].getClass().getSuperclass();
	     }   
	     Method method = ownerClass.getMethod(methodName,argsClass);   
	     return method.invoke(owner, args);   
	} 
	/**
	 * 反射执行对象中的方法 （给对象赋值）
	 * @param owner
	 * @param methodName
	 * @param args
	 * @return
	 * @throws Exception
	 */
	 @SuppressWarnings("rawtypes")
	public static Object invokeFieldMethod(Object owner, String methodName, Object[] args) throws Exception {   
	     Class<? extends Object> ownerClass = owner.getClass();   
	     Class[] argsClass = new Class[args.length];   
	    
	     for (int i = 0, j = args.length; i < j; i++) {   
	    	 argsClass[i] = args[i].getClass();
	     }   
	     Method method = ownerClass.getMethod(methodName,argsClass);   
	     return method.invoke(owner, args);   
	}
}

