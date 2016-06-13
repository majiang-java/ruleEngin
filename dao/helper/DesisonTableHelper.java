/**                                               
 * <p>Title: DROOLS RULE ENGINE SYSTEM</p>          
 * <p>Description: 主要功能为 </p>                           
 * <p>Copyright: Copyright (c) 2012 - 2015</p>    
 * <p>Company: Excel Technology International (Bei Jing) Limited</p>      
 * @author Johnson Ma   
 * @Date 2013/06/04                                
 * @version 1.0                                   
 */
package com.ruleEngine.dao.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ruleEngine.bean.DataElement;
import com.ruleEngine.bean.DeciFieldBean;
import com.ruleEngine.bean.DecisionTableBean;
import com.ruleEngine.db.ConnectionHelper;

public class DesisonTableHelper {
	private static final Logger log = Logger.getLogger(HotClassComplierRuleCreator.class);
	
	/**
	 * 获取评分卡的数据
	 * @param disisonID  评分卡ID
	 * @return DecisionTableBean 评分卡数据封装
	 * @throws Exception
	 */
	public static DecisionTableBean getDesionTableFromDB(String disisonID) throws Exception{
		DecisionTableBean desisionTableBean = new DecisionTableBean();
		desisionTableBean.setDesisonTableName(disisonID);
		List<DeciFieldBean> fieldList = new ArrayList<DeciFieldBean>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String ruleStr = null;
		try{
			ruleStr = "SELECT M.LS_SCORE_CARD_RULE_SEQ,  L.GEN_FIELD_CAT,L.GEN_FIELD_COL," +
					"DECODE('PRI', 'SND', L.GEN_FIELD_SND_DESC, L.GEN_FIELD_PRI_DESC) COM_CDE_PRI_DESC,L.GEN_FIELD_DATA_TYPE "+
					"FROM LS_GEN_FIELD L, LS_SCORE_CARD_RULE M WHERE L.LS_GEN_FIELD_SEQ = M.SCORE_RULE_FIELD_SEQ AND LS_SCORE_CARD_SEQ IN "+
					"(SELECT LS_SCORE_CARD_SEQ FROM LS_SCORE_CARD WHERE SCORE_CARD_CODE = ?)";
			conn = ConnectionHelper.getConnection();
			ps = conn.prepareStatement(ruleStr);
			ps.setString(1, disisonID);
			rs = ps.executeQuery();
		
			while(rs.next()){
				//hm.put("RULE_FLOW_CDE", disisonID);
				DeciFieldBean fieldBean = new DeciFieldBean();
				fieldBean.setGenFieldSeq(rs.getString("LS_SCORE_CARD_RULE_SEQ"));
				fieldBean.setGenFieldCat(rs.getString("GEN_FIELD_CAT"));
				fieldBean.setGenFieldCol(rs.getString("GEN_FIELD_COL"));
				fieldBean.setGenFieldDataType(rs.getString("GEN_FIELD_DATA_TYPE"));
				fieldBean.setGenFieldPriDesc(rs.getString("COM_CDE_PRI_DESC"));
				fieldList.add(fieldBean);
			}
			desisionTableBean.setFieldList(fieldList);
			getDataElement(fieldList);
		}catch(Exception e){
			log.error("执行 getRuleStr ruleGroup 出现错误",e);
			throw new Exception(e);
		}finally{
			ConnectionHelper.close(rs, ps,null, conn);
		}
		getTotleCatList(desisionTableBean);
		return desisionTableBean;
		
	}
	
	
	/**
	 * 从评分卡的字段中获取类型
	 * @param desisionTableBean
	 */
	private static void getTotleCatList(DecisionTableBean desisionTableBean){
		List<String> cache = new ArrayList<String>();
		for (int i = 0; i < desisionTableBean.getFieldList().size(); i++) {
			if(!cache.contains(desisionTableBean.getFieldList().get(i).getGenFieldCat())){
				cache.add(desisionTableBean.getFieldList().get(i).getGenFieldCat());
			}
		}
		desisionTableBean.setCatList(cache);
	}
	
