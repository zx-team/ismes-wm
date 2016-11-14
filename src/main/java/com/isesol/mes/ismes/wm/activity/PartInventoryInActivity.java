package com.isesol.mes.ismes.wm.activity;

import java.math.BigDecimal;
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

/**
 * 零件入库
 * @author Yang Fan
 *
 */
public class PartInventoryInActivity {
	
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
	 * 跳转零件库入库界面
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_ljxx(Parameters parameters, Bundle bundle){
		return "wm_ljkcgl_rk";
	}
	
	/**查询库存信息,从工单表中查询尾序未入库的工单信息。
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception 
	 */
	public String table_ljscxx(Parameters parameters, Bundle bundle) throws Exception {
		
		String ljbh = parameters.getString("query_ljbh");
		String scrwbh = parameters.getString("query_scrwbh");
		String pcbh = parameters.getString("query_pcbh");
		
		List<Map<String, Object>> ljidList = null;
		
		StringBuffer ljidSb = new StringBuffer();
		if (StringUtils.isNotBlank(ljbh)) {

			Parameters parts = new Parameters();
			parts.set("query_ljbh", ljbh);
			
			Bundle ljidBundle = Sys.callModuleService("pm", "pmservice_ljxxbybhmc", parts);
			
			ljidList = (List<Map<String, Object>>) ljidBundle.get("ljxx");
			if (null != ljidList && ljidList.size() > 0) {
				Iterator<Map<String, Object>> iterator = ljidList.iterator();
				ljidSb.append(" (");
				while (iterator.hasNext()) {
					Map<String, Object> map = (Map<String, Object>) iterator.next();
					ljidSb.append(map.get("ljid")).append(",");
				}
				ljidSb.deleteCharAt(ljidSb.length() - 1);
				ljidSb.append(")");
			}else 
			{
				ljidSb.append(" ( 1=0 ) ");
			}
		}
		
		//查询生产任务完成情况
		/*Parameters scrwParts = new Parameters();
		scrwParts.set("ljid", ljidSb.toString());
		scrwParts.set("scrwbh", scrwbh);
		scrwParts.set("pcbh", pcbh);
		scrwParts.set("pcztdm", "80");
		
		scrwParts.set("page",parameters.get("page"));
		scrwParts.set("pageSize",parameters.get("pageSize"));
		Bundle scrwpcBundle = Sys.callModuleService("pro", "proServiceScrwpcxxService", scrwParts);*/
		
//		List<Map<String, Object>> scrwpcList = (List<Map<String, Object>>) scrwpcBundle.get("pcxx");
		
		Parameters parts = new Parameters();
		parts.set("page",parameters.get("page"));
		parts.set("pageSize",parameters.get("pageSize"));
		Bundle scrwpcBundle = Sys.callModuleService("pl", "plservice_ywcgdxx", parts);
		List<Map<String, Object>> scrwpcList = (List<Map<String, Object>>) scrwpcBundle.get("rows");
		
		if(!scrwpcList.isEmpty()){
			Iterator scrwpcIterator = scrwpcList.iterator();
			StringBuffer ljidResultSb = new StringBuffer("(");
			StringBuffer scrwResultSb = new StringBuffer("(");
			for(int i = 0;i<scrwpcList.size();i++){
				Map<String,Object> scrwxx = (Map<String,Object>) scrwpcList.get(i);
				String ljid =  scrwxx.get("ljid").toString();
				String pcid = scrwxx.get("pcid").toString();
				ljidResultSb.append(ljid).append(",");
				scrwResultSb.append(pcid).append(",");
			}
			//查询零件编号信息
			ljidResultSb.deleteCharAt(ljidResultSb.length()-1);
			ljidResultSb.append(")");
			
			//查询生产任务信息
			scrwResultSb.deleteCharAt(scrwResultSb.length()-1);
			scrwResultSb.append(")");
			
			parts = new Parameters();
			parts.set("val_lj", ljidResultSb.toString());
			Bundle ljxxBundle = Sys.callModuleService("pm", "pmservice_ljxx", parts);

			if (null != ljxxBundle) {
				List<Map<String,Object>> ljxxList= (List<Map<String, Object>>) ljxxBundle.get("ljxx");
				for (int i = 0; i < scrwpcList.size(); i++) {
					Iterator ljIterator = ljxxList.iterator();
					Map<String, Object> scrwxx = (Map<String, Object>) scrwpcList.get(i);
					String ljid = scrwxx.get("ljid").toString();
					while (ljIterator.hasNext()) {
						Map<String, Object> ljxx = (Map<String, Object>) ljIterator.next();
						if (ljid.equals(ljxx.get("ljid").toString())) {
							scrwxx.put("ljbh", ljxx.get("ljbh"));
							scrwxx.put("ljmc", ljxx.get("ljmc"));
							scrwxx.put("dw", ljxx.get("dw"));
							scrwxx.put("zxsl", ljxx.get("zxsl"));//装箱数量/生产数量
							break;
						}
					}
				}
			}
			
			parts = new Parameters();
			parts.set("val_lj", scrwResultSb.toString());
			Bundle pcxxBundle = Sys.callModuleService("pro", "proService_getScrwpcxxBypcid", parts);
			if (null != pcxxBundle) {
				List<Map<String, Object>> pcxxList = (List<Map<String,Object>>)pcxxBundle.get("pcxx");
				for (int i = 0; i < scrwpcList.size(); i++) {
					Iterator pcIterator = pcxxList.iterator();
					Map<String, Object> scrwxx = (Map<String, Object>) scrwpcList.get(i);
					String pcid = scrwxx.get("pcid").toString();
					while (pcIterator.hasNext()) {
						Map<String, Object> pcxx = (Map<String, Object>) pcIterator.next();
						if (pcid.equals(pcxx.get("scrwpcid").toString())) {
							scrwxx.put("scrwbh", pcxx.get("scrwbh"));//生产任务编号
							scrwxx.put("scrwpcid", pcxx.get("scrwpcid"));//生产任务编号
							scrwxx.put("pcbh", pcxx.get("pcbh"));//批次编号
							scrwxx.put("scph", pcxx.get("scph"));//生产批号 2016/10/12 生产表中未增加
							scrwxx.put("mplh", pcxx.get("mplh"));//毛坯炉号 2016/10/12 生产表中未增加
							scrwxx.put("pcjhksrq", pcxx.get("pcjhksrq"));//批次计划开始时间
							scrwxx.put("pcjhwcrq", pcxx.get("pcjhwcrq"));//批次计划完成时间
							scrwxx.put("pcjhztdm", pcxx.get("pcjhztdm"));//批次计划状态代码，以前是显示已完成状态的批次，现在是按照工单（箱）入库，所以应该显示工单的状态
							break;
						}
					}
				}
			}
		}
		
		bundle.put("rows", scrwpcList);
		bundle.put("totalPage", scrwpcBundle.get("totalPage"));
		bundle.put("currentPage", scrwpcBundle.get("currentPage"));
		bundle.put("totalRecord", scrwpcBundle.get("totalRecord"));
		return "json:";
	}
	
