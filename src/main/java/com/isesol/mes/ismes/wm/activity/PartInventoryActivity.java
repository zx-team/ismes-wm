package com.isesol.mes.ismes.wm.activity;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.isesol.ismes.platform.core.service.bean.Dataset;
import com.isesol.ismes.platform.module.Bundle;
import com.isesol.ismes.platform.module.Parameters;
import com.isesol.ismes.platform.module.Sys;

import net.sf.json.JSONArray;

/**
 * 零件库存管理
 * @author Yang Fan
 *
 */
public class PartInventoryActivity {
	
	private SimpleDateFormat sdf_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Logger LOGGER = Logger.getLogger(this.getClass());
	
	/*
	 * 库存流水类别：入库
	 */
	private final String INVENTORY_TYPE_IN ="10";
	/*
	 * 库存流水类别：出库
	 */
	private final String  INVENTORY_TYPE_OUT ="20";
	
	/**
	 * 跳转零件库存管理界面
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_ljkcgl(Parameters parameters, Bundle bundle){
		return "wm_ljkcgl_kcgl";
	}

	
	/**
	 * 跳转入库界面
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_rkxx(Parameters parameters, Bundle bundle) {
		return "wm_ljkcgl_rkxx";
		
	}
	/**
	 * 跳转出库界面
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_ckxx(Parameters parameters, Bundle bundle) {
		return "wm_ljkcgl_ckxx";
	}
	
	/**查询库存信息
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception 
	 * @update YangFan 业务修改，查询已完成工单信息，包括箱号，报工确认数量。
	 */
	public String table_kcgl(Parameters parameters, Bundle bundle) throws Exception {
		
		String ljbh = parameters.getString("query_ljbh");
		String ljmc = parameters.getString("query_ljmc");
		String ljlx = parameters.getString("query_ljlxid");
		
		StringBuffer ljidSb = new StringBuffer();
		if (StringUtils.isNotBlank(ljbh) || StringUtils.isNotBlank(ljmc) || StringUtils.isNotBlank(ljlx)) {

			Parameters parts = new Parameters();
			parts.set("query_ljbh", ljbh);
			parts.set("query_ljmc", ljmc);
			parts.set("query_ljlb", ljlx);
			Bundle ljidBundle = Sys.callModuleService("pm", "pmservice_ljxxbybhmc", parts);
			
			List<Map<String, Object>> ljidList = (List<Map<String, Object>>) ljidBundle.get("ljxx");
			// List ljid = new ArrayList();
			if (null != ljidList && ljidList.size() > 0) {
				Iterator<Map<String, Object>> iterator = ljidList.iterator();
				ljidSb.append(" ljid in (");
				while (iterator.hasNext()) {
					// ljid.add(iterator.next());
					Map<String, Object> map = (Map<String, Object>) iterator.next();
					ljidSb.append(map.get("ljid")).append(",");
				}
				ljidSb.deleteCharAt(ljidSb.length() - 1);
				ljidSb.append(")");
			} else {
				ljidSb.append("1 = 0");
			}
		}
		
		//查询库存信息
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());
		/*Dataset datasetkcgl = Sys.query("kc_sjljkc", " ljid ,sum(kcsl) kcsl", ljidSb.toString(), " ljid ", null,
				(page - 1) * pageSize, pageSize, new Object[] {});*/
		
		Dataset datasetkcgl = Sys.query("lj_kcb", "sum(kcsl) kcsl,ljid", ljidSb.toString(), "ljid", null,
				(page - 1) * pageSize, pageSize, new Object[] {});
		List<Map<String, Object>> kcgl = datasetkcgl.getList();
		
		Dataset datasetkcmx = Sys.query("lj_kcmx", "ljid,mplh", ljidSb.toString(), null, new Object[]{});
		
		List<Map<String, Object>> kcmx_list = datasetkcmx.getList();
		
		/*Parameters parts = new Parameters();
		Bundle gdBundle = Sys.callModuleService("pl", "plservice_ywcgdxx", parts);
		List<Map<String, Object>> kcgl = (List<Map<String, Object>>) gdBundle.get("rows");
		*/
		if (kcgl.size()<=0) {
			return "json:";
		}
		
