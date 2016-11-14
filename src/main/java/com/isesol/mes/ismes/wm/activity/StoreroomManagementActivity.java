package com.isesol.mes.ismes.wm.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.isesol.ismes.platform.core.service.bean.Dataset;
import com.isesol.ismes.platform.module.Bundle;
import com.isesol.ismes.platform.module.Parameters;
import com.isesol.ismes.platform.module.Sys;

import net.sf.json.JSONArray;

/**
 * 库房管理
 * @author YangFan
 *
 */
public class StoreroomManagementActivity {

	/**
	 * 库房管理界面
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_kfgl(Parameters parameters, Bundle bundle) {
		return "wm_kfgl";
	}

	/**
	 * 查询库房信息
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	public String table_kfgl(Parameters parameters, Bundle bundle) throws Exception {
		// 查询库存信息
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());
		Dataset datasetkfgl = Sys.query("wm_kfxxb", " kfid, kfbh, kfmc, kfwz, zzjgid, qybsdm ", null, " kfid desc ",
				(page - 1) * pageSize, pageSize, new Object[] {});
		List<Map<String, Object>> kfgl = datasetkfgl.getList();
		
		if(null != kfgl && !kfgl.isEmpty()){
			Iterator<Map<String, Object>> iterator = kfgl.iterator();
			StringBuffer cjSb = new StringBuffer();
			cjSb.append("(");
			while(iterator.hasNext()){
				Map<String, Object> map = (Map) iterator.next();
				if(null != map && null != map.get("zzjgid") && StringUtils.isNotBlank(map.get("zzjgid").toString())){
					cjSb.append(map.get("zzjgid")).append(",");
				}
			}
			cjSb.deleteCharAt(cjSb.length()-1);
			cjSb.append(")");
			Parameters part = new Parameters();
			part.set("zzjg", cjSb.toString());
			Bundle cjBundle = Sys.callModuleService("org", "cjServiceByZzjgList", part);
			List<Map<String,Object>> cjList = (List<Map<String, Object>>) cjBundle.get("data");
			
			iterator = kfgl.iterator();
			while(iterator.hasNext()){
				Map<String, Object> map = (Map<String, Object>) iterator.next();
				for (int i = 0; i < cjList.size(); i++) {
					Map<String, Object> cjMap = cjList.get(i);
					if(null != map && null != map.get("zzjgid") && StringUtils.isNotBlank(map.get("zzjgid").toString()) && map.get("zzjgid").equals(cjMap.get("zzjgid"))){
						map.put("zzjgmc", cjMap.get("zzjgmc"));
						break;
					}
				}
			}
		}
		
		bundle.put("rows", kfgl);
		int totalPage = datasetkfgl.getTotal() % pageSize == 0 ? datasetkfgl.getTotal() / pageSize :datasetkfgl.getTotal() / pageSize + 1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", datasetkfgl.getTotal());
		return "json:";
	}

	/**
	 * 新增或者修改库房信息
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String update_kfxx(Parameters parameters, Bundle bundle) throws Exception {

		List<Map<String, Object>> kfxx_inlist = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> kfxx_uplist = new ArrayList<Map<String, Object>>();
		List<Object[]> objlist = new ArrayList<Object[]>();
		JSONArray jsonarray = JSONArray.fromObject(parameters.get("data_list"));
		for (int i = 0; i < jsonarray.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> map_in = new HashMap<String, Object>();
			map = jsonarray.getJSONObject(i);
			if (StringUtils.isNotBlank(map.get("addSign").toString())) {
				map_in.put("kfid", map.get("kfid"));
				map_in.put("kfbh", map.get("kfbh"));
				map_in.put("kfmc", map.get("kfmc"));
				map_in.put("kfwz", map.get("kfwz"));
				map_in.put("zzjgid", map.get("zzjgid"));
				map_in.put("qybsdm", map.get("qybsdm"));

				System.out.println("kfid：" + map.get("kfid") + " kfbh：" + map.get("kfbh") + " kfmc：" + map.get("kfmc") +" kfwz：" + map.get("kfwz") + " zzjgid:" + map.get("zzjgid")+ " qybsdm：" + map.get("qybsdm"));

				Dataset ds = Sys.query("wm_kfxxb", "kfbh", "kfbh=?", null, new Object[]{map.get("kfbh")});
				if(null != ds && ds.getCount() > 0){
					bundle.put("code", "1");//0:成功，1:失败
					bundle.put("message", "库房编号重复，请重新输入！");
					return "json:";
				}
				
				kfxx_inlist.add(map_in);
			} else {
				objlist.add(new Object[] { map.get("kfid") });
				map_in.put("kfid", map.get("kfid"));
				map_in.put("kfbh", map.get("kfbh"));
				map_in.put("kfmc", map.get("kfmc"));
				map_in.put("kfwz", map.get("kfwz"));
				map_in.put("zzjgid", map.get("zzjgid"));
				map_in.put("qybsdm", map.get("qybsdm"));

				Dataset ds = Sys.query("wm_kfxxb", "kfbh", "kfbh=? and kfid<>?", null, new Object[]{map.get("kfbh"),map.get("kfid")});
				if(null != ds && ds.getCount() > 0){
					bundle.put("code", "1");//0:成功，1:失败
					bundle.put("message", "库房编号重复，请重新输入！");
					return "json:";
				}
				
				kfxx_uplist.add(map_in);
			}
		}
		if (kfxx_inlist.size() > 0) {
			try {
				int i = Sys.insert("wm_kfxxb", kfxx_inlist);
				System.out.println("新增库房信息 数量：" + i );
			} catch (Exception e) {
				e.printStackTrace();
				throw  new Exception("新增库房信息失败！",e);
			}
		}
		if (kfxx_uplist.size() > 0) {
			try {
				int i = Sys.update("wm_kfxxb", kfxx_uplist, "kfid=?", objlist);
				System.out.println("更新库房信息 数量：" + i );
			} catch (Exception e) {
				e.printStackTrace();
				throw  new Exception("更新库房信息失败！",e);
			}
		}
		bundle.put("code", "0");//0:成功，1:失败
		return "json:";
	}

	/**
	 * 删除库房信息
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String del_kfxx(Parameters parameters, Bundle bundle)  throws Exception{
		if (StringUtils.isBlank(parameters.getString("data_list"))) {
			return "json:";
		}
		List<Object[]> objlist = new ArrayList<Object[]>();
		objlist.add(new Object[] { Integer.parseInt(parameters.getString("data_list")) });
		try {
			int i = Sys.delete("wm_kfxxb", "kfid=?", objlist);
			System.out.println("删除库房信息 数量：" + i + " 库房id：" + String.valueOf(objlist.get(0)[0]));
		} catch (Exception e) {
			e.printStackTrace();
			throw  new Exception("删除库房信息失败！",e);
		}
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
	

}
