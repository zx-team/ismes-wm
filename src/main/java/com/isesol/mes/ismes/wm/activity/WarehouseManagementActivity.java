package com.isesol.mes.ismes.wm.activity;

import java.io.InputStream;
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
import com.isesol.ismes.platform.module.bean.File;

import net.sf.json.JSONArray;

/**
 * 物料库存管理 activity
 *
 */
public class WarehouseManagementActivity {

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
	 * 跳转库存管理界面
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_kcgl(Parameters parameters, Bundle bundle) {
		return "wm_kcgl";
	}
	/**
	 * 跳转入库界面
	 * @param parameters
	 * @param bundle
	 * @return
	 */
//	public String query_rkxx(Parameters parameters, Bundle bundle) {
//		return "wm_rkxx";
//		
//	}
	/**
	 * 跳转出库界面
	 * @param parameters
	 * @param bundle
	 * @return
	 */
//	public String query_ckxx(Parameters parameters, Bundle bundle) {
//		return "wm_ckxx";
//	}
	
	/**入库-update_rkxx
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception 
	 */
//	@SuppressWarnings("unchecked")
//	public String update_rkxx(Parameters parameters, Bundle bundle) throws Exception {
//		JSONArray jsonarray = JSONArray.fromObject(parameters.get("data_list"));  
//		for (int i = 0; i < jsonarray.size(); i++) {
//			Map<String, Object> lsmap = new HashMap<String, Object>(); 
//			Map<String, Object> lsxxmap = new HashMap<String, Object>(); 
//			Map<String, Object> kcmap = new HashMap<String, Object>(); 
//			lsmap = jsonarray.getJSONObject(i);
//			int rksl = Math.abs(Integer.parseInt(lsmap.get("kcsl").toString()));
//			Date czsj = new Date();
//			lsxxmap.put("ry_dm", Sys.getUserIdentifier());
//			lsxxmap.put("sl", rksl);
//			lsxxmap.put("czsj", czsj);
//			lsxxmap.put("wlid", lsmap.get("wlid"));
//			lsxxmap.put("pcid", lsmap.get("pcid"));
//			lsxxmap.put("kfid", lsmap.get("kfid"));
//			lsxxmap.put("kwid", lsmap.get("kwid"));
//			lsxxmap.put("zzjgid", lsmap.get("zzjgid"));
//			lsxxmap.put("bz", lsmap.get("bz"));
//			lsxxmap.put("kclslx_dm", lsmap.get("kclslx_dm"));
//			
//			String wlid = lsmap.get("wlid").toString();
//			String pcid = lsmap.get("pcid").toString();
//			String kfid = lsmap.get("kfid").toString();
//			String kwid = lsmap.get("kwid").toString();
////			int sl = lsmap.get("kwid").toString());
//			int kcsl = Integer.parseInt(lsmap.get("kcsl").toString());
//			lsmap.put("czsj",czsj);
//			Dataset datasetkcgl = Sys.query("kc_sjwlkc","sjwlkcid,kcsl", "wlid = ? and pcid = ? and kfid = ? and kwid = ? ", null, new Object[]{wlid, pcid, kfid, kwid});
//			List<Map<String, Object>> kcgl = datasetkcgl.getList();
//			if(kcgl.size()<=0)
//			{
//				kcmap.put("wlid", lsmap.get("wlid"));
//				kcmap.put("pcid", lsmap.get("pcid"));
//				kcmap.put("kfid", lsmap.get("kfid"));
//				kcmap.put("kwid", lsmap.get("kwid"));
//				kcmap.put("kcsl", lsmap.get("kcsl"));
//				kcmap.put("czsj", new Date());
//				int n = Sys.insert("kc_sjwlkc", kcmap);
//				if(n==1)
//				{
//					lsxxmap.put("sjkcid", kcmap.get("sjwlkcid"));
//				}
//				n = Sys.insert("wm_wlls", lsxxmap);
//			}else{
//				kcmap.put("kcsl", Float.parseFloat(kcgl.get(0).get("kcsl").toString())+Float.parseFloat(lsxxmap.get("sl").toString()));
//				kcmap.put("czsj", czsj);
//				int n = Sys.update("kc_sjwlkc", kcmap, "sjwlkcid = ?", new Object[]{kcgl.get(0).get("sjwlkcid")});
//				if(n==1)
//				{
//					lsxxmap.put("sjkcid", kcgl.get(0).get("sjwlkcid"));
//				}
//				n = Sys.insert("wm_wlls", lsxxmap);
//			}
//		}  
//		return null;
//	}
	