		StringBuffer con = new StringBuffer();
		
		if(null != kcgl && kcgl.size() > 0)
		{
			Iterator<Map<String, Object>> iterator = kcgl.iterator();
			con.append("(");
			while(iterator.hasNext()){
				Map<String, Object> map =  iterator.next();
				if(map != null  && map.get("kcsl") != null && 
						(new BigDecimal(map.get("kcsl").toString()).compareTo(BigDecimal.ZERO) == 1)){
					con.append(map.get("ljid")).append(",");
				}
			}
			con.deleteCharAt(con.length()-1);
			con.append(")");
					
		}

		//查询零件基本信息
		Parameters parts = new Parameters();
		parts.set("val_lj", con);
		Bundle ljBundle = Sys.callModuleService("pm", "pmservice_ljxx", parts);
		List<Map<String, Object>>ljList = (List<Map<String, Object>>) ljBundle.get("ljxx");
		if(ljList != null && ljList.size() > 0){
			Map<String, Object> kcMap = null; 
			for (int i = 0; i < kcgl.size();i++){
				kcMap = (Map<String, Object>) kcgl.get(i);
				Iterator<Map<String, Object>> ljIterator = ljList.iterator();
				while (ljIterator.hasNext()){
					Map<String, Object> ljMap = (Map<String, Object>) ljIterator.next();
					if(ljMap != null && kcMap.get("ljid").equals(ljMap.get("ljid"))){
						//ljid,ljbh,ljmc,ljlbdm,clbh,mpdh,ljtpid,fjdid,ljms,dwdm"
						kcMap.put("ljbh", ljMap.get("ljbh"));//零件编号
						kcMap.put("ljmc", ljMap.get("ljmc"));//零件名称
						kcMap.put("dw", ljMap.get("dw"));//单位代码
						kcMap.put("ljlbdm", ljMap.get("ljlbdm"));//零件类别代码  自制/采购
						kcMap.put("zxsl", ljMap.get("zxsl"));//装箱数量/生产数量
						break;
					}
				}
				
			}
		}
		
		for(int i = 0, len = kcgl.size(); i < len; i++){
			for(int j = 0, _len = kcmx_list.size(); j < _len; j++){
				if(kcgl.get(i).get("ljid").equals(kcmx_list.get(j).get("ljid"))){
					kcgl.get(i).put("mplh", kcmx_list.get(j).get("mplh"));
					break;
				}
			}
		}
		