	/**
	 * 保存入库信息
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	public String save_rkxx(Parameters parameters, Bundle bundle) throws Exception {
		Map<String, Object> lsmap = parameters.getMap();
		if (null != lsmap) {

			Map<String, Object> lsxxmap = new HashMap<String, Object>();
			Date czsj = new Date(); // 操作时间
			int rksl = 0;
			if(null != lsmap.get("add_rksl")){
				rksl = Math.abs(Integer.parseInt(lsmap.get("add_rksl").toString())); // 入库数量，正整数
			}
			
			lsxxmap.put("ry_dm", Sys.getUserIdentifier());
			lsxxmap.put("sl", rksl);
			lsxxmap.put("czsj", czsj);
			lsxxmap.put("ljid", lsmap.get("add_ljid"));
			lsxxmap.put("pcid", lsmap.get("add_scrwpcid"));
			lsxxmap.put("kfid", lsmap.get("add_kfid"));
			lsxxmap.put("kwid", lsmap.get("add_kwid"));
			lsxxmap.put("zzjgid", lsmap.get("add_zzjgid"));
			lsxxmap.put("bz", lsmap.get("add_bz"));
			lsxxmap.put("kclslbdm", this.INVENTORY_TYPE_IN);//库存类别：入库
			lsxxmap.put("yggh", Sys.getUserIdentifier());//员工号
			lsxxmap.put("kczt", "0");//库存状态，未出库：0
			
			//新增箱号、生产批号、工单id
			lsxxmap.put("xh", lsmap.get("add_xh"));//箱号
			lsxxmap.put("scph", lsmap.get("add_scph"));//生产批号
			lsxxmap.put("mplh", lsmap.get("add_mplh"));//毛坯炉号
			//入库人员，入库时间
			lsxxmap.put("rksj", czsj);//入库时间
			lsxxmap.put("rkry", Sys.getUserIdentifier());//入库员工号

			int ljid = Integer.parseInt(lsmap.get("add_ljid").toString());
			int gdid = Integer.parseInt(lsmap.get("add_gdid").toString());
			int pcid = Integer.parseInt(lsmap.get("add_scrwpcid").toString());
			int kfid = Integer.parseInt(lsmap.get("add_kfid").toString());
			int kwid = Integer.parseInt(lsmap.get("add_kwid").toString());

			//通过零件ID,库房ID,库位ID查询零件库存汇总表,if(有匹配)修改数据,else添加数据
			Dataset datasetkcgl = Sys.query("lj_kcb", "kcid,kcsl",
					"ljid = ? and kfid = ? and kwid = ?", null, new Object[] { ljid, kfid, kwid });
			List<Map<String, Object>> kcgl = datasetkcgl.getList();
			
			Map<String, Object> kchz_map = new HashMap<String, Object>();//库存汇总
			Map<String, Object> kcmx_map = new HashMap<String, Object>();//库存明细
			Map<String, Object> kcls_map = new HashMap<String, Object>();//库存流水
			
			//设置库存汇总信息
			kchz_map.put("ljid", ljid);
			kchz_map.put("kcsl", lsxxmap.get("sl"));
			kchz_map.put("kfid", kfid);
			kchz_map.put("kwid", kwid);
			kchz_map.put("update_time", new Date());
			kchz_map.put("update_user", Sys.getUserName());
			kchz_map.put("create_time", new Date());
			kchz_map.put("create_user", Sys.getUserName());
			
			//设置库存明细信息
			kcmx_map.put("ljid", ljid);
			kcmx_map.put("kfid", kfid);
			kcmx_map.put("kwid", kwid);
			kcmx_map.put("kczt", 10);
			kcmx_map.put("rksj", new Date());
			kcmx_map.put("rkry", Sys.getUserName());
			kcmx_map.put("rksl", lsxxmap.get("sl"));
			kcmx_map.put("xh", lsmap.get("add_xh"));
			kcmx_map.put("scph", lsmap.get("add_scph"));
			kcmx_map.put("mplh", lsmap.get("add_mplh"));
			kcmx_map.put("gdid", lsmap.get("add_gdid"));
			kcmx_map.put("pcid", lsmap.get("add_scrwpcid"));
			
			//设置库存流水信息
			kcls_map.put("kfid", kfid);
			kcls_map.put("kwid", kwid);
			kcls_map.put("czlx", 10);
			kcls_map.put("sl", lsxxmap.get("sl"));
			kcls_map.put("czry", Sys.getUserName());
			kcls_map.put("czsj", new Date());
			kcls_map.put("xh", lsmap.get("add_xh"));
			kcls_map.put("scph", lsmap.get("add_scph"));
			kcls_map.put("mplh", lsmap.get("add_mplh"));
			kcls_map.put("kcsl", lsxxmap.get("sl"));
			
			//新增库存汇总,库存明细,库存流水信息
			if (kcgl.size() <= 0) {	
				
				int n = Sys.insert("lj_kcb", kchz_map);
				
				kcmx_map.put("kcid", kchz_map.get("kcid"));
				
				n = Sys.insert("lj_kcmx", kcmx_map);
				
				kcls_map.put("kcmxid", kcmx_map.get("kcmxid"));
				
				n = Sys.insert("lj_kcls", kcls_map);
			} 
			//更新库存汇总,库存明细,库存流水信息
			else {
				BigDecimal number = new BigDecimal(kcgl.get(0).get("kcsl") + "");
				BigDecimal addnumber = new BigDecimal(lsxxmap.get("sl") + "");
				String kcid = kcgl.get(0).get("kcid") + "";
				
				kchz_map.clear();
				kchz_map.put("kcsl", number.add(addnumber));
				
				int n = Sys.update("lj_kcb", kchz_map, "kcid = ? ", new Object[]{kcid});
				
				kcmx_map.put("kcid", kcid);
				
				n = Sys.insert("lj_kcmx", kcmx_map);
				
				kcls_map.put("kcmxid", kcmx_map.get("kcmxid"));
				kcls_map.put("kcsl", number.add(addnumber));
				n = Sys.insert("lj_kcls", kcls_map);
			}
			
			/*
			 * 更新工单状态，由“已完成”更新为“已入库”
			 */
			Parameters parts = new Parameters();
			parts.set("gdid", gdid);
			parts.set("gdzt", "55");
			Sys.callModuleService("pl", "plservice_updategdxxByGdID", parts);
			