	/**入库 -save_rkxx
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception 
	 */
	public String save_rkxx(Parameters parameters, Bundle bundle) throws Exception {
		Map<String, Object> lsmap = parameters.getMap();
		if (null != lsmap) {
			Map<String, Object> lsxxmap = new HashMap<String, Object>(); 
			Map<String, Object> kcmap = new HashMap<String, Object>();
			Map<String, Object> kcmxmap = new HashMap<String, Object>();
			int rksl = Math.abs(Integer.parseInt(lsmap.get("add_kcsl").toString()));
			Date czsj = new Date();
			lsxxmap.put("ry_dm", Sys.getUserIdentifier());
			lsxxmap.put("sl", rksl);
			lsxxmap.put("czsj", czsj);
			lsxxmap.put("wlid", lsmap.get("add_wlid"));
			lsxxmap.put("pcid", lsmap.get("add_pcid"));
			lsxxmap.put("kfid", lsmap.get("add_kfid"));
			lsxxmap.put("kwid", lsmap.get("add_kwid"));
			lsxxmap.put("zzjgid", lsmap.get("add_zzjgid"));
			lsxxmap.put("bz", lsmap.get("add_bz"));
			lsxxmap.put("kclslbdm", this.INVENTORY_TYPE_IN);
			lsxxmap.put("yggh", Sys.getUserIdentifier());//员工号
			lsxxmap.put("rkdh", lsmap.get("add_rkdh"));//入库单号
			lsxxmap.put("mplh", lsmap.get("add_mplh"));//毛坯炉号
			lsxxmap.put("wlgg", lsmap.get("add_wlgg"));//物料规格
			lsxxmap.put("wltm", lsmap.get("add_wltm"));//物料条码
			lsxxmap.put("jh", lsmap.get("add_jh"));//件号
			lsxxmap.put("wllb", lsmap.get("add_wllb"));
			lsxxmap.put("kccx_enable", 1);
			
			String wlid = lsmap.get("add_wlid").toString();
			String pcid = lsmap.get("add_pcid").toString();
			String kfid = lsmap.get("add_kfid").toString();
			String kwid = lsmap.get("add_kwid").toString();
			String mplh = lsmap.get("add_mplh").toString();
			String jh = lsmap.get("add_jh").toString();
			
			kcmxmap.put("kfid", lsmap.get("add_kfid"));
			kcmxmap.put("kwid", lsmap.get("add_kwid"));
			kcmxmap.put("kczt", this.INVENTORY_TYPE_IN);
			kcmxmap.put("rksj", czsj);
			kcmxmap.put("rkry", Sys.getUserName());
			kcmxmap.put("rksl", rksl);
			kcmxmap.put("cksl", 0);
			kcmxmap.put("pcid", lsmap.get("add_pcid"));
			kcmxmap.put("wlid", lsmap.get("add_wlid"));
			kcmxmap.put("jh", lsmap.get("add_jh"));
			kcmxmap.put("mplh", lsmap.get("add_mplh"));
			kcmxmap.put("wllb", lsmap.get("add_wllb"));
			kcmxmap.put("wlgg", lsmap.get("add_wlgg"));
			
			lsmap.put("czsj",czsj);
			//查询物料实际库存表信息，区分 add update
			Dataset datasetkcgl = Sys.query("kc_sjwlkc","sjwlkcid,kcsl", "wlid = ? and pcid = ? and kfid = ? and kwid = ? and mplh=? ", null, new Object[]{wlid, pcid, kfid, kwid, mplh});
			List<Map<String, Object>> kcgl = datasetkcgl.getList();
			if(kcgl.size()<=0)
			{
				kcmap.put("wlid", lsxxmap.get("wlid"));
				kcmap.put("pcid", lsxxmap.get("pcid"));
				kcmap.put("kfid", lsxxmap.get("kfid"));
				kcmap.put("kwid", lsxxmap.get("kwid"));
				kcmap.put("kcsl", lsxxmap.get("sl"));
				kcmap.put("czsj", czsj);
				kcmap.put("mplh", lsxxmap.get("mplh"));//毛坯炉号
				kcmap.put("wlgg", lsxxmap.get("wlgg"));//物料规格
				kcmap.put("wltm", lsxxmap.get("wltm"));//物料条码
				kcmap.put("wllb", lsmap.get("add_wllb"));
				
				int n = Sys.insert("kc_sjwlkc", kcmap);
				if(n==1)
				{
					lsxxmap.put("sjkcid", kcmap.get("sjwlkcid"));
					kcmxmap.put("sjwlkcid", kcmap.get("sjwlkcid"));
				}
				n = Sys.insert("wm_wlls", lsxxmap);
				
				n = Sys.insert("kc_wlkcmx", kcmxmap);
			}else{
				kcmap.put("kcsl", Float.parseFloat(kcgl.get(0).get("kcsl").toString())+rksl);
				kcmap.put("czsj", czsj);
				int n = Sys.update("kc_sjwlkc", kcmap, "sjwlkcid = ?", new Object[]{kcgl.get(0).get("sjwlkcid")});
				if(n==1)
				{
					lsxxmap.put("sjkcid", kcgl.get(0).get("sjwlkcid"));
					
				}
				kcmxmap.put("sjwlkcid", kcgl.get(0).get("sjwlkcid"));
				n = Sys.insert("kc_wlkcmx", kcmxmap);
				
				n = Sys.insert("wm_wlls", lsxxmap);
			}
			
			// update by duanpeng for send activity info 20160823
			// send activity
			//查询物料入库流水
			Dataset rkxx = Sys.query(new String[]{"wm_wlls","wm_kwxxb","wm_kfxxb"}, "wm_wlls join wm_kfxxb on wm_wlls.kfid = wm_kfxxb.kfid "
					+ " join wm_kwxxb on wm_wlls.kwid = wm_kwxxb.kwid ","wm_wlls.wlid,wm_wlls.sl,wm_kwxxb.kwmc,wm_kfxxb.kfmc,"
							+ "wm_wlls.pcid,wm_wlls.zzjgid,wm_wlls.rkdh","wm_wlls.kclsid = ?" ,
					null, null,new Object[]{lsxxmap.get("kclsid")});
			List<Map<String, Object>> rkxxList = rkxx.getList();
			//查询物料名称
			Parameters wlmcCon = new Parameters();
			wlmcCon.set("wlid", rkxxList.get(0).get("wlid"));
			Bundle b_wlxx = Sys.callModuleService("mm", "mmservice_query_wlxxByWlid", wlmcCon);
			List<Map<String, Object>> wlxx = (List<Map<String, Object>>) b_wlxx.get("wlxx");
			//查询组织机构名称
			Parameters orgCon = new Parameters();
			orgCon.set("id", rkxxList.get(0).get("zzjgid"));
			Bundle resultOrg = Sys.callModuleService("org", "nameService", orgCon);
			String activityType = "3"; //动态类型
			String[] roles = new String[] { "STOCKMAN_ROLE","MANUFACTURING_MANAGEMENT_ROLE" };//关注该动态的角色
			String templateId = "wlrk_tp";
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("wlid", wlxx.get(0).get("wlbh"));//物料编号
			data.put("wlmc", wlxx.get(0).get("wlmc"));//物料名称
			data.put("sl", rkxxList.get(0).get("sl"));//入库数量
			data.put("kwmc", rkxxList.get(0).get("kwmc"));//库位
			data.put("kfmc", rkxxList.get(0).get("kfmc"));//库房
			data.put("pcid", rkxxList.get(0).get("pcid"));//批次 
			data.put("zzjgid", rkxxList.get(0).get("zzjgid"));//部门ID
			data.put("zzjgmc", resultOrg.get("name"));//部门 名称
			data.put("userid", Sys.getUserIdentifier());//操作人
			data.put("username", Sys.getUserName());//操作人
			data.put("rksj", czsj.getTime());//入库时间
			String dw = wlxx.get(0).get("wldwdm") != null? wlxx.get(0).get("wldwdm").toString() : "";
			String[] dwmc = null;
			if(dw!=null){
				dwmc = Sys.getDictFieldNames("JLDW", dw);
			}
			data.put("dw", dwmc[0]);//物料单位
			sendActivity(activityType, templateId, true, roles, null, null, data);
		}  
		return null;
	}
	private Bundle sendMessage(String type, String title, String abs, String content, String from, String bizType,
			String bizId, String priority, String url, String[] roles, String[] users, String[] group, String sourceType,
			String sourceId, Map<String, Object> data, Date sendTime, String[] fileUri, String[] fileNames,
			long[] filesSize) {
		String PARAMS_TYPE = "message_type";
		String PARAMS_ROLE = "receiver_role";
		String PARAMS_USER = "receiver_user";
		String PARAMS_GROUP = "receiver_group";
		String PARAMS_TITLE = "title";
		String PARAMS_ABSTRACT = "abstract";
		String PARAMS_CONTENT = "content";
		String PARAMS_FROM = "from";
		String PARAMS_DATA = "data";
		String PARAMS_PRIORITY = "priority";
		String PARAMS_SOURCE_TYPE = "source_type";
		String PARAMS_SOURCE_ID = "source_id";
		String PARAMS_URL = "url";
		String PARAMS_FILE_URI = "file_uri";
		String PARAMS_FILE_NAME = "file_name";
		String PARAMS_FILE_SIZE = "file_size";
		String PARAMS_BIZTYPE = "biz_type";
		String PARAMS_BIZID = "biz_id";
		String PARAMS_SEND_TIME = "send_time";
		String SERVICE_NAME = "message";
		String METHOD_NAME = "send";
		Parameters parameters = new Parameters();
		parameters.set(PARAMS_TITLE, title);
		parameters.set(PARAMS_ABSTRACT, abs);
		parameters.set(PARAMS_CONTENT, content);
		parameters.set(PARAMS_FROM, from);
		parameters.set(PARAMS_BIZTYPE, bizType);
		parameters.set(PARAMS_BIZID, bizId);
		parameters.set(PARAMS_TYPE, type);
		parameters.set(PARAMS_PRIORITY, priority);
		parameters.set(PARAMS_USER, users);
		parameters.set(PARAMS_GROUP, group);
		parameters.set(PARAMS_ROLE, roles);
		parameters.set(PARAMS_SOURCE_TYPE, sourceType);
		parameters.set(PARAMS_SOURCE_ID, sourceId);
		parameters.set(PARAMS_URL, url);
		parameters.set(PARAMS_FILE_URI, fileUri);
		parameters.set(PARAMS_FILE_NAME, fileNames);
		parameters.set(PARAMS_FILE_SIZE, filesSize);
		parameters.set(PARAMS_SEND_TIME, sendTime);
		parameters.set(PARAMS_DATA, data);
		return Sys.callModuleService(SERVICE_NAME, METHOD_NAME, parameters);
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
	
	/**出库-update_ckxx
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
			Map<String, Object> lsxxmap = new HashMap<String, Object>(); 
			Map<String, Object> kcmap = new HashMap<String, Object>(); 
			Map<String, Object> kcmxmap = new HashMap<String, Object>();
			lsmap = jsonarray.getJSONObject(i);
			
			Dataset kcxxset = Sys.query("kc_sjwlkc", "sjwlkcid,kcsl", "sjwlkcid = ?", null, new Object[]{lsmap.get("sjwlkcid")+""});
			
			Map<String, Object> kcxxmap = kcxxset.getMap();
			
			BigDecimal cksl = new BigDecimal(lsmap.get("ckwlsl") + "");//出库数量
			BigDecimal kcsl = new BigDecimal(kcxxmap.get("kcsl") + "");//库存数量
			kcmap.put("kcsl", kcsl.subtract(cksl));
			lsxxmap.put("sl", cksl);//库存流水，2016/8/27 修改为正数，根据库存流水类别判断出入库
			lsxxmap.put("czsj", new Date());
			lsxxmap.put("wlid", lsmap.get("wlid").toString());
			lsxxmap.put("pcid", lsmap.get("pcid").toString());
			lsxxmap.put("kfid", lsmap.get("kfid").toString());
			lsxxmap.put("kwid", lsmap.get("kwid").toString());
			lsxxmap.put("zzjgid", lsmap.get("zzjgid").toString());
			lsxxmap.put("kclslbdm", this.INVENTORY_TYPE_OUT);
			lsxxmap.put("sjkcid", lsmap.get("sjwlkcid"));
			lsxxmap.put("mplh", lsmap.get("mplh"));
			lsxxmap.put("jh", lsmap.get("jh"));
			lsxxmap.put("kccx_enable", 0);
			lsxxmap.put("yggh", Sys.getUserIdentifier());//员工号
			//lsxxmap.put("bz", lsmap.get("bz").toString());
			int n = Sys.update("kc_sjwlkc", kcmap, "sjwlkcid = ?", new Object[]{lsmap.get("sjwlkcid")});
			LOGGER.info("物料出库：物料库存更新成功："+n);
			Map<String, Object> kcls = new HashMap<String, Object>();
			kcls.put("kccx_enable", 0);
			n = Sys.update("wm_wlls", kcls, "kclsid = '"+lsmap.get("kclsid")+""+"'", new Object[]{});
			
			String pcid = lsmap.get("pcid").toString();
			String mplh = lsmap.get("mplh").toString();
			String jh = lsmap.get("jh").toString();
			String wlgg = lsmap.get("wlgg").toString();
			String wllb = lsmap.get("wllb").toString();
			Dataset kcmxset = Sys.query("kc_wlkcmx", "wlkcmxid", "wlgg=? and jh =? and mplh=? and wllb=?", null, new Object[]{wlgg,jh,mplh,wllb});
			String kcmxid = kcmxset.getMap().get("wlkcmxid")+"";
			
			kcmxmap.put("cksl", cksl);
			kcmxmap.put("cksj", new Date());
			kcmxmap.put("ckry", Sys.getUserName());
			kcmxmap.put("kczt", this.INVENTORY_TYPE_OUT);
			
			n = Sys.update("kc_wlkcmx", kcmxmap, "wlkcmxid=?", new Object[]{kcmxid});
			
			n = Sys.insert("wm_wlls", lsxxmap);
			
			// update by duanpeng for send activity info 20160823
			// send activity
			//查询物料出库流水
			Dataset rkxx = Sys.query(new String[]{"wm_wlls","wm_kwxxb","wm_kfxxb"}, "wm_wlls join wm_kfxxb on wm_wlls.kfid = wm_kfxxb.kfid "
					+ " join wm_kwxxb on wm_wlls.kwid = wm_kwxxb.kwid ","wm_wlls.wlid,wm_wlls.sl,wm_kwxxb.kwmc,wm_kfxxb.kfmc,wm_wlls.pcid,wm_wlls.zzjgid", "wm_wlls.kclsid = ?" ,
					null, null,new Object[]{lsxxmap.get("kclsid")});
			List<Map<String, Object>> rkxxList = rkxx.getList();
			//查询物料名称
			Parameters wlmcCon = new Parameters();
			wlmcCon.set("wlid", rkxxList.get(0).get("wlid"));
			Bundle b_wlxx = Sys.callModuleService("mm", "mmservice_query_wlxxByWlid", wlmcCon);
			List<Map<String, Object>> wlxx = (List<Map<String, Object>>) b_wlxx.get("wlxx");
			//查询组织机构名称
			Parameters orgCon = new Parameters();
			orgCon.set("id", rkxxList.get(0).get("zzjgid"));
			Bundle resultOrg = Sys.callModuleService("org", "nameService", orgCon);
			
			String activityType = "3"; //动态类型
			String[] roles = new String[] { "STOCKMAN_ROLE","MANUFACTURING_MANAGEMENT_ROLE" };//关注该动态的角色
			String templateId = "wlck_tp";
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("wlid", rkxxList.get(0).get("wlid"));//物料编号
			data.put("wlmc", wlxx.get(0).get("wlmc"));//物料名称
			data.put("sl", rkxxList.get(0).get("sl"));//入库数量
			data.put("kwmc", rkxxList.get(0).get("kwmc"));//库位
			data.put("kfmc", rkxxList.get(0).get("kfmc"));//库房
			data.put("pcid", rkxxList.get(0).get("pcid"));//批次 
			data.put("zzjgid", rkxxList.get(0).get("zzjgid"));//部门ID
			data.put("zzjgmc", resultOrg.get("name"));//部门 名称
			data.put("userid", Sys.getUserIdentifier());//操作人
			data.put("username", Sys.getUserName());//操作人
			data.put("cksj", ((Date)lsxxmap.get("czsj")).getTime());//出库时间
			String dw = wlxx.get(0).get("wldwdm") != null? wlxx.get(0).get("wldwdm").toString() : "";
			String[] dwmc = null;
			if(dw!=null){
				dwmc = Sys.getDictFieldNames("JLDW", dw);
			}
			data.put("dw", dwmc[0]);//物料单位
			sendActivity(activityType, templateId, true, roles, null, null, data);
		}  
		return null;
	}
	
	/**查询库存信息
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public String table_kcgl(Parameters parameters, Bundle bundle) throws Exception {
		
		//查询物料信息
		Bundle b_wlxx = Sys.callModuleService("mm", "mmservice_wlxxkc", parameters);
		List<Map<String, Object>> wlxx = (List<Map<String, Object>>) b_wlxx.get("wlxx");
		if (wlxx.size()<=0) {
			return "json:";
		}
		String val_wl = "(";
		for (int i = 0; i < wlxx.size(); i++) {
			if(!wlxx.get(i).get("wllbdm").equals("40")
					&& !wlxx.get(i).get("wllbdm").equals("50")){
				continue;
			}
			val_wl += "'"+wlxx.get(i).get("wlid")+"',";
		} 
		if(val_wl.endsWith(",")){
			val_wl = val_wl.substring(0, val_wl.length()-1);
		}
		val_wl = val_wl +")";
		
		//查询库存信息
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());
		
		Dataset datasetkcgl = Sys.query("kc_sjwlkc","max(wlid) wlid,sum(kcsl) kcsl,mplh,wlgg", " kcsl > '0' and wlid in " +val_wl ,"wlgg,mplh", "mplh", (page-1)*pageSize, pageSize,new Object[]{});
		
		List<Map<String, Object>> kcgl = datasetkcgl.getList();
		if (kcgl.size()<=0) {
			return "json:";
		}
		val_wl = "(";
		for (int i = kcgl.size()-1; i >= 0 ; i--) {
			for (int j = 0; j < wlxx.size(); j++) {
				if (kcgl.get(i).get("wlid").toString().equals(wlxx.get(j).get("wlid").toString())) {
					kcgl.get(i).put("wlbh", wlxx.get(j).get("wlbh"));
					kcgl.get(i).put("wlmc", wlxx.get(j).get("wlmc"));
					kcgl.get(i).put("wldwdm", wlxx.get(j).get("wldwdm"));
					kcgl.get(i).put("wllxdm", wlxx.get(j).get("wllxdm"));
					kcgl.get(i).put("wllbdm", wlxx.get(j).get("wllbdm"));
					kcgl.get(i).put("wlzt", wlxx.get(j).get("wlzt"));
					kcgl.get(i).put("wlcb", wlxx.get(j).get("wlcb"));
					kcgl.get(i).put("wltm", wlxx.get(j).get("wltm"));
					kcgl.get(i).put("wlgg", wlxx.get(j).get("wlgg"));
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
		String wlid = parameters.getString("wlid");
		if(StringUtils.isBlank(wlid))
		{
			return "json:";
		}
		String wlbh = parameters.getString("wlbh");
		String wlmc = parameters.getString("wlmc");
		
		String mplh = parameters.getString("mplh");
		String wlgg = parameters.getString("wlgg");
		String wllb = parameters.getString("wllb");
		String pcid = parameters.getString("pcid");
		
		String val_wl = "('" + wlid + "')";
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());
		Dataset datasetkcxx = Sys.query(new String[] {"wm_wlls","wm_kfxxb","wm_kwxxb"}, 
				" wm_wlls left join wm_kfxxb on wm_wlls.kfid=wm_kfxxb.kfid left join wm_kwxxb on wm_wlls.kwid=wm_kwxxb.kwid ",
				" sjkcid,kclsid,wlid,pcid,wm_wlls.kfid,wm_wlls.kwid,sl,czsj, wm_kfxxb.kfmc, wm_kwxxb.kwmc,jh,mplh,wlgg,wllb", 
				" kccx_enable = '1' and wlgg = '"+wlgg+"' and mplh = '"+mplh+
				"' and wlid = '" +wlid +"' and wllb = '" + wllb + "'",
				"mplh", (page - 1) * pageSize, pageSize, new Object[] {});
		
		List<Map<String, Object>> kcxx = datasetkcxx.getList();
		if (kcxx.size() <= 0) {
			return "json:";
		}
		for (int i = 0; i < kcxx.size(); i++) {
			kcxx.get(i).put("wlbh", wlbh);
			kcxx.get(i).put("wlmc", wlmc);
			kcxx.get(i).put("ckwlsl", kcxx.get(i).get("sl"));
			kcxx.get(i).put("zzjgid", "");//设置默认值，解决页面显示undefined 问题。
		}
		bundle.put("rows", kcxx);
		int totalPage = datasetkcxx.getTotal()%pageSize==0?datasetkcxx.getTotal()/pageSize:datasetkcxx.getTotal()/pageSize+1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", datasetkcxx.getTotal());
		return "json:";
	}

	/**根据物料规格查询物料信息
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String wlxxSelect(Parameters parameters,Bundle bundle){
		parameters.set("query_wlgg", parameters.get("query"));
		//查询物料信息
		Bundle b_wlxx = Sys.callModuleService("mm", "mmservice_wlxxkc", parameters);
		List<Map<String, Object>> wlxx = (List<Map<String, Object>>) b_wlxx.get("wlxx");
		if (wlxx.size()<=0) {
			return "json:";
		}
		
		//删除半成品物料。add by yangfan 2016/8/31；update by guofeng 2016/10/18
		Iterator<Map <String,Object>> iterator = wlxx.iterator();
		int i=0;
		while(iterator.hasNext()){
			Map<String,Object> map = iterator.next();
			if (null != map && !("40".equals(map.get("wllbdm").toString())
					|| "50".equals(map.get("wllbdm").toString()))){
				iterator.remove();
			}else{
				wlxx.get(i).put("dwmc",Sys.getDictFieldNames("JLDW", wlxx.get(i).get("wldwdm").toString()));
				wlxx.get(i).put("wllb",wlxx.get(i).get("wllbdm").toString());
				wlxx.get(i).put("wllbdm",Sys.getDictFieldNames("WLLB", wlxx.get(i).get("wllbdm").toString()));
				wlxx.get(i).put("value", wlxx.get(i).get("wlgg"));
				wlxx.get(i).put("label", wlxx.get(i).get("wlgg"));
				wlxx.get(i).put("title", wlxx.get(i).get("wlgg"));
				wlxx.get(i).put("wlid", wlxx.get(i).get("wlid"));
				wlxx.get(i).put("wlbh", wlxx.get(i).get("wlbh"));
				i++;
			}
		}
		bundle.put("wlxx", wlxx);
		return "json:wlxx";
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
	
	public String getSingleJh(Parameters parameters,Bundle bundle){
		
		String jh = parameters.getString("jh");
		
		Dataset dataset = Sys.query("kc_wlkcmx", "wlkcmxid,wlid", "(wllb = '40' or wllb = '50') and jh = '" + jh +"'", null, new Object[]{});
		
		List<Map<String, Object>> list = dataset.getList();
		
		bundle.put("data", list);
		return "json:";
	}
	
	public String uploadfile(Parameters parameters,Bundle bundle) throws ParseException{
		File file = parameters.getFile("importFile");
		
		String fileName = file.getName();
		
		if(!fileName.contains(".")){
			return "json:";
		}
		
		String fileType = fileName.split("\\.")[1];
		
		String import_kfid = parameters.getString("import_kfid");
		String import_kwid = parameters.getString("import_kwid");
		String wllb = parameters.getString("wllb");
		
		String templeteName = "";
		String wllbdm = "";
		if("1".equals(wllb)){
			wllbdm = "40";
			templeteName = "mptemplete";
		} else {
			wllbdm="50";
			templeteName = "ycltemplete";
		}
		
		InputStream is = file.getInputStream();
		
		InputStream modal_is = this.getClass().getResourceAsStream("/" + templeteName + "." + fileType);
		
		Map<String, String> settings = new HashMap<String, String>();
		settings.put("templateType", "multi");
		List<Map<String, Object>> list = Sys.importFile("xls", is, modal_is, settings);

		//记录为导入的数据,返回前台
		List<Map<String, Object>> remove_list = new ArrayList<Map<String, Object>>();
		StringBuffer jhs = new StringBuffer();
		
		jhs.append("(");
		//删除无效数据
		for(int i = list.size() - 1; i >= 0; i--){
			if(list.get(i).get("wlgg") == null) {
				list.remove(i);
			} else {
				jhs.append("'" + list.get(i).get("jh") + "',");
			}
		}
		if(list.size() == 0){
			bundle.put("data", remove_list);
			return "json:";
		}
		//去重件号重复的数据
		String jhs_str = jhs.substring(0, jhs.toString().length() - 1);
		jhs_str += ")";
		Dataset jhxx_set = Sys.query("kc_wlkcmx", "wlkcmxid,jh", 
				"(wllb = '40' or wllb = '50') and jh in " + jhs_str, null, new Object[]{});
		
		List<Map<String, Object>> jh_list = jhxx_set.getList();
		
		for(int i = list.size()-1; i >= 0; i--){
			for(int j = 0, len = jh_list.size(); j < len; j++){
				if((list.get(i).get("jh")+"").equals(jh_list.get(j).get("jh")+"")){
					list.get(i).put("reason", "物料件号重复");
					remove_list.add(list.get(i));
					list.remove(i);
					break;
				}
			}
		}
		if(list.size() == 0){
			bundle.put("data", remove_list);
			return "json:";
		}
		
		//获取物料规格,通过物料规格查询物料id
		StringBuffer wlggs = new StringBuffer();
		wlggs.append("(");
		for(int i = 0, len = list.size(); i < len; i++){
			wlggs.append("'" + list.get(i).get("wlgg") + "',");
		}
		String wlggs_str = wlggs.substring(0, wlggs.toString().length()-1);
		wlggs_str += ")";
		
		parameters.set("wlggs", wlggs_str);
		parameters.set("wllb", wllbdm);
		Bundle wlxx_bundle = Sys.callModuleService("mm", "mmservice_query_wlxx_bywlgg", parameters);
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> wlxx_list = (List<Map<String, Object>>)wlxx_bundle.get("wlxxs");
		
		//整合物料ID
		for(int i = 0, len = list.size(); i < len; i++){
			for(int j = 0, _len = wlxx_list.size(); j < _len; j++){
				if(list.get(i).get("wlgg").equals(wlxx_list.get(j).get("wlgg"))){
					list.get(i).put("wlid", wlxx_list.get(j).get("wlid"));
					list.get(i).put("wltm", wlxx_list.get(j).get("wltm"));
					list.get(i).put("wldw", wlxx_list.get(j).get("wldwdm"));
					BigDecimal sl_bd = new BigDecimal(list.get(i).get("sl") + "");					
					String wldw = wlxx_list.get(j).get("wldwdm") + "";
					if("30".equals(wldw)){
						list.get(i).put("sl", sl_bd.divide(new BigDecimal("1000"))
								.setScale(2, BigDecimal.ROUND_HALF_UP));
					}
					break;
				}
			}
		}
		
		StringBuffer wlcon = new StringBuffer();
		wlcon.append(" 1 = 1 and (");
		//拼接查询实际物料库存表条件
		for(int i = list.size() - 1; i >= 0; i--){
			//数据库中没有导入数据物料规格的物料,删除这条数据
			if(null == list.get(i).get("wlid")){
				list.get(i).put("reason", "系统未录入该种物料");
				remove_list.add(list.get(i));
				list.remove(i);
				continue;
			}
			wlcon.append("(");
			wlcon.append(" wlid = '" + list.get(i).get("wlid") + "'");
			wlcon.append(" and pcid = '" + list.get(i).get("pcid") + "'");
			wlcon.append(" and kfid = '" + import_kfid + "'");
			wlcon.append(" and kwid = '" + import_kwid + "'");
			wlcon.append(" and mplh = '" + list.get(i).get("mplh") + "'");
			wlcon.append(")");
			wlcon.append(" or ");
		}
		String wlsr = wlcon.substring(0, wlcon.length() - 3);
		wlsr += ")";
		if(list.size() == 0){
			bundle.put("data", remove_list);
			return "json:";
		}
		//获取实际库存表是否有相同数据
		Dataset datasetkcgl = Sys.query("kc_sjwlkc","sjwlkcid,wlid,pcid,kfid,kwid,mplh,kcsl", wlsr, null, new Object[]{});
		List<Map<String, Object>> kcxx_list = datasetkcgl.getList();
		
		//获取相同数据的角标
		List<Integer> index = new ArrayList<Integer>();	
		for(int i = 0, len = list.size(); i < len; i++){
			for(int j = 0, _len = kcxx_list.size(); j < _len; j++){
				if(list.get(i).get("pcid").equals(kcxx_list.get(j).get("pcid"))
						&& list.get(i).get("wlid").equals(kcxx_list.get(j).get("wlid"))
						&& list.get(i).get("mplh").equals(kcxx_list.get(j).get("mplh"))){
					list.get(i).put("sjwlkcid", kcxx_list.get(j).get("sjwlkcid"));
					list.get(i).put("kcsl", kcxx_list.get(j).get("kcsl"));
					index.add(i);
					break;
				}
			}
		}
		
		//新增实际物料库存表集合
		List<Map<String, Object>> add_list = new ArrayList<Map<String, Object>>();
		//更新实际物料库存表集合
		List<Map<String, Object>> update_list = new ArrayList<Map<String ,Object>>();
		
		//获取需要更新数据,并在原数据集合删除,保留需要添加的数据
		for(int i = index.size()-1; i >= 0 ; i--){
			update_list.add(list.get(index.get(i)));
			int num = index.get(i);
			list.remove(num);
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		/*************新增BEGIN********************************/
		//实际物料库存数据  list和add_list数据是统一的
		for(int i = 0, len = list.size(); i < len; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> native_map = list.get(i);
			map.put("wlid", native_map.get("wlid"));
			map.put("pcid", native_map.get("pcid"));
			map.put("kfid", import_kfid);
			map.put("kwid", import_kwid);
			map.put("kcsl", native_map.get("sl"));
			map.put("czsj", new Date());//native_map.get("rksj")
			map.put("mplh", native_map.get("mplh"));//毛坯炉号
			map.put("wlgg", native_map.get("wlgg"));//物料规格
			map.put("wltm", native_map.get("wltm"));//物料条码
			map.put("wllb", wllbdm);
			add_list.add(map);
		}
		
		@SuppressWarnings("unused")
		int n = Sys.insert("kc_sjwlkc", add_list);
		
		//物料库存明细数据
		for(int i = 0, len = add_list.size(); i < len; i++){
			Map<String, Object> native_map = add_list.get(i);
			native_map.put("kczt", this.INVENTORY_TYPE_IN);
			native_map.put("rksj", sdf.parse(list.get(i).get("rksj") + " 00:00:00"));//
			native_map.put("rkry", Sys.getUserName());
			native_map.put("rksl", add_list.get(i).get("kcsl"));
			native_map.put("cksl", 0);
			native_map.put("rkdh", list.get(i).get("rkdh"));
			native_map.put("jh",list.get(i).get("jh"));
		}
		
		n = Sys.insert("kc_wlkcmx", add_list);
		
		//物料流水数据
		for(int i = 0 ,len = add_list.size(); i < len; i++){
			Map<String, Object> native_map = add_list.get(i);
			native_map.put("ry_dm", Sys.getUserIdentifier());
			native_map.put("sl", list.get(i).get("sl"));
			native_map.put("czsj", sdf.parse(list.get(i).get("rksj") + " 00:00:00"));//list.get(i).get("rksj")
			native_map.put("sjkcid", add_list.get(i).get("sjwlkcid"));
			
//			native_map.put("zzjgid", list.get(i).get("add_zzjgid"));
			native_map.put("bz", list.get(i).get("bz"));
			native_map.put("kclslbdm", this.INVENTORY_TYPE_IN);
			native_map.put("yggh", Sys.getUserIdentifier());//员工号
			native_map.put("rkdh", list.get(i).get("rkdh"));//入库单号
			native_map.put("kccx_enable", 1);
		}
		n = Sys.insert("wm_wlls", add_list);
		/**************新增END*****************************/
		
		
		/**************更新BEGIN*********************************/
		List<Object[]> conVal = new ArrayList<Object[]>();
		List<Map<String, Object>> value_list = new ArrayList<Map<String, Object>>();
		for(int i = 0, len = update_list.size(); i < len; i++){
			Map<String, Object> native_map = update_list.get(i);
			Map<String, Object> value_map = new HashMap<String, Object>();
			String value = native_map.get("sjwlkcid").toString();
			Object[] obj = new Object[]{"'" + value + "'"};
			conVal.add(obj);
			
			BigDecimal kcsl = new BigDecimal(native_map.get("kcsl") + "");
			BigDecimal sl = new BigDecimal(native_map.get("sl") + "");
			value_map.put("kcsl", kcsl.add(sl));
			value_map.put("czsj", new Date());//native_map.get("rksj")
			value_list.add(value_map);
			n = Sys.update("kc_sjwlkc", value_map, "sjwlkcid = '" + value + "'", new Object[]{});
		}
		
//		if(update_list.size() > 0)
//			n = Sys.update("kc_sjwlkc", value_list, "sjwlkcid = ?", conVal);
		
		for(int i = 0, len = update_list.size(); i < len; i++){
			Map<String, Object> native_map = update_list.get(i);
			native_map.put("kczt", this.INVENTORY_TYPE_IN);
			native_map.put("rkry", Sys.getUserName());
			native_map.put("cksl", 0);
			native_map.put("kfid", import_kfid);
			native_map.put("kwid", import_kwid);
			native_map.put("rksj", sdf.parse(list.get(i).get("rksj") + " 00:00:00"));//native_map.get("rksj")
			native_map.put("rksl", native_map.get("sl"));
			native_map.put("wllb", wllbdm);
			native_map.put("rkdh", native_map.get("rkdh"));
			native_map.put("cksj", new Date());//native_map.get("cksj")
		}
		if(update_list.size() > 0)
		n = Sys.insert("kc_wlkcmx", update_list);
		
		//物料流水数据
		for(int i = 0 ,len = update_list.size(); i < len; i++){
			Map<String, Object> native_map = update_list.get(i);
			native_map.put("ry_dm", Sys.getUserIdentifier());
//			native_map.put("zzjgid", list.get(i).get("add_zzjgid"));
			native_map.put("kclslbdm", this.INVENTORY_TYPE_IN);
			native_map.put("yggh", Sys.getUserIdentifier());//员工号
			native_map.put("kccx_enable", 1);
			native_map.put("rkdh", native_map.get("rkdh"));
			native_map.put("sjkcid", native_map.get("sjwlkcid"));
			native_map.put("czsj", sdf.parse(list.get(i).get("rksj") + " 00:00:00"));//native_map.get("rksj")
		}
		if(update_list.size() > 0)
		n = Sys.insert("wm_wlls", update_list);
		
		/**************更新END**********************************/
		bundle.put("data", remove_list);
		return "json:";
	}
	
//	public Map<String, Object> oprateInfo(List<Map<String, Object>> list){
//		
//	}
	
}
