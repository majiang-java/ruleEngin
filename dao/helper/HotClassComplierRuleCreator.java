/**                                               
 * <p>Title: DROOLS RULE ENGINE SYSTEM</p>          
 * <p>Description: 主要用于动态生成编译和加载JavaBean的服务类 </p>                           
 * <p>Copyright: Copyright (c) 2012 - 2015</p>    
 * <p>Company: Excel Technology International (Bei Jing) Limited</p>      
 * @author    Johnson Ma
 * @Date 2013/03/15                                
 * @version 1.0                                   
 */
package com.ruleEngine.dao.helper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager.Location;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.apache.log4j.Logger;

public class HotClassComplierRuleCreator {
	
	private static final Logger log = Logger.getLogger(HotClassComplierRuleCreator.class);

	/**
	 * 热编译，热加载方法 
	 * @param className  方法名称
	 * @param sourse  数据源
	 * @return  map  返回热加载类的对象
	 * @throws Exception
	 */
	public static Map<String,Object> hotComplierEngine(String className,String sourse) throws Exception{
		
		Map<String,Object> hm = new HashMap<String,Object>();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(
				null, null, null);
		Location location = StandardLocation.CLASS_OUTPUT;
		
		File path = new File(HotClassComplierRuleCreator.getClassesPath());
		if(!path.exists()){
			path.mkdir();
		}
		File[] outputs = new File[] { path };
		try {
			fileManager.setLocation(location, Arrays.asList(outputs));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		StringSourceJavaObject sourceObject = new HotClassComplierRuleCreator.StringSourceJavaObject(
				className, sourse);
		Iterable<? extends SimpleJavaFileObject> fileObjects = Arrays
				.asList(sourceObject);
		CompilationTask task = compiler.getTask(null, fileManager, null, null,
				null, fileObjects);
		boolean result = task.call();

		if (result) {
			log.info("编译成功！");
			try {
				
			//	Class clazz = HotClassComplierRuleCreator.class.getClassLoader().loadClass("com.ruleEngine.dao.helper.GenRule");
				URL url = new URL("file:"+HotClassComplierRuleCreator.getClassesPath());
				URLClassLoader classLoader = new URLClassLoader(new URL[]{url},Thread.currentThread().getContextClassLoader());
				Class<?> clazz = classLoader.loadClass(DynamicGenRuleHepler.PACKAGE_NAME+"."+className);
				
				hm.put(className, clazz.newInstance());;
				log.info("clazz:"+clazz);
			} catch (ClassNotFoundException e) {
				throw new Exception(e);
			} catch (SecurityException e) {
				throw new Exception(e);
			} catch (IllegalArgumentException e) {
				throw new Exception(e);
			} catch (IllegalAccessException e) {
				throw new Exception(e);
			}
		}
		return hm;
	}
	
	/**
	 * 删除临时文件夹
	 */
	public static void deletePath(){
		File f = new File(getClassesPath()+"/com/ruleEngine/dao/dynamic/");
		if(f.isDirectory()){
			File[] files = f.listFiles();
		    for (File file : files) {
		    	file.delete();
			}
		}
		f.delete();
	}
	static class StringSourceJavaObject extends SimpleJavaFileObject {
		private String code;
		protected StringSourceJavaObject(String name, String content) {
			
			super(URI.create("String:///" + name.replace(".", "/")
					+ Kind.SOURCE.extension), Kind.SOURCE);
			this.code = content;
		}
		@Override
		public CharSequence getCharContent(boolean paramBoolean)
		throws IOException {
			return code;
		}
	}
	
	
	/**
	 * 获取项目的根路径
	 * @return
	 */
    public static String getClassesPath(){
        String path = "";
        path = HotClassComplierRuleCreator.class.getResource("/").toString();
        if(path.startsWith("file")){
            // 当class文件在class文件中时，返回"file:/F:/ ..."样的路径
            path = path.substring(6);
        }else if(path.startsWith("jar")){
            // 当class文件在jar文件中时，返回"jar:file:/F:/ ..."样的路径
            path = path.substring(10);
        }
        if(path.endsWith("/") || path.endsWith("\\")){
            //使返回的路径不包含最后的"/"
            path = path.substring(0, path.length()-1);
        }
        return path;
    }



}