			/*
			 * 更新生产任务信息：
			 * 1. 更新生产任务批次状态：已完成（80） -> 已入库（85）
			 * 2. 更新如果当前任务所有批次有已经完成，并且完成数量> 生产任务数量，则将生产任务更新为已完成
			*/
			parts = new Parameters();
			parts.set("pcid", pcid);
			Sys.callModuleService("pro", "proService_updateScrwpcztAndScrwzt", parts);
			
			// update by duanpeng for send activity info 20160823
			// send activity
			//查询物料入库流水
			Dataset rkxx = Sys.query(new String[]{"lj_kcls","wm_kwxxb","wm_kfxxb"},					
					"lj_kcls join wm_kfxxb on lj_kcls.kfid = wm_kfxxb.kfid "
					+ " join wm_kwxxb on lj_kcls.kwid = wm_kwxxb.kwid ",					
					"lj_kcls.sl,wm_kwxxb.kwmc,wm_kfxxb.kfmc,"
					+ "wm_kfxxb.zzjgid", "lj_kcls.kclsid = ?" ,
					null, null,new Object[]{kcls_map.get("kclsid")});
			List<Map<String, Object>> rkxxList = rkxx.getList();
			//查询物料名称
			Parameters ljxxCon = new Parameters();
			ljxxCon.set("val_lj", "("+ljid+")");
			Bundle result = Sys.callModuleService("pm", "pmservice_ljxx", ljxxCon);
			List<Map<String,Object>> ljxx = (List<Map<String, Object>>) result.get("ljxx");
			parameters.set("ljid", ljid);
			Bundle resultLjUrl = Sys.callModuleService("pm", "partsInfoService", parameters);
			Object ljtp = ((Map)resultLjUrl.get("partsInfo")).get("url");
			//查询批次名称
			Parameters pcCon = new Parameters();
			pcCon.set("val_pc", "("+pcid+")");
			List<Map<String, Object>> pcxx = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> pcrwbhxx = new ArrayList<Map<String,Object>>();
			if(pcid !=0){
				Bundle resultPcxx = Sys.callModuleService("pro", "proService_pcxxbyid", pcCon);
				pcxx = ((List<Map<String, Object>>)resultPcxx.get("pcxx"));
				pcCon.set("pcid", "("+pcid+")");
				Bundle resultScrw = Sys.callModuleService("pro", "proServicePcbhByPcidService", pcCon);
				pcrwbhxx = ((List<Map<String, Object>>)resultScrw.get("scrwpc"));
			}
			//查询组织机构名称
			Parameters orgCon = new Parameters();
			orgCon.set("id", rkxxList.get(0).get("zzjgid"));
			Bundle resultOrg = Sys.callModuleService("org", "nameService", orgCon);
			String activityType = "3"; //动态类型
			String[] roles = new String[] { "STOCKMAN_ROLE","MANUFACTURING_MANAGEMENT_ROLE" };//关注该动态的角色
			String templateId = "ljrk_tp";
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("ljbh", ljxx.get(0).get("ljbh"));//零件编号
			data.put("ljmc", ljxx.get(0).get("ljmc"));//零件名称
			data.put("sl", rkxxList.get(0).get("sl"));//入库数量
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
			data.put("rksj", czsj.getTime());//入库时间
			data.put("ljtp", ljtp);//零件图片
			String dw = ljxx.get(0).get("dw") != null? ljxx.get(0).get("dw").toString() : "";
			String[] dwmc = null;
			if(dw!=null){
				dwmc = Sys.getDictFieldNames("JLDW", dw);
			}
			data.put("dw", dwmc[0]);//零件单单位
			sendActivity(activityType, templateId, true, roles, null, null, data);
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
	
	public String getDate(){
		
		String str = "";
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=new Date();
		str = dateFormater.format(date);
		return str;
	} 
	

}
