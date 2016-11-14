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
 * 工装库存管理
 * @author Guo Feng
 *
 */
public class FrockActivity {

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
	 * 跳转工装库存管理界面
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_gzkcgl(Parameters parameters, Bundle bundle){
		return "wm_gzkcgl";
	}
	
	/**
	 * 显示工装库存管理信息
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String table_gzkcgl(Parameters parameters, Bundle bundle) throws Exception {
		//查询工装信息
		Bundle b_wlxx = Sys.callModuleService("mm", "mmservice_wlxxkc", parameters);
		List<Map<String, Object>> wlxx = (List<Map<String, Object>>) b_wlxx.get("wlxx");
		if (wlxx.size()<=0) {
			return "json:";
		}
		String val_wl = "(";
		for (int i = 0; i < wlxx.size(); i++) {
			if(!wlxx.get(i).get("wllbdm").equals("20")
					&& !wlxx.get(i).get("wllbdm").equals("30")){
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
		Dataset datasetkcgl = Sys.query("kc_sjwlkc","wlid,sum(kcsl) kcsl", "kcsl > '0' and wlid in " +val_wl ,"wlid ", null, (page-1)*pageSize, pageSize,new Object[]{});
		List<Map<String, Object>> kcgl = datasetkcgl.getList();
		if (kcgl.size()<=0) {
			return "json:";
		}
		val_wl = "(";
		for (int i = 0; i < kcgl.size(); i++) {
			for (int j = 0; j < wlxx.size(); j++) {
				if (kcgl.get(i).get("wlid").toString().equals(wlxx.get(j).get("wlid").toString())) {
					kcgl.get(i).put("gzid", wlxx.get(j).get("wlid"));
					kcgl.get(i).put("gzbh", wlxx.get(j).get("wlbh"));
					kcgl.get(i).put("gzlx", wlxx.get(j).get("wllxdm"));
					kcgl.get(i).put("gzmc", wlxx.get(j).get("wlmc"));
					kcgl.get(i).put("gzpp", wlxx.get(j).get("wlpp"));
					kcgl.get(i).put("gzlb", wlxx.get(j).get("wllbdm"));
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
	 * 查询工装编号信息
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String gzbhSelect(Parameters parameters, Bundle bundle)throws Exception{
		parameters.set("query_wlbh", parameters.get("query"));
		//查询工装信息
		Bundle b_gzxx = Sys.callModuleService("mm", "mmservice_wlxxkc", parameters);
		List<Map<String, Object>> gzxx = (List<Map<String, Object>>) b_gzxx.get("wlxx");
		if (gzxx.size()<=0) {
			return "json:";
		}
		//删除非工装类型信息
		Iterator<Map <String,Object>> iterator = gzxx.iterator();
		int i = 0;
		while(iterator.hasNext()){
			Map<String,Object> map = iterator.next();
			//夹具20，量具30
			if (null != map && !"20".equals(map.get("wllbdm").toString())&&
								!"30".equals(map.get("wllbdm").toString())){
				iterator.remove();
			}else{
				gzxx.get(i).put("dwmc",Sys.getDictFieldNames("JLDW", (String)gzxx.get(i).get("wldwdm")));
				gzxx.get(i).put("value", gzxx.get(i).get("wlid"));
				gzxx.get(i).put("label", gzxx.get(i).get("wlbh"));
				gzxx.get(i).put("title", gzxx.get(i).get("wlbh"));
				gzxx.get(i).put("gzgg", gzxx.get(i).get("wlgg"));
				i++;
			}
		}
		bundle.put("gzxx", gzxx);
		return "json:gzxx";
	}
	
	/**库存详细页面
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String table_kcxx(Parameters parameters, Bundle bundle) throws Exception {
		String wlid = parameters.getString("gzid");
		if(StringUtils.isBlank(wlid))
		{
			return "json:";
		}
		String wlbh = parameters.getString("gzbh");
		String wlmc = parameters.getString("gzmc");
		
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
		for (int i = 0; i < kcxx.size(); i++) {
			kcxx.get(i).put("gzid", kcxx.get(i).get("wlid"));
			kcxx.get(i).put("gzbh", wlbh);
			kcxx.get(i).put("gzmc", wlmc);
			kcxx.get(i).put("yxq", sdf.format(sdf.parse(kcxx.get(i).get("yxq")+"")));
			kcxx.get(i).put("zzjgid", "");//设置默认值，解决页面显示undefined 问题。
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
	 * 工装库存入库
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	public String save_gzrk(Parameters parameters, Bundle bundle) throws Exception{
		Map<String, Object> lsmap = parameters.getMap();
		if (null != lsmap) {
			Map<String, Object> lsxxmap = new HashMap<String, Object>(); 
			Map<String, Object> kcmap = new HashMap<String, Object>(); 
			Map<String, Object> kcmxmap = new HashMap<String, Object>();
			int rksl = Math.abs(Integer.parseInt(lsmap.get("add_kcsl").toString()));
			Date czsj = new Date();
			lsxxmap.put("ry_dm", Sys.getUserIdentifier());
			lsxxmap.put("sl", rksl);
			lsxxmap.put("czsj", czsj);//操作时间
			lsxxmap.put("wlid", lsmap.get("add_gzbh"));	//工装ID
			lsxxmap.put("kfid", lsmap.get("add_kfid"));	//库房ID
			lsxxmap.put("kwid", lsmap.get("add_kwid"));	//库位ID
			lsxxmap.put("zzjgid", lsmap.get("add_zzjgid"));//部门ID
			lsxxmap.put("bz", lsmap.get("add_bz"));	//备注
			lsxxmap.put("kclslbdm", this.INVENTORY_TYPE_IN);
			lsxxmap.put("yggh", Sys.getUserIdentifier());//员工号
			lsxxmap.put("rkdh", lsmap.get("add_rkdh"));//入库单号
			lsxxmap.put("wlgg", lsmap.get("add_gzgg"));//工装规格
			lsxxmap.put("wltm", lsmap.get("add_gztm"));//工装条码
			lsxxmap.put("mplh", "0");//毛坯炉号
			lsxxmap.put("pcid", "0");//批次
			lsxxmap.put("jh", lsmap.get("add_gztm"));
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
			kcmxmap.put("wlid", lsmap.get("add_gzbh"));
			kcmxmap.put("jh", lsmap.get("add_gztm"));
//			kcmxmap.put("mplh", lsmap.get("add_mplh"));
			kcmxmap.put("wllb", lsmap.get("add_wllb"));
			kcmxmap.put("wlgg", lsmap.get("add_gzgg"));
			kcmxmap.put("rkdh", lsmap.get("add_rkdh"));//入库单号
			kcmxmap.put("yxq", getDate(lsmap.get("add_yxq")+" 00:00:00"));
			
			String wlid = lsmap.get("add_gzbh").toString();
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
					kcmxmap.put("sjwlkcid", kcgl.get(0).get("sjwlkcid"));
				}
				n = Sys.insert("wm_wlls", lsxxmap);
				n = Sys.insert("kc_wlkcmx", kcmxmap);
			}
			
			//查询工装入库流水
			Dataset rkxx = Sys.query(new String[]{"wm_wlls","wm_kwxxb","wm_kfxxb"}, "wm_wlls join wm_kfxxb on wm_wlls.kfid = wm_kfxxb.kfid "
					+ " join wm_kwxxb on wm_wlls.kwid = wm_kwxxb.kwid ","wm_wlls.wlid,wm_wlls.sl,wm_kwxxb.kwmc,wm_kfxxb.kfmc,"
							+ "wm_wlls.pcid,wm_wlls.zzjgid,wm_wlls.rkdh","wm_wlls.kclsid = ?" ,
					null, null,new Object[]{lsxxmap.get("kclsid")});
			List<Map<String, Object>> rkxxList = rkxx.getList();
			//查询工装名称
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
			String templateId = "gzrk_tp";
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("wlid", wlxx.get(0).get("wlbh"));//工装编号
			data.put("wlmc", wlxx.get(0).get("wlmc"));//工装名称
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
			lsxxmap.put("bz", "工装出库");
			lsxxmap.put("jh", jh);
			lsxxmap.put("wlgg", wlgg);
			lsxxmap.put("wllb", wllb);
			lsxxmap.put("yggh", Sys.getUserIdentifier());//员工号
			lsxxmap.put("kccx_enable", 0);
			int n = Sys.update("kc_sjwlkc", kcmap, "sjwlkcid = ?", new Object[]{lsmap.get("sjwlkcid")});
			LOGGER.info("工装出库：工装库存更新成功："+n);
			Dataset kcmxset = Sys.query("kc_wlkcmx", "rkdh,wlkcmxid", "sjwlkcid=? and jh =?", null, new Object[]{sjwlkcid,jh});
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
			//查询工装出库流水
			Dataset rkxx = Sys.query(new String[]{"wm_wlls","wm_kwxxb","wm_kfxxb"}, "wm_wlls join wm_kfxxb on wm_wlls.kfid = wm_kfxxb.kfid "
					+ " join wm_kwxxb on wm_wlls.kwid = wm_kwxxb.kwid ","wm_wlls.wlid,wm_wlls.sl,wm_kwxxb.kwmc,wm_kfxxb.kfmc,wm_wlls.pcid,wm_wlls.zzjgid", "wm_wlls.kclsid = ?" ,
					null, null,new Object[]{lsxxmap.get("kclsid")});
			List<Map<String, Object>> rkxxList = rkxx.getList();
			//查询工装名称
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
			String templateId = "gzck_tp";
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("wlid", rkxxList.get(0).get("wlid"));//工装编号
			data.put("wlmc", wlxx.get(0).get("wlmc"));//工装名称
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
	public Date getDate(String time) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		return sdf.parse(time);
	}
	public String getSingleJh(Parameters parameters,Bundle bundle){
		
		String jh = parameters.getString("jh");
		
		Dataset dataset = Sys.query("kc_wlkcmx", "wlkcmxid,wlid", "(wllb = '20' or wllb = '30') and kczt = '10' and jh = '" + jh +"'", null, new Object[]{});
		
		List<Map<String, Object>> list = dataset.getList();
		
		bundle.put("data", list);
		return "json:";
	}
}