		bundle.put("rows", kcgl);
		int totalPage = datasetkcgl.getTotal()%pageSize==0?datasetkcgl.getTotal()/pageSize:datasetkcgl.getTotal()/pageSize+1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", datasetkcgl.getTotal());
		return "json:";
	}
	
	/**库存详细页面
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String table_kcxx(Parameters parameters, Bundle bundle) throws Exception {
		String sortName = parameters.getString("sortName");
		String sortOrder = parameters.getString("sortOrder");
		String ljid = parameters.getString("ljid");
		String sort = "";
		
		if(!StringUtils.isEmpty(sortName)){
			sort = sortName + " " + sortOrder;
		} else {
			sort = "rksj asc";
		}
		
		
		if(StringUtils.isBlank(ljid))
		{
			return "json:";
		}
		String ljbh = parameters.getString("ljbh");
		String ljmc = parameters.getString("ljmc");
		
		String val_lj = "('"+ljid +"')";
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());
		
		Dataset datasetkcxx = Sys.query(new String[] {"lj_kcmx","wm_kfxxb","wm_kwxxb"},
				" lj_kcmx left join wm_kfxxb on lj_kcmx.kfid=wm_kfxxb.kfid left join wm_kwxxb on lj_kcmx.kwid=wm_kwxxb.kwid ",
				" kcmxid,kcid,pcid,ljid,scph,lj_kcmx.kfid,lj_kcmx.kwid,rksl,rksj,cksl,wm_kfxxb.kfmc, wm_kwxxb.kwmc, lj_kcmx.mplh, lj_kcmx.xh",
				" kczt = '10' and ljid in " +val_lj , null, sort, (page-1)*pageSize, pageSize,new Object[]{});
			
		List<Map<String, Object>> kcxx = datasetkcxx.getList();
		if(kcxx.size()<=0)
		{
			return "json:";
		}
		String val_pc = "(";
		for (int i = 0; i < kcxx.size(); i++) {
			if(i!=0)
			{
				val_pc = val_pc +",";
			}
			val_pc += "'" +kcxx.get(i).get("pcid")+"'";
		} 
		val_pc = val_pc +")";
		parameters.set("val_pc", val_pc);
		Bundle b_pcxx = Sys.callModuleService("pro", "proService_pcxxbyid", parameters);
		if (null==b_pcxx) {
			return "json:";
		}
		List<Map<String, Object>> pcxx = (List<Map<String, Object>>) b_pcxx.get("pcxx");
		if (pcxx.size()<=0) {
			return "json:";
		}
		
		for (int i = 0; i < kcxx.size(); i++) {
			for (int j = 0; j < pcxx.size(); j++) {
				if (kcxx.get(i).get("pcid").toString().equals(pcxx.get(j).get("pcid").toString())) {
					kcxx.get(i).put("pcbh", pcxx.get(j).get("pcbh"));
					kcxx.get(i).put("pcmc", pcxx.get(j).get("pcmc"));
					break;
				}
			}
			String rksj = (kcxx.get(i).get("rksj") + "").split("\\.")[0];
			kcxx.get(i).put("rksj", rksj);
			kcxx.get(i).put("ljbh", ljbh);
			kcxx.get(i).put("ljmc", ljmc);
			kcxx.get(i).put("zzjgid", "");//初始化，部门代码赋空值
		}
		bundle.put("rows", kcxx);
		int totalPage = datasetkcxx.getTotal()%pageSize==0?datasetkcxx.getTotal()/pageSize:datasetkcxx.getTotal()/pageSize+1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", datasetkcxx.getTotal());
		return "json:";
	}

	
	/** 出库查询
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String table_ckxx(Parameters parameters, Bundle bundle) throws Exception {
		String ljid = parameters.getString("ljid");
		String ljbh = parameters.getString("ljbh");
		String ljmc = parameters.getString("ljmc");
		
		if(StringUtils.isBlank(ljid))
		{
			return "wm_ljkcgl_ckxx";
		}
		
		String val_lj = "('"+ljid +"')";
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());
		//
		//Dataset datasetkcxx = Sys.query("kc_sjljkc"," sjljkcid,ljid,pcid,kfid,kwid,kcsl,czsj ", " ljid in " +val_lj , null, (page-1)*pageSize, pageSize,new Object[]{});
		Dataset datasetkcxx = Sys.query("wm_ljls"," kclsid，sjljkcid,ljid,pcid,kfid,kwid,kcsl,czsj,scph ", " kczt='0' and ljid in " +val_lj , null, (page-1)*pageSize, pageSize,new Object[]{});
		List<Map<String, Object>> kcxx = datasetkcxx.getList();
		if(kcxx.size()<=0)
		{
			return "wm_ljkcgl_ckxx";
		}
		
		StringBuffer val_pc = new StringBuffer("(");
		for (int i = 0; i < kcxx.size(); i++) {
			val_pc.append("'").append(kcxx.get(i).get("pcid")).append("'");
		} 
		val_pc.deleteCharAt(val_pc.length()-1);
		val_pc.append(")");
		
		parameters.set("val_pc", val_pc);
		Bundle b_pcxx = Sys.callModuleService("pro", "proService_pcxxbyid", parameters);
		if (null==b_pcxx) {
			return "wm_ljkcgl_ckxx";
		}
		List<Map<String, Object>> pcxx = (List<Map<String, Object>>) b_pcxx.get("pcxx");
		if (pcxx.size()<=0) {
			return "wm_ljkcgl_ckxx";
		}
		
		for (int i = 0; i < kcxx.size(); i++) {
			for (int j = 0; j < pcxx.size(); j++) {
				if (kcxx.get(i).get("pcid").toString().equals(pcxx.get(j).get("pcid").toString())) {
					kcxx.get(i).put("pcbh", pcxx.get(j).get("pcbh"));
					kcxx.get(i).put("pcmc", pcxx.get(j).get("pcmc"));
					break;
				}
			}
			kcxx.get(i).put("ljbh", ljbh);
			kcxx.get(i).put("ljmc", ljmc);
		}
		bundle.put("rows", kcxx);
		int totalPage = datasetkcxx.getTotal()%pageSize==0?datasetkcxx.getTotal()/pageSize:datasetkcxx.getTotal()/pageSize+1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", datasetkcxx.getTotal());
		return "wm_ljkcgl_ckxx";
	}
	
	
	/**入库
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public String update_rkxx(Parameters parameters, Bundle bundle) throws Exception {
		JSONArray jsonarray = JSONArray.fromObject(parameters.get("data_list"));  
		parameters.getMap();
		for (int i = 0; i < jsonarray.size(); i++) {
			Map<String, Object> lsmap = new HashMap<String, Object>(); 
			Map<String, Object> lsxxmap = new HashMap<String, Object>(); 
			Map<String, Object> kcmap = new HashMap<String, Object>(); 
			Date czsj = new Date(); // 操作时间
			lsmap = jsonarray.getJSONObject(i);
			int rksl = Math.abs(Integer.parseInt(lsmap.get("kcsl").toString())); //入库数量，正整数
			lsxxmap.put("ry_dm", Sys.getUserIdentifier());
			lsxxmap.put("sl", rksl);
			lsxxmap.put("czsj", czsj);
			lsxxmap.put("ljid", lsmap.get("ljid").toString());
			lsxxmap.put("pcid", lsmap.get("pcid").toString());
			lsxxmap.put("kfid", lsmap.get("kfid").toString());
			lsxxmap.put("kwid", lsmap.get("kwid").toString());
			lsxxmap.put("zzjgid", lsmap.get("zzjgid").toString());
			lsxxmap.put("bz", lsmap.get("bz").toString());
			lsxxmap.put("kclslbdm", lsmap.get("kclslbdm").toString());
			lsxxmap.put("kczt", "1");//库存状态，已出库
			
			int ljid = Integer.parseInt(lsmap.get("ljid").toString());
			int pcid = Integer.parseInt(lsmap.get("pcid").toString());
			int kfid = Integer.parseInt(lsmap.get("kfid").toString());
			int kwid = Integer.parseInt(lsmap.get("kwid").toString());
			
			//查询是否有相同零件、批次、库房和库位的信息,如果有则更新库存信息,如果没有则新增库存信息。
			Dataset datasetkcgl = Sys.query("kc_sjljkc","sjljkcid,kcsl", "ljid = ? ", null, new Object[]{ljid});
			List<Map<String, Object>> kcgl = datasetkcgl.getList();
			if(kcgl.size()<=0)
			{
				kcmap.put("ljid", lsxxmap.get("ljid"));
				kcmap.put("pcid", lsxxmap.get("pcid"));
				kcmap.put("kfid", lsxxmap.get("kfid"));
				kcmap.put("kwid", lsxxmap.get("kwid"));
				kcmap.put("kcsl", lsxxmap.get("sl"));
				kcmap.put("czsj", czsj);
				int n = Sys.insert("kc_sjljkc", kcmap);
				if(n==1)
				{
					lsxxmap.put("sjkcid", kcmap.get("sjljkcid"));
				}
				n = Sys.insert("wm_ljls", lsxxmap);
			}else{
				kcmap.put("kcsl", Float.parseFloat(kcgl.get(0).get("kcsl").toString())+rksl);
				kcmap.put("czsj", lsxxmap.get("czsj"));
				int n = Sys.update("kc_sjljkc", kcmap, "sjljkcid = ?", new Object[]{kcgl.get(0).get("sjljkcid")});
				if(n==1)
				{
					lsxxmap.put("sjkcid", kcgl.get(0).get("sjljkcid"));
				}
				n = Sys.insert("wm_ljls", lsxxmap);
			}
		}  
		return null;
	}
	
	private Bundle sendActivity(String type, String templateId, boolean isPublic, String[] roles, String[] users, String[] group,
			Map<String, Object> data) {
		String PARAMS_TYPE = "type";
		String PARAMS_TEMPLATE_ID = "template_id";
		String PARAMS_PUBLIC = "public";
		String PARAMS_ROLE = "role";
		String PARAMS_USER = "user";
		String PARAMS_GROUP = "group";
		String PARAMS_DATA = "data";
		String SERVICE_NAME = "activity";
		String METHOD_NAME = "send";
		Parameters parameters = new Parameters();
		parameters.set(PARAMS_TYPE, type);
		parameters.set(PARAMS_TEMPLATE_ID, templateId);
		if (isPublic)
			parameters.set(PARAMS_PUBLIC, "1");
		if (roles != null && roles.length > 0)
			parameters.set(PARAMS_ROLE, roles);
		if (users != null && users.length > 0)
			parameters.set(PARAMS_USER, users);
		if (group != null && group.length > 0)
			parameters.set(PARAMS_GROUP, group);
		if (data != null && !data.isEmpty())
			parameters.set(PARAMS_DATA, data);
		return Sys.callModuleService(SERVICE_NAME, METHOD_NAME, parameters);
	}
	
	/**出库
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String update_ckxx(Parameters parameters, Bundle bundle) throws Exception {
		JSONArray jsonarray = JSONArray.fromObject(parameters.get("data_list"));  
		for (int i = 0; i < jsonarray.size(); i++) {
			Map<String, Object> lsmap = new HashMap<String, Object>(); 
			Date czsj = new Date();
			lsmap = jsonarray.getJSONObject(i);
			
			Dataset kcxx_set = Sys.query("lj_kcb", "kcid,kcsl", "kcid = ?", null, new Object[]{lsmap.get("kcid").toString()});
			
			Map<String, Object> kcxx = kcxx_set.getMap();
			
			BigDecimal bd = new BigDecimal(kcxx.get("kcsl").toString());
			
			BigDecimal kcsl_result = bd.subtract(new BigDecimal(lsmap.get("kcsl").toString()));
			
			Map<String, Object> param_map = new HashMap<String, Object>();
			param_map.put("kcsl", kcsl_result);
			//更新库存表,库存数量 = 库存数量-出库数量
			int n = Sys.update("lj_kcb", param_map, "kcid = ?", new Object[]{lsmap.get("kcid").toString()});
			
			Dataset kcmx_set = Sys.query("lj_kcmx", "rksj,kcmxid", "kcmxid = ?", null, new Object[]{lsmap.get("kcmxid").toString()});
			
			Map<String, Object> kcmx_map = kcmx_set.getMap();
			
			String kcmx_rksj = kcmx_map.get("rksj").toString();
			
			String day = getDate();
			
			//获取在库时间
			String zksj = getDays(day, kcmx_rksj)+"";
			
			param_map.clear();
			
			param_map.put("zksj", zksj);
			param_map.put("cksj", new Date());
			param_map.put("ckry", Sys.getUserName());
			param_map.put("kczt", 20);
			param_map.put("cksl", lsmap.get("cksl").toString());
			param_map.put("ckkhdh", lsmap.get("khdh").toString());
			n = Sys.update("lj_kcmx", param_map, "kcmxid = ?", new Object[]{lsmap.get("kcmxid").toString()});
			
			param_map.clear();
			param_map.put("kcmxid", lsmap.get("kcmxid").toString());
			param_map.put("kfid", lsmap.get("kfid").toString());
			param_map.put("kwid", lsmap.get("kwid").toString());
			param_map.put("czlx", 20);
			param_map.put("sl", lsmap.get("cksl").toString());
			param_map.put("kcsl", kcsl_result);
			param_map.put("czry", Sys.getUserName());
			param_map.put("czsj", new Date());
			param_map.put("xh", lsmap.get("xh").toString());
			param_map.put("scph", lsmap.get("scph").toString());
			param_map.put("mplh", lsmap.get("mplh").toString());
			
			n = Sys.insert("lj_kcls", param_map);
			
			//查询物料出库流水
			Dataset rkxx = Sys.query(new String[]{"lj_kcls","wm_kwxxb","wm_kfxxb"},					
					"lj_kcls join wm_kfxxb on lj_kcls.kfid = wm_kfxxb.kfid "
					+ " join wm_kwxxb on lj_kcls.kwid = wm_kwxxb.kwid ",					
					"lj_kcls.sl,wm_kwxxb.kwmc,wm_kfxxb.kfmc,"
					+ "wm_kfxxb.zzjgid", "lj_kcls.kclsid = ?" ,
					null, null,new Object[]{param_map.get("kclsid")});
			List<Map<String, Object>> rkxxList = rkxx.getList();
			//查询物料名称
			Parameters ljxxCon = new Parameters();
			ljxxCon.set("val_lj", "("+lsmap.get("ljid") + ")");
			Bundle result = Sys.callModuleService("pm", "pmservice_ljxx", ljxxCon);
			List<Map<String,Object>> ljxx = (List<Map<String, Object>>) result.get("ljxx");
			parameters.set("ljid", lsmap.get("ljid"));
			Bundle resultLjUrl = Sys.callModuleService("pm", "partsInfoService", parameters);
			Object ljtp = ((Map)resultLjUrl.get("partsInfo")).get("url");
			//查询批次名称
			List<Map<String, Object>> pcxx = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> pcrwbhxx = new ArrayList<Map<String,Object>>();
			if(lsmap.get("pcid") != null){
				Parameters pcCon = new Parameters();
				pcCon.set("val_pc", "("+lsmap.get("pcid")+")");
				Bundle resultPcxx = Sys.callModuleService("pro", "proService_pcxxbyid", pcCon);
				pcxx = ((List<Map<String, Object>>)resultPcxx.get("pcxx"));
				pcCon.set("pcid", "("+lsmap.get("pcid")+")");
				Bundle resultScrw = Sys.callModuleService("pro", "proServicePcbhByPcidService", pcCon);
				pcrwbhxx = ((List<Map<String, Object>>)resultScrw.get("scrwpc"));
			}
			//查询组织机构名称
			Parameters orgCon = new Parameters();
			orgCon.set("id", lsmap.get("zzjgid"));
			Bundle resultOrg = Sys.callModuleService("org", "nameService", orgCon);
			String activityType = "3"; //动态类型
			String[] roles = new String[] { "STOCKMAN_ROLE","MANUFACTURING_MANAGEMENT_ROLE" };//关注该动态的角色
			String templateId = "ljck_tp";
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("khdh", lsmap.get("khdh").toString());//客户单号
			data.put("ljbh", ljxx.get(0).get("ljbh"));//零件编号
			data.put("ljmc", ljxx.get(0).get("ljmc"));//零件名称
			data.put("sl", lsmap.get("cksl"));//入库数量
			data.put("kwmc", rkxxList.get(0).get("kwmc"));//库位
			data.put("kfmc", rkxxList.get(0).get("kfmc"));//库房
			if(pcxx!=null && pcxx.size() > 0){
				data.put("pcid", pcxx.get(0).get("pcid"));//批次id
				data.put("pcmc", pcxx.get(0).get("pcmc"));//批次名称
			}
			if(pcrwbhxx!=null && pcrwbhxx.size() > 0){
				data.put("pcbh", pcrwbhxx.get(0).get("pcbh"));//批次编号
				data.put("scrwbh", pcrwbhxx.get(0).get("scrwbh"));//生产任务编号
			}
			data.put("zzjgid", rkxxList.get(0).get("zzjgid"));//部门ID
			data.put("zzjgmc", resultOrg.get("name"));//部门 名称
			data.put("userid", Sys.getUserIdentifier());//操作人
			data.put("username", Sys.getUserName());//操作人
			data.put("cksj", czsj.getTime());//出库时间
			data.put("ljtp", ljtp);//零件图片
			String dw = ljxx.get(0).get("dw") != null? ljxx.get(0).get("dw").toString() : "";
			String[] dwmc = null;
			if(dw!=null){
				dwmc = Sys.getDictFieldNames("JLDW", dw);
			}
			data.put("dw", dwmc[0]);//零件单位
			sendActivity(activityType, templateId, true, roles, null, null, data);
		}  
		return null;
	}
	
	
	/**零件编号查询零件信息
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String ljxxSelect(Parameters parameters,Bundle bundle){
		parameters.set("query_ljbh", parameters.get("query"));
		//查询零件信息
		Bundle b_ljxx = Sys.callModuleService("pm", "pmservice_ljxxbybhmc", parameters);
		List<Map<String, Object>> ljxx = (List<Map<String, Object>>) b_ljxx.get("ljxx");
		if (ljxx.size()<=0) {
			return "json:";
		}
		for (int i = 0; i < ljxx.size(); i++) {
			ljxx.get(i).put("dwmc",Sys.getDictFieldNames("JLDW", (String)ljxx.get(i).get("dw")));
			ljxx.get(i).put("value", ljxx.get(i).get("ljid"));
			ljxx.get(i).put("label", ljxx.get(i).get("ljbh"));
			ljxx.get(i).put("title", ljxx.get(i).get("ljbh"));
		}
		bundle.put("ljxx", ljxx);
		return "json:ljxx";
	}
	
	/**零件编号查询零件信息
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String pcxxSelect(Parameters parameters,Bundle bundle){
		parameters.set("pcbh", parameters.get("query"));
		//查询零件信息
		Bundle b_ljxx = Sys.callModuleService("pro", "proServiceScrwByScrwpcbhService", parameters);
		List<Map<String, Object>> pcxx = (List<Map<String, Object>>) b_ljxx.get("scrwpc");
		if (pcxx.size()<=0) {
			return "json:";
		}
		for (int i = 0; i < pcxx.size(); i++) {
			pcxx.get(i).put("value", pcxx.get(i).get("scrwpcid"));
			pcxx.get(i).put("label", pcxx.get(i).get("pcbh"));
			pcxx.get(i).put("title", pcxx.get(i).get("pcbh"));
		}
		bundle.put("pcxx", pcxx);
		return "json:pcxx";
	}
	
	/**
	 * 选择车间
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String cjSelect(Parameters parameters,Bundle bundle){
		Bundle b = Sys.callModuleService("org", "cjService", parameters);
		if(b == null){
			bundle.put("select_cj", new Object[]{});
			return "json:select_cj";
		}
		List<Map<String,Object>> cjList = (List<Map<String, Object>>) b.get("data");
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map : cjList){
			Map<String,Object> m = new HashMap<String, Object>();
			m.put("label", map.get("zzjgmc"));
			m.put("value", map.get("zzjgid"));
			returnList.add(m);
		}
		bundle.put("select_cj", returnList.toArray());
		return "json:select_cj";
	}
	
	/**
	 * 选择库房
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String kfSelect(Parameters parameters,Bundle bundle){
		Dataset datasetkfgl = Sys.query("wm_kfxxb", " kfid, kfbh, kfmc, kfwz, zzjgid, qybsdm ", "qybsdm = '10' " , " kfid desc ",new Object[] {});
		
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> kfgl = datasetkfgl.getList();
		for(Map<String,Object> map : kfgl){
			Map<String,Object> m = new HashMap<String, Object>();
			m.put("label", map.get("kfmc"));
			m.put("value", map.get("kfid"));
			returnList.add(m);
		}
		bundle.put("select_kf", returnList.toArray());
		return "json:select_kf";
	}
	
	/**
	 * 选择库位
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String kwSelect(Parameters parameters, Bundle bundle) {
		String parentId = parameters.getString("parent");
		if (StringUtils.isNotBlank(parentId)) {
			Dataset datasetkfgl = Sys.query("wm_kwxxb", " kwid, kwbh, kwmc, kwwz, kfid, qybsdm ",
					"qybsdm = '10' and kfid=?", " kfid desc ", new Object[] { parentId });

			List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> kfgl = datasetkfgl.getList();
			for (Map<String, Object> map : kfgl) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("label", map.get("kwmc"));
				m.put("value", map.get("kwid"));
				returnList.add(m);
			}
			bundle.put("select_kw", returnList.toArray());
		}
		return "json:select_kw";
	}
	
	public long getDays(String begin, String end){
	 	long day=0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");    
        java.util.Date beginDate;
        java.util.Date endDate;
        try
        {
            beginDate = format.parse(begin);
            endDate= format.parse(end);    
            day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);    
            //System.out.println("相隔的天数="+day);   
        } catch (ParseException e)
        {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }   
        
        return day;
	}
	public String getDate(){
		
		String str = "";
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=new Date();
		str = dateFormater.format(date);
		return str;
	} 

}
