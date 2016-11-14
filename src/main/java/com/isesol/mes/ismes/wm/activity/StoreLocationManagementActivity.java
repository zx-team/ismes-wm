package com.isesol.mes.ismes.wm.activity;

import java.util.ArrayList;
import java.util.HashMap;
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
 * 
 * @author YangFan
 *
 */
public class StoreLocationManagementActivity {

	private Logger LOGGER = Logger.getLogger(this.getClass());

	/**
	 * 库位管理界面
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_kwgl(Parameters parameters, Bundle bundle) {
		return "wm_kwgl";
	}

	/**
	 * 查询库位信息
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	public String table_kwgl(Parameters parameters, Bundle bundle) throws Exception {
		// 查询库存信息
		int page = Integer.parseInt(parameters.get("page").toString());
		int pageSize = Integer.parseInt(parameters.get("pageSize").toString());
		Dataset datasetkwgl = Sys.query(new String[]{"wm_kwxxb","wm_kfxxb"}, " wm_kwxxb left join wm_kfxxb on wm_kwxxb.kfid = wm_kfxxb.kfid ", 
				" kwid, kwbh, kwmc, kwwz, wm_kwxxb.kfid,wm_kfxxb.kfmc, wm_kwxxb.qybsdm ",
				null, " kwid desc ", (page - 1) * pageSize, pageSize, new Object[]{});//"wm_kwxxb.qybsdm ='10'"
		
		List<Map<String, Object>> kwgl = datasetkwgl.getList();
		bundle.put("rows", kwgl);
		int totalPage = datasetkwgl.getTotal() % pageSize == 0 ? datasetkwgl.getTotal() / pageSize :datasetkwgl.getTotal() / pageSize + 1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", datasetkwgl.getTotal());
		return "json:";
	}

	/**
	 * 新增或者修改库位信息
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String update_kfxx(Parameters parameters, Bundle bundle) throws Exception {

		List<Map<String, Object>> kwxx_inlist = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> kwxx_uplist = new ArrayList<Map<String, Object>>();
		List<Object[]> objlist = new ArrayList<Object[]>();
		JSONArray jsonarray = JSONArray.fromObject(parameters.get("data_list"));
		for (int i = 0; i < jsonarray.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> map_in = new HashMap<String, Object>();
			map = jsonarray.getJSONObject(i);
			if (StringUtils.isNotBlank(map.get("addSign").toString())) {
				//判断库位编号是否已经存在，如果已经存在，提示用户编号不能重复
				Dataset ds = Sys.query("wm_kwxxb", "kwbh", "kwbh=?", null, new Object[]{map.get("kwbh")});
				if(null != ds && ds.getCount() > 0){
					bundle.put("code", "1");//0:成功，1:失败
					bundle.put("message", "库位编号重复，请重新输入！");
					return "json:";
				}
				
//				map_in.put("kwid", map.get("kwid"));
				map_in.put("kwbh", map.get("kwbh"));
				map_in.put("kwmc", map.get("kwmc"));
				map_in.put("kwwz", map.get("kwwz"));
				map_in.put("kfid", map.get("kfid"));
				map_in.put("qybsdm", map.get("qybsdm"));

				LOGGER.info("kfid：" + map.get("kfid") + " kwbh：" + map.get("kwbh") + " kwmc：" + map.get("kwmc") +" kwwz：" + map.get("kwwz") + " kfid:" + map.get("kfid")+ " qybsdm：" + map.get("qybsdm"));
				kwxx_inlist.add(map_in);
			} else {
				//判断库位编号是否已经存在，如果已经存在，提示用户编号不能重复
				Dataset ds = Sys.query("wm_kwxxb", "kwbh", "kwbh=? and kwid<>? ", null, new Object[]{map.get("kwbh"),map.get("kwid")});
				if(null != ds && ds.getCount() > 0){
					bundle.put("code", "1");//0:成功，1:失败
					bundle.put("message", "库位编号重复，请重新输入！");
					return "json:";
				}
				
				objlist.add(new Object[] { map.get("kwid") });
				map_in.put("kwbh", map.get("kwbh"));
				map_in.put("kwmc", map.get("kwmc"));
				map_in.put("kwwz", map.get("kwwz"));
				map_in.put("kfid", map.get("kfid"));
				map_in.put("qybsdm", map.get("qybsdm"));
				
				kwxx_uplist.add(map_in);
			}
		}
		if (kwxx_inlist.size() > 0) {
			try {
				int i = Sys.insert("wm_kwxxb", kwxx_inlist);
				System.out.println("新增库位信息 数量：" + i );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (kwxx_uplist.size() > 0) {
			try {
				int i = Sys.update("wm_kwxxb", kwxx_uplist, "kwid=?", objlist);
				LOGGER.info("更新库位信息 数量：" + i );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		bundle.put("code", "0");//0:成功，1:失败
		return "json:";
	}

	/**
	 * 删除库位信息
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
			int i = Sys.delete("wm_kwxxb", "kwid=?", objlist);
			System.out.println("删除库位信息 数量：" + i + " 库位id：" + String.valueOf(objlist.get(0)[0]));
		} catch (Exception e) {
			e.printStackTrace();
			throw  new Exception("删除数据失败！",e);
		}
		return "json:";
	}
	
	/**
	 * 选择库房
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String kfSelect(Parameters parameters,Bundle bundle){
		Dataset datasetkfgl = Sys.query("wm_kfxxb", " kfid, kfbh, kfmc, kfwz, zzjgid, qybsdm ", " qybsdm ='10' " , " kfid desc ", new Object[] {});
		List<Map<String,Object>> kfList = datasetkfgl.getList();
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map : kfList){
			Map<String,Object> m = new HashMap<String, Object>();
			m.put("label", map.get("kfmc"));
			m.put("value", map.get("kfid"));
			returnList.add(m);
		}
		bundle.put("select_kf", returnList.toArray());
		return "json:select_kf";
	}

}
