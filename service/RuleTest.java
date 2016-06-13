package com.ruleEngine.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.ruleEngine.bean.DecisionTableBean;
import com.ruleEngine.dao.HotClassComplier;
import com.ruleEngine.dao.RuleEngineType;
import com.ruleEngine.dao.RuleParam;
import com.ruleEngine.dao.helper.DesisonTableHelper;
import com.ruleEngine.db.ConnectionHelper;
import com.ruleEngine.util.CreateDesisonExcelUtil;

public class RuleTest {
//	public static void main(String[] args) throws Exception {
//		ApplicationContext ac = new FileSystemXmlApplicationContext("D:/Workspaces/SimpleFrameWork/WebRoot/WEB-INF/applicationContext.xml");
//		RuleService ruleService = (RuleService) ac.getBean("ruleIntfService");
//		Map<String, Object> hm = HotClassComplier.getInitDataMap();
//		RuleParam param = new RuleParam();
////	    param.setGEN_RULE_ID("0002");
//		HotClassComplier.invokeFieldMethod(hm.get("APPT"), "setAPPT_AGE", new Object[]{23.0});
//		HotClassComplier.invokeFieldMethod(hm.get("APPL"), "setAPPL_LOAN_TYPE", new Object[]{"A102"});
//		HotClassComplier.invokeFieldMethod(hm.get("APPT"), "setAPPT_INDIV_CHILD_STATUS", new Object[]{"CHILD"});
//		param.setGEN_RULE_ID("0001");
//		param.setRULE_FALG(RuleEngineType.DECISION_TAB);
//////	    param.setGEN_RULE_ID("0002");
//////		param.setRULE_FALG(RuleEngineType.DECISION_TAB);
////		
////	    param.setGEN_RULE_ID("flow001");
////		param.setRULE_FALG(RuleEngineType.RULE_FLOW);
//		boolean i = ruleService.callRuleEngine(param, hm);
////		System.out.println(i);
//	//	RuleGroupBean bean = RuleGroupHelper.getRuleGroupFromDB(param);
//
//	}
	
	public static void main(String[] args) throws Exception {
		ApplicationContext ac = new FileSystemXmlApplicationContext("D:/Workspaces/SimpleFrameWork/WebRoot/WEB-INF/applicationContext.xml");

		DecisionTableBean decisionTableBean = DesisonTableHelper.getDesionTableFromDB("CREDIT");
		CreateDesisonExcelUtil.getInstance().createExcel(decisionTableBean);
//		createRule();
//		StringBuilder sb = new StringBuilder();
//		sb.append("package com.ruleEngine.rulegroup;").append("\n");
//		sb.append("import com.ruleEngine.domain.APPL;").append("\n");
//		sb.append("import com.ruleEngine.dao.RuleEngineFlowAdpter")
//				.append("\n");
//		sb.append("import java.util.Date").append("\n");
//		sb.append("global com.ruleEngine.bean.RuleBean ruleBean").append("\n");
//		sb.append("rule 'rule123'").append("\n");
//		sb.append("when ").append("\n");
//		sb.append("$APPL:APPL(); ").append("\n");
//		sb.append("eval(($APPL.getAPPL_LOAN_TYPE()==\"A102\"));").append("\n");
//		sb.append("then ").append("\n");
//		sb.append("ruleBean.setResult(true);").append("\n");
//		sb.append(" end").append("\n");
//		 StatefulKnowledgeSession ksession = RuleResourseUtil.getDrlResourseFromStream(sb.toString());
//		 RuleBean ruleBean = new RuleBean();
//		 ruleBean.setResult(false);
//		 ksession.setGlobal("ruleBean", ruleBean);
//		 APPL appl = new APPL();
//		 appl.setAPPL_LOAN_TYPE("A102");
//		 ksession.insert(appl);
//		 ksession.fireAllRules();
//		 ksession.dispose();
	}

	private static void createRule() throws Exception {
		RuleParam param = new RuleParam();
		param.setGEN_RULE_ID("goup123");
		param.setRULE_FALG(RuleEngineType.RULE_GROUP);
	
		Map<String,Object[]> hm = new HashMap<String,Object[]>();
	
		Object[] appts = HotClassComplier.getFieldArray("APPT", 2);
		HotClassComplier.invokeFieldMethod(appts[0], "setAPPT_INDIV_NO_DEPENDENT", new Object[]{new Double(32.0)});
		HotClassComplier.invokeFieldMethod(appts[0], "setAPPT_AGE", new Object[]{new Double(32.0)});
		HotClassComplier.invokeFieldMethod(appts[1], "setAPPT_INDIV_NO_DEPENDENT", new Object[]{new Double(32.0)});
		HotClassComplier.invokeFieldMethod(appts[1], "setAPPT_AGE", new Object[]{new Double(32.0)});
		hm.put("APPT", appts);

		Object[] appls = HotClassComplier.getFieldArray("APPL", 2);
		HotClassComplier.invokeFieldMethod(appls[0], "setAPPL_LOAN_TYPE", new Object[]{"A102"});
		HotClassComplier.invokeFieldMethod(appls[1], "setAPPL_LOAN_TYPE", new Object[]{"A101"});
		hm.put("APPL", appls);
		
		
		boolean b = RuleService.callRuleEngin(param, hm);
		System.out.println(b);
	}

	public void test() throws SQLException{
	     Connection conn = ConnectionHelper.getConnection();
		 String sql ="{call ?:= PA_TX_LOG.GEN_BACK_TRANS_WS_N(?,?,?,?,?,?,?,?,?,?,?)}";
		 CallableStatement cs = conn.prepareCall(sql);
		 cs.registerOutParameter(1, Types.INTEGER);
		 cs.setString(2, "12445678991234567890123456789014");
		 cs.setString(3, "LOANQUERY");
		 cs.setString(4, "LOAN");
		 cs.setString(5, "5065");
		 cs.setString(6, "贷款详细信息查询");
		 cs.setString(7, "1|1|1|1|1|12345678991234567890123456789014|1|1|1|");
		 cs.setString(8, "");
		 cs.setString(9, "0101201280433801");
		 cs.setString(10, "F");
		 cs.setString(11, "1");
		 cs.registerOutParameter(12, Types.VARCHAR);
		 cs.execute();
		 System.out.println(cs.getInt(1));
		 System.out.println(cs.getString(12));
		 ConnectionHelper.close(null, cs, null, conn);
	}

	

}