	/**
	 * 获得评分卡数据
	 * @param fieldList
	 * @throws Exception
	 */
	public static void getDataElement(List<DeciFieldBean> fieldList) throws Exception{
		for (DeciFieldBean deciFieldBean : fieldList) {
			if("VARCHAR".equals(deciFieldBean.getGenFieldDataType())
					|| "VARCHAR2".equals(deciFieldBean.getGenFieldDataType())){
				getRuleCode(deciFieldBean);
			}else if("DATE".equals(deciFieldBean.getGenFieldDataType()) 
					|| "NUMBER".equals(deciFieldBean.getGenFieldDataType())){
				getRuleRange(deciFieldBean);
			}
		}
	}
	
	/**
	 * 如果字段类型为VARCHAR，VARCHAR2得话，从LS_SCORE_RULE_CODE获取数据
	 * @param deciFieldBean
	 * @throws Exception
	 */
	private static void getRuleCode(DeciFieldBean deciFieldBean) throws Exception{
		List<DataElement> list = new ArrayList<DataElement>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String ruleStr = null;
		ruleStr = "select SCORE_RULE_CODE_VALUE,SCORE_RULE_CODE_SCORE FROM LS_SCORE_RULE_CODE lg WHERE LS_SCORE_CARD_RULE_SEQ = ?";
		try{
			conn = ConnectionHelper.getConnection();
			ps = conn.prepareStatement(ruleStr);
			ps.setString(1, deciFieldBean.getGenFieldSeq());
			rs = ps.executeQuery();
			rs.getMetaData().toString();
			while(rs.next()){
				DataElement dataElement = new DataElement();
				dataElement.setHighValue(rs.getString("SCORE_RULE_CODE_VALUE")) ;
				dataElement.setScore(rs.getString("SCORE_RULE_CODE_SCORE"));
				list.add(dataElement);
			}
			
		}catch(Exception e){
			log.error("执行 getRuleStr rule 出现错误",e);
			throw new Exception(e);
		}finally{
			ConnectionHelper.close(rs, ps, null,conn);
		}
		deciFieldBean.setDataElement(list);
	}
	
	
	/**
	 * 如果字段类型为NUMBER, Data 则从LS_SCORE_RULE_RANGE取出数据
	 * @param deciFieldBean
	 * @throws Exception
	 */
	private static void getRuleRange(DeciFieldBean deciFieldBean) throws Exception{
		List<DataElement> list = new ArrayList<DataElement>();
	
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String ruleStr = null;
		ruleStr = "select SCORE_RULE_LOWER_LIMIT,SCORE_RULE_UPPER_LIMIT,SCORE_RULE_RANGE_SCORE from LS_SCORE_RULE_RANGE where LS_SCORE_CARD_RULE_SEQ = ?";
		try{
			conn = ConnectionHelper.getConnection();
			ps = conn.prepareStatement(ruleStr);
			ps.setString(1, deciFieldBean.getGenFieldSeq());
			rs = ps.executeQuery();
			rs.getMetaData().toString();
			while(rs.next()){
				DataElement dataElement = new DataElement();
				dataElement.setHighValue(rs.getString("SCORE_RULE_UPPER_LIMIT"));
				dataElement.setLowValue(rs.getString("SCORE_RULE_LOWER_LIMIT"));
				dataElement.setScore(rs.getString("SCORE_RULE_RANGE_SCORE"));
				list.add(dataElement);
			}
		}catch(Exception e){
			log.error("执行 getRuleStr rule 出现错误",e);
			throw new Exception(e);
		}finally{
			ConnectionHelper.close(rs, ps, null,conn);
		}
		deciFieldBean.setDataElement(list);
	}
	
	

}
