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
 * 刀具库存管理
 * @author Administrator
 *
 */
public class ToolStockManagementActivity {

	/*
	 * 库存流水类别：入库
	 */
	private final String INVENTORY_TYPE_IN ="10";
	/*
	 * 库存流水类别：出库
	 */
	private final String  INVENTORY_TYPE_OUT ="20";
	
	private SimpleDateFormat sdf_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Logger LOGGER = Logger.getLogger(this.getClass());
	
	/**
	 * 跳转刀具库存管理界面
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_djkcgl(Parameters parameters, Bundle bundle){
		return "wm_djkcgl";
	}
	
	/**
	 * 显示刀具库存管理信息
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String table_djkcgl(Parameters parameters, Bundle bundle) throws Exception {
		
		//刀具物料类别
		String query_wllbdm_ids = "\'10\', \'60\'";
		parameters.set("query_wllbdm_ids", query_wllbdm_ids);
		//查询刀具信息
		Bundle b_wlxx = Sys.callModuleService("mm", "mmservice_wlxxkc", parameters);
		List<Map<String, Object>> wlxx = (List<Map<String, Object>>) b_wlxx.get("wlxx");
		if (wlxx.size()<=0) {
			return "json:";
		}
		String val_wl = "(";
		for (int i = 0; i < wlxx.size(); i++) {
			val_wl += "'"+wlxx.get(i).get("wlid")+"',";
		} 
		if(val_wl.endsWith(",")){
			val_wl = val_wl.substring(0, val_wl.length()-1);
		}
		val_wl = val_wl +")";
		
		//查询库存信息
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());
		Dataset datasetkcgl = Sys.query("kc_sjwlkc","wlid,sum(kcsl) kcsl", " wlid in " +val_wl ,"wlid ", null, (page-1)*pageSize, pageSize,new Object[]{});
		List<Map<String, Object>> kcgl = datasetkcgl.getList();
		if (kcgl.size()<=0) {
			return "json:";
		}
		val_wl = "(";
		for (int i = 0; i < kcgl.size(); i++) {
			for (int j = 0; j < wlxx.size(); j++) {
				if (kcgl.get(i).get("wlid").toString().equals(wlxx.get(j).get("wlid").toString())) {
					kcgl.get(i).put("wlid", wlxx.get(j).get("wlid"));
					kcgl.get(i).put("wlbh", wlxx.get(j).get("wlbh"));
					kcgl.get(i).put("wllxdm", wlxx.get(j).get("wllxdm"));
					kcgl.get(i).put("wlmc", wlxx.get(j).get("wlmc"));
					kcgl.get(i).put("wlpp", wlxx.get(j).get("wlpp"));
					kcgl.get(i).put("wllbdm", wlxx.get(j).get("wllbdm"));
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
	
	/**
	 * 通过编号查询刀具信息
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String djbhSelect(Parameters parameters, Bundle bundle)throws Exception{
		parameters.set("query_wlbh", parameters.get("query"));
		//刀具物料类别
		String query_wllbdm_ids = "\'10\', \'60\'";
		parameters.set("query_wllbdm_ids", query_wllbdm_ids);
		//查询刀具信息
		Bundle b_gzxx = Sys.callModuleService("mm", "mmservice_wlxxkc", parameters);
		List<Map<String, Object>> djxx = (List<Map<String, Object>>) b_gzxx.get("wlxx");
		if (djxx.size()<=0) {
			return "json:";
		}
		
		for(int i = 0, len = djxx.size(); i < len; i++){
			djxx.get(i).put("dwmc",Sys.getDictFieldNames("JLDW", (String)djxx.get(i).get("wldwdm")));
			djxx.get(i).put("value", djxx.get(i).get("wlid"));
			djxx.get(i).put("label", djxx.get(i).get("wlbh"));
			djxx.get(i).put("title", djxx.get(i).get("wlbh"));
			djxx.get(i).put("wlgg", djxx.get(i).get("wlgg"));
		}
		bundle.put("djxx", djxx);
		return "json:djxx";
	}
	
	/**刀具库存详细页面
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
		
		String val_wl = "('" + wlid + "')";
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());
		Dataset datasetkcxx = Sys.query(new String[] {"wm_wlls","wm_kfxxb","wm_kwxxb"}, 
				" wm_wlls left join wm_kfxxb on wm_wlls.kfid=wm_kfxxb.kfid left join wm_kwxxb on wm_wlls.kwid=wm_kwxxb.kwid ",
				"kclsid,sjkcid,wlid,pcid,wm_wlls.kfid,wm_wlls.kwid,sl,wlgg,wllb,czsj,wm_kfxxb.kfmc,wm_kwxxb.kwmc,jh,yxq", "kccx_enable = '1' and wlid in " + val_wl,
				null, (page - 1) * pageSize, pageSize, new Object[] {});
		
		List<Map<String, Object>> kcxx = datasetkcxx.getList();
		if (kcxx.size() <= 0) {
			return "json:";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer wlids = new StringBuffer();
		wlids.append("(");
		for (int i = 0; i < kcxx.size(); i++) {
			String wlid_str = kcxx.get(i).get("wlid") + "";
			kcxx.get(i).put("wlid", wlid_str);
			kcxx.get(i).put("wlbh", wlbh);
			kcxx.get(i).put("wlmc", wlmc);
			kcxx.get(i).put("yxq", sdf.format(sdf.parse(kcxx.get(i).get("yxq")+"")));
			kcxx.get(i).put("zzjgid", "");//设置默认值，解决页面显示undefined 问题。
			wlids.append("\'" + wlid_str + "\'");
			if(i != (kcxx.size() - 1)){
				wlids.append(",");
			}
		}
		wlids.append(")");
		parameters.set("val_wl", wlids.toString());
		Bundle wlxxs = Sys.callModuleService("mm", "mmservice_wlxx", parameters);
		List<Map<String, Object>> wlxx_list = (List<Map<String, Object>>)wlxxs.get("wlxx");
		for (int i = 0; i < kcxx.size(); i++) {
			for(int j = 0, len = wlxx_list.size(); j < len; j++){
				if(kcxx.get(i).get("wlid").equals(wlxx_list.get(j).get("wlid")+"")){
					kcxx.get(i).put("aqkc", wlxx_list.get(j).get("aqkc"));
				}
			}
		}
		bundle.put("rows", kcxx);
		int totalPage = datasetkcxx.getTotal()%pageSize==0?datasetkcxx.getTotal()/pageSize:datasetkcxx.getTotal()/pageSize+1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", datasetkcxx.getTotal());
		return "json:";
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

	/**
	 * 刀具库存入库
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	public String save_djrk(Parameters parameters, Bundle bundle) throws Exception{
		Map<String, Object> lsmap = parameters.getMap();
		if (null != lsmap) {
			Map<String, Object> lsxxmap = new HashMap<String, Object>(); 
			Map<String, Object> kcmap = new HashMap<String, Object>(); 
			Map<String, Object> kcmxmap = new HashMap<String, Object>();
//			int sl = Math.abs(Integer.parseInt(lsmap.get("add_kcsl").toString()));
			int rksl = Math.abs(Integer.parseInt(lsmap.get("add_rksl").toString()));
			Date czsj = new Date();
			lsxxmap.put("ry_dm", Sys.getUserIdentifier());
			lsxxmap.put("sl", rksl);
			lsxxmap.put("czsj", czsj);//操作时间
			lsxxmap.put("wlid", lsmap.get("add_djbh"));	//工装ID
			lsxxmap.put("kfid", lsmap.get("add_kfid"));	//库房ID
			lsxxmap.put("kwid", lsmap.get("add_kwid"));	//库位ID
			lsxxmap.put("zzjgid", lsmap.get("add_zzjgid"));//部门ID
			lsxxmap.put("bz", lsmap.get("add_bz"));	//备注
			lsxxmap.put("kclslbdm", this.INVENTORY_TYPE_IN);
			lsxxmap.put("yggh", Sys.getUserIdentifier());//员工号
			lsxxmap.put("rkdh", lsmap.get("add_rkdh"));//入库单号
			lsxxmap.put("wlgg", lsmap.get("add_djgg"));//刀具规格
			lsxxmap.put("wltm", lsmap.get("add_djtm"));//刀具条码
			lsxxmap.put("mplh", "0");//毛坯炉号
			lsxxmap.put("pcid", "0");//批次
			lsxxmap.put("jh", lsmap.get("add_djtm"));
			lsxxmap.put("wllb", lsmap.get("add_wllb"));
			lsxxmap.put("kccx_enable", 1);
			lsxxmap.put("yxq", getDate(lsmap.get("add_yxq")+" 00:00:00"));
			
			kcmxmap.put("kfid", lsmap.get("add_kfid"));
			kcmxmap.put("kwid", lsmap.get("add_kwid"));
			kcmxmap.put("kczt", this.INVENTORY_TYPE_IN);
			kcmxmap.put("rksj", czsj);
			kcmxmap.put("rkry", Sys.getUserName());
			kcmxmap.put("rksl", rksl);
			kcmxmap.put("cksl", 0);
			kcmxmap.put("pcid", "0");
			kcmxmap.put("wlid", lsmap.get("add_djbh"));
			kcmxmap.put("jh", lsmap.get("add_djtm"));
//			kcmxmap.put("mplh", lsmap.get("add_mplh"));
			kcmxmap.put("wllb", lsmap.get("add_wllb"));
			kcmxmap.put("wlgg", lsmap.get("add_djgg"));
			kcmxmap.put("rkdh", lsmap.get("add_rkdh"));//入库单号
			kcmxmap.put("yxq", getDate(lsmap.get("add_yxq")+" 00:00:00"));
			
			String wlid = lsmap.get("add_djbh").toString();
			String kfid = lsmap.get("add_kfid").toString();
			String kwid = lsmap.get("add_kwid").toString();
			
			lsmap.put("czsj",czsj);
			Dataset datasetkcgl = Sys.query("kc_sjwlkc","sjwlkcid,kcsl", "wlid = ? and kfid = ? and kwid = ? ", null, new Object[]{wlid, kfid, kwid});
			List<Map<String, Object>> kcgl = datasetkcgl.getList();
			if(kcgl.size()<=0)
			{
				kcmap.put("wlid", lsxxmap.get("wlid"));
				kcmap.put("kfid", lsxxmap.get("kfid"));
				kcmap.put("kwid", lsxxmap.get("kwid"));
				kcmap.put("kcsl", lsxxmap.get("sl"));
				kcmap.put("czsj", czsj);
				kcmap.put("pcid", lsxxmap.get("pcid"));//批次
				kcmap.put("mplh", lsxxmap.get("mplh"));//毛坯
				kcmap.put("wlgg", lsxxmap.get("wlgg"));
				kcmap.put("wltm", lsxxmap.get("wltm"));
				kcmap.put("wllb", lsmap.get("add_wllb"));
				int n = Sys.insert("kc_sjwlkc", kcmap);
				if(n==1)
				{
					lsxxmap.put("sjkcid", kcmap.get("sjwlkcid"));
					kcmxmap.put("sjwlkcid", kcmap.get("sjwlkcid"));
					n = Sys.insert("kc_wlkcmx", kcmxmap);
				}
				n = Sys.insert("wm_wlls", lsxxmap);
			}else{
				kcmap.put("kcsl", Float.parseFloat(kcgl.get(0).get("kcsl").toString())+rksl);
				kcmap.put("czsj", czsj);
				int n = Sys.update("kc_sjwlkc", kcmap, "sjwlkcid = ?", new Object[]{kcgl.get(0).get("sjwlkcid")});
				if(n==1)
				{
					lsxxmap.put("sjkcid", kcgl.get(0).get("sjwlkcid"));
					kcmxmap.put("sjwlkcid", kcgl.get(0).get("sjwlkcid"));
				}
				n = Sys.insert("wm_wlls", lsxxmap);
				n = Sys.insert("kc_wlkcmx", kcmxmap);
			}
			
			//查询刀具入库流水
			Dataset rkxx = Sys.query(new String[]{"wm_wlls","wm_kwxxb","wm_kfxxb"}, "wm_wlls join wm_kfxxb on wm_wlls.kfid = wm_kfxxb.kfid "
					+ " join wm_kwxxb on wm_wlls.kwid = wm_kwxxb.kwid ","wm_wlls.wlid,wm_wlls.sl,wm_kwxxb.kwmc,wm_kfxxb.kfmc,"
							+ "wm_wlls.pcid,wm_wlls.zzjgid,wm_wlls.rkdh","wm_wlls.kclsid = ?" ,
					null, null,new Object[]{lsxxmap.get("kclsid")});
			List<Map<String, Object>> rkxxList = rkxx.getList();
			//查询刀具名称
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
			String templateId = "djrk_tp";
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("wlid", wlxx.get(0).get("wlbh"));//刀具编号
			data.put("wlmc", wlxx.get(0).get("wlmc"));//刀具名称
			data.put("sl", rkxxList.get(0).get("sl"));//入库数量
			data.put("kwmc", rkxxList.get(0).get("kwmc"));//库位
			data.put("kfmc", rkxxList.get(0).get("kfmc"));//库房
			data.put("zzjgid", rkxxList.get(0).get("zzjgid"));//部门ID
			data.put("zzjgmc", resultOrg.get("name"));//部门 名称
			data.put("userid", Sys.getUserIdentifier());//操作人
			data.put("username", Sys.getUserName());//操作人
			data.put("rksj", czsj.getTime());//入库时间
			sendActivity(activityType, templateId, true, roles, null, null, data);
		}
		return null;
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
			lsmap = jsonarray.getJSONObject(i);
			
			String sjwlkcid = lsmap.get("sjwlkcid").toString();
			String jh = lsmap.get("jh").toString();
			String wlgg = lsmap.get("wlgg").toString();
			String wllb = lsmap.get("wllb").toString();
			Dataset wlkcxx = Sys.query("kc_sjwlkc", "kcsl,sjwlkcid", "sjwlkcid = '"+sjwlkcid + "'", null, new Object[]{});
			
			int cksl = Math.abs(Integer.parseInt(lsmap.get("ckwlsl").toString()));//出库数量
			BigDecimal wl_kcsl = new BigDecimal(wlkcxx.getList().get(0).get("kcsl").toString());
			kcmap.put("kcsl", wl_kcsl.subtract(new BigDecimal(cksl+"")));
			lsxxmap.put("sl", cksl);//库存流水，2016/8/27 修改为正数，根据库存流水类别判断出入库
			lsxxmap.put("czsj", new Date());
			lsxxmap.put("wlid", lsmap.get("wlid").toString());
			lsxxmap.put("kfid", lsmap.get("kfid").toString());
			lsxxmap.put("kwid", lsmap.get("kwid").toString());
			lsxxmap.put("zzjgid", lsmap.get("zzjgid").toString());
			lsxxmap.put("kclslbdm", this.INVENTORY_TYPE_OUT);
			lsxxmap.put("sjkcid", lsmap.get("sjwlkcid"));
			lsxxmap.put("bz", "刀具出库");
			lsxxmap.put("jh", jh);
			lsxxmap.put("wlgg", wlgg);
			lsxxmap.put("wllb", wllb);
			lsxxmap.put("yggh", Sys.getUserIdentifier());//员工号
			lsxxmap.put("kccx_enable", 0);
			int n = Sys.update("kc_sjwlkc", kcmap, "sjwlkcid = ?", new Object[]{lsmap.get("sjwlkcid")});
			LOGGER.info("刀具出库：刀具库存更新成功："+n);
			Dataset kcmxset = Sys.query("kc_wlkcmx", "wlkcmxid", "sjwlkcid=? and jh =?", null, new Object[]{sjwlkcid,jh});
			lsxxmap.put("rkdh", kcmxset.getList().get(0).get("rkdh"));
			n = Sys.insert("wm_wlls", lsxxmap);
			
			Map<String, Object> kcmxmap = new HashMap<String, Object>();
			kcmxmap.put("cksl", cksl);
			kcmxmap.put("cksj", new Date());
			kcmxmap.put("ckry", Sys.getUserName());
			kcmxmap.put("kczt", this.INVENTORY_TYPE_OUT);
			String wlkcmxid = "'" + kcmxset.getList().get(0).get("wlkcmxid") + "'";
			String kclsid = lsmap.get("kclsid") + "";
			
			Map<String, Object> kcls = new HashMap<String, Object>();
			kcls.put("kccx_enable", 0);
			n = Sys.update("wm_wlls", kcls, "kclsid = '"+kclsid+""+"'", new Object[]{});
			
			n = Sys.update("kc_wlkcmx", kcmxmap, "wlkcmxid= " + wlkcmxid, new Object[]{});
			
			//查询刀具出库流水
			Dataset rkxx = Sys.query(new String[]{"wm_wlls","wm_kwxxb","wm_kfxxb"}, "wm_wlls join wm_kfxxb on wm_wlls.kfid = wm_kfxxb.kfid "
					+ " join wm_kwxxb on wm_wlls.kwid = wm_kwxxb.kwid ","wm_wlls.wlid,wm_wlls.sl,wm_kwxxb.kwmc,wm_kfxxb.kfmc,wm_wlls.pcid,wm_wlls.zzjgid", "wm_wlls.kclsid = ?" ,
					null, null,new Object[]{lsxxmap.get("kclsid")});
			List<Map<String, Object>> rkxxList = rkxx.getList();
			//查询刀具名称
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
			String templateId = "djck_tp";
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("wlid", rkxxList.get(0).get("wlid"));//刀具编号
			data.put("wlmc", wlxx.get(0).get("wlmc"));//刀具名称
			data.put("sl", rkxxList.get(0).get("sl"));//入库数量
			data.put("kwmc", rkxxList.get(0).get("kwmc"));//库位
			data.put("kfmc", rkxxList.get(0).get("kfmc"));//库房
			data.put("pcid", rkxxList.get(0).get("pcid"));//批次 
			data.put("zzjgid", rkxxList.get(0).get("zzjgid"));//部门ID
			data.put("zzjgmc", resultOrg.get("name"));//部门 名称
			data.put("userid", Sys.getUserIdentifier());//操作人
			data.put("username", Sys.getUserName());//操作人
			data.put("cksj", ((Date)lsxxmap.get("czsj")).getTime());//出库时间
			sendActivity(activityType, templateId, true, roles, null, null, data);
		}  
		return null;
	}
	public String getSingleJh(Parameters parameters,Bundle bundle){
		
		String jh = parameters.getString("jh");
		
		Dataset dataset = Sys.query("kc_wlkcmx", "wlkcmxid,wlid", "(wllb = '10' or wllb = '60') and jh = '" + jh +"'", null, new Object[]{});
		
		List<Map<String, Object>> list = dataset.getList();
		
		bundle.put("data", list);
		return "json:";
	}
	public Date getDate(String time) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		return sdf.parse(time);
	}
}
