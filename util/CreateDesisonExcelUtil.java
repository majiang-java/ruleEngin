package com.ruleEngine.util;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.ruleEngine.bean.DataElement;
import com.ruleEngine.bean.DeciFieldBean;
import com.ruleEngine.bean.DecisionTableBean;


public class CreateDesisonExcelUtil {
	
	private static CreateDesisonExcelUtil instance;
	private static int height = 1;
	public static CreateDesisonExcelUtil getInstance(){
		height = 1;
		if(instance == null){
			instance = new CreateDesisonExcelUtil();
		}
		return instance;
	}
	

	public void createExcel(DecisionTableBean decisionBean) throws Exception{
		//WorkBook wb = new WorkBook();
		
		WritableWorkbook wwb = null;
		try {
			File file = new File("c:\\excel2.xls");
			if(!file.exists()){
				file.createNewFile();
			}
			wwb = Workbook.createWorkbook(file);
			
			WritableSheet ws = wwb.createSheet("Tables", 0); 
			WritableCellFormat format = new WritableCellFormat();//
			 
			format.setAlignment(Alignment.LEFT);//
			format.setBorder(Border.ALL,BorderLineStyle.THIN);//
			format.setWrap(true);//
			format.setShrinkToFit(true);
			 

		    // CREATE HEAD
		//	WritableFont wf = new jxl.write.WritableFont(WritableFont.TIMES, 18, WritableFont.BOLD, true);
			//                 height+=2;      
			Label labelRuleSetA = new jxl.write.Label(1, height, "RuleSet",format); 
			ws.addCell(labelRuleSetA);
			Label labelRuleSetB = new jxl.write.Label(2, height, "package com.drools.rules.table",format); 
			ws.addCell(labelRuleSetB);
			Label labelImportA = new jxl.write.Label(1, ++height, "Import",format); 
			ws.addCell(labelImportA);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < decisionBean.getCatList().size(); i++) {
				sb.append("com.ruleEngine.domain.").append(decisionBean.getCatList().get(i));
				if(i != decisionBean.getCatList().size()-1){
					sb.append(",");
				}
			}
			Label labelImportB = new jxl.write.Label(2, height, sb.toString()); 
			ws.addCell(labelImportB);
			
			
			Label labelSequentialA = new jxl.write.Label(1, ++height, "Sequential",format); 
			ws.addCell(labelSequentialA);
			Label labelSequentialB = new jxl.write.Label(2, height, "true",format); 
			ws.addCell(labelSequentialB);
			height += 2;
			//create body
			for (int i = 0; i < decisionBean.getFieldList().size(); i++) {
				DeciFieldBean deciFieldBean =  decisionBean.getFieldList().get(i);
				Label labelTableHead = new Label(1, ++height, "RuleTable"+deciFieldBean.getGenFieldPriDesc(),format); 
				ws.addCell(labelTableHead);
				Label conditionHead = new Label(1, ++height, "CONDITION",format); 
				ws.addCell(conditionHead);
				Label actionHead = new Label(2, height, "ACTION",format); 
				ws.addCell(actionHead);
				Label conditionCat = new Label(1, ++height, deciFieldBean.getGenFieldCat(),format);
				ws.addCell(conditionCat);
				//核心部位
				if("VARCHAR".equals(deciFieldBean.getGenFieldDataType()) 
						|| "VARCHAR2".equals(deciFieldBean.getGenFieldDataType())){
					Label condition = new Label(1, ++height, deciFieldBean.getGenFieldCol(),format);
					ws.addCell(condition);
					Label action = new Label(2, height, "mylist.add($param);",format);
					ws.addCell(action);
					//名称
					Label conditionName = new Label(1, ++height, deciFieldBean.getGenFieldPriDesc(),format);
					ws.addCell(conditionName);
					Label actionName = new Label(2, height, "分数",format);
					ws.addCell(actionName);
					for (DataElement dataElement : deciFieldBean.getDataElement()) {
						Label actionNameValue = new Label(1,++height, dataElement.getHighValue(),format );
						ws.addCell(actionNameValue);
						Label actionNameScore = new Label(2, height, dataElement.getScore(),format);
						ws.addCell(actionNameScore);
					}
				}else if("NUMBER".equals(deciFieldBean.getGenFieldDataType()) 
						|| "DATE".equals(deciFieldBean.getGenFieldDataType())){
					Label condition = new Label(1, ++height, deciFieldBean.getGenFieldCol()+" >= $1, "+deciFieldBean.getGenFieldCol()+" <= $2",format);
					ws.addCell(condition);
					Label action = new Label(2, height, "mylist.add($param);",format);
					ws.addCell(action);
					//名称
					Label conditionName = new Label(1, ++height, deciFieldBean.getGenFieldPriDesc(),format);
					ws.addCell(conditionName);
					Label actionName = new Label(2, height, "分数",format);
					ws.addCell(actionName);
					for (DataElement dataElement : deciFieldBean.getDataElement()) {
						Label actionNameA = new Label(1, ++height, dataElement.getLowValue()+","+dataElement.getHighValue(),format);
						ws.addCell(actionNameA);
						Label actionNameB = new Label(2, height, dataElement.getScore(),format);
						ws.addCell(actionNameB);
					}
				}
				height += 3;
				
			}
			//Variables	java.util.List mylist
			Label variables = new Label(1, ++height, "Variables",format);
			ws.addCell(variables);
			Label value = new Label(2, height, "java.util.List mylist",format);
			ws.addCell(value);
			
			wwb.write();
		} catch (IOException e) {
			throw new Exception(e);
		} catch (RowsExceededException e) {
			throw new Exception(e);
		} catch (WriteException e) {
			throw new Exception(e);
		} finally{
			wwb.close();
		}
		
	}

}
