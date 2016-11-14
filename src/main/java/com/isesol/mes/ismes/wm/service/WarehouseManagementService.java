package com.isesol.mes.ismes.wm.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.isesol.ismes.platform.core.service.bean.Dataset;
import com.isesol.ismes.platform.module.Bundle;
import com.isesol.ismes.platform.module.Parameters;
import com.isesol.ismes.platform.module.Sys;

public class WarehouseManagementService {
	
	private Logger log4j = Logger.getLogger(WarehouseManagementService.class);
	/**
	 * service 
	 * 根据物料id 得到物料的库存信息
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public void wl_warehouseInfoByWlid(Parameters parameters,Bundle bundle){
		String wlid = (String) parameters.get("wlid");
		if(StringUtils.isBlank(wlid)){
			log4j.info("查询物料库存信息，物料id不能为空");
			return;
		}
		Dataset set = Sys.query("kc_sjwlkc", "sjwlkcid,"/*实际库存表id*/
				+"wlid,"/*物料id*/+"pcid,"/*批次id*/
				+"kfid,"/*库房代码*/+"kwid,"/*库位代码*/
				+"kcsl,"/*数量*/+"czsj"/*操作时间*/, 
				"wlid = ?"  , null, null,wlid);
		List<Map<String, Object>> warehouseInfoList = set.getList();
		//库存数量
		BigDecimal num = BigDecimal.ZERO;
		if(!CollectionUtils.isEmpty(warehouseInfoList)){
			for(Map<String, Object> map : warehouseInfoList){
				if(map.get("kcsl") == null || StringUtils.isBlank(map.get("kcsl").toString())){
					continue;
				}
				BigDecimal b = new BigDecimal(map.get("kcsl").toString());
				num = num.add(b);
				
				if(map.get("kwid") != null){
					Dataset kwxx_dataset = Sys.query("wm_kwxxb", "kwbh,kwmc,kwwz", " kwid = ? ", null, new Object[]{map.get("kwid")});
					String kwmc = (String) kwxx_dataset.getMap().get("kwmc");
					map.put("kwmc", kwmc);
				}
				if(map.get("kfid") != null){
					Dataset kfxx_dataset = Sys.query("wm_kfxxb", "kfbh,kfmc,kfwz", " kfid = ? ", null ,  new Object[]{map.get("kfid")});
					String kfmc = (String) kfxx_dataset.getMap().get("kfmc");
					map.put("kfmc", kfmc);
				}
			}
		}
		//库存信息
		bundle.put("warehouseInfo", warehouseInfoList);
		bundle.put("warehouseNum", num);
	}
	
	public void getWlkcByWlidLh(Parameters parameters,Bundle bundle){
		
		String wlid = "'"+parameters.getString("wlid")+"'";
		String mplh = "'"+parameters.getString("mplh")+"'";
		
		Dataset dataset = Sys.query("kc_sjwlkc", "sum(kcsl) kcsl,max(wlid) wlid", "wlid = "+wlid+" and mplh = " +mplh, "wlid,mplh", null, new Object[]{});
		
		List<Map<String, Object>> list = dataset.getList();
		if(CollectionUtils.isEmpty(list)){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("kcsl", 0);
			map.put("wlid", wlid);
			list.add(map);
		}
		bundle.put("wlkcxx", list);
	}
	
	
	/**
	 * service 
	 * 根据零件id 得到零件的库存信息
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public void lj_warehouseInfoByLjid(Parameters parameters,Bundle bundle){
		String ljid = (String) parameters.get("ljid");
		if(StringUtils.isBlank(ljid)){
			log4j.info("查询零件库存信息，零件id不能为空");
			return;
		}
		Dataset set = Sys.query("kc_sjljkc", "sjljkcid,"/*实际库存表id*/
				+"ljid,"/*零件id*/+"pcid,"/*批次id*/
				+"kfid,"/*库房id*/+"kwid,"/*库位i*/
				+"kcsl,"/*数量*/+"czsj"/*操作时间*/, 
				"ljid = ?"  , null, null,ljid);
		
		List<Map<String, Object>> warehouseInfoList = set.getList();
		BigDecimal num = BigDecimal.ZERO;
		if(!CollectionUtils.isEmpty(warehouseInfoList)){
			for(Map<String, Object> map : warehouseInfoList){
				if(map.get("kcsl") == null || StringUtils.isBlank(map.get("kcsl").toString())){
					continue;
				}
				BigDecimal b = new BigDecimal(map.get("kcsl").toString());
				num = num.add(b);
				
				if(map.get("kwid") != null){
					Dataset kwxx_dataset = Sys.query("wm_kwxxb", "kwbh,kwmc,kwwz", " kwid = ? ", null, new Object[]{map.get("kwid")});
					String kwmc = (String) kwxx_dataset.getMap().get("kwmc");
					map.put("kwmc", kwmc);
				}
				if(map.get("kfid") != null){
					Dataset kfxx_dataset = Sys.query("wm_kfxxb", "kfbh,kfmc,kfwz", " kfid = ? ", null ,  new Object[]{map.get("kfid")});
					String kfmc = (String) kfxx_dataset.getMap().get("kfmc");
					map.put("kfmc", kfmc);
				}
			}
		}
		//库存信息
		bundle.put("warehouseInfo", warehouseInfoList);
		//库存数量
		bundle.put("warehouseNum", num);
	}
	
	/**按照物料ID集合查询库存信息
	 * @param parameters
	 * @param bundle
	 * @throws Exception
	 */
	public void queryService_kcxx(Parameters parameters, Bundle bundle) throws Exception {
		String val_wl = parameters.getString("val_wl");
		if (null==val_wl) {
			return;
		}
		Dataset dataset_kcxx = Sys.query(new String[]{"kc_sjwlkc","wm_kfxxb"}, "kc_sjwlkc left join wm_kfxxb on kc_sjwlkc.kfid = wm_kfxxb.kfid"," sjwlkcid,wlid,pcid,kc_sjwlkc.kfid,kwid,kcsl,czsj,kfmc ", "wlid in "+val_wl, null,new Object[]{} );
		bundle.put("kcxx", dataset_kcxx.getList());
	}
	/**
	 * 按照物料ID查询毛坯炉号
	 * @param parameters
	 * @param bundle
	 * @throws Exception
	 */
	public void queryService_mplh(Parameters parameters, Bundle bundle)throws Exception {
		String val_wl = parameters.getString("val_wl");
		if (null==val_wl) {
			return;
		}
		Dataset dataset_kcxx = Sys.query("kc_sjwlkc", "wlid,mplh", "wlid in "+val_wl.toString(), null, new Object[]{});
		bundle.put("mplh", dataset_kcxx.getList());
	}
	
	/**
	 * 通过物料ID查询对应的物料实际库存数量
	 * @param parameters
	 * @param bundle
	 */
	public void queryService_sjwlkcByIds(Parameters parameters, Bundle bundle){
		String wlids = parameters.getString("wlids");
		if(StringUtils.isEmpty(wlids)){
			return;
		}
		Dataset dataset = Sys.query("kc_sjwlkc", "wlid,sum(kcsl) total", "wlid in "+wlids, "wlid", null, new Object[]{});
		bundle.put("sjwlkc", dataset.getList());
	}
	/**
	 * 通过物料ID和mplh查询对应的物料实际库存数量
	 * @param parameters
	 * @param bundle
	 */
	public void queryService_sjwlkcByIdandMplh(Parameters parameters, Bundle bundle){
		String wlids = parameters.getString("wlids");
		if(StringUtils.isEmpty(wlids)){
			return;
		}
		String mplh = parameters.getString("mplh");
		String mplhcon = "";
		if(mplh != null && !"".equals(mplh)){
			mplhcon = " and mplh = '"+mplh+"'";
		}
		Dataset dataset = Sys.query("kc_sjwlkc", "wlid,sum(kcsl) total", "wlid in "+wlids+mplhcon, "wlid", null, new Object[]{});
		bundle.put("sjwlkc", dataset.getList());
	}
	/**根据毛坯炉号查询库存信息
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public void query_kcxx_by_mplh(Parameters parameters, Bundle bundle) {
		Dataset dataset_kcxx;
		if(null!=parameters.get("wlid")&&!"".equals(parameters.get("wlid"))){
			dataset_kcxx = Sys.query("kc_sjwlkc","sjwlkcid,mplh", "mplh like '%"+parameters.get("query").toString()+"%'"+" and wlid = "+parameters.get("wlid"), null, new Object[]{});
			bundle.put("kcxx", dataset_kcxx.getList());
		}else{
			bundle.put("kcxx", null);
		}
		
	}
	
	/**查询库存全部数据信息
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public void query_kcxx(Parameters parameters, Bundle bundle) {
		String param = " 1=1 ";
		if(null!=parameters.get("kwid")&&!"".equals(parameters.get("kwid"))){
			param+=" and kwid = '"+parameters.get("kwid")+"'";
		}
		if(null!=parameters.get("kfid")&&!"".equals(parameters.get("kfid"))){
			param+=" and kfid = '"+parameters.get("kfid")+"'";
		}
		if(null!=parameters.get("val_tm")&&!"".equals(parameters.get("val_tm"))){
			param+=" and wltm in "+parameters.get("val_tm");
		}
		if(null!=parameters.get("wllb")&&!"".equals(parameters.get("wllb"))){
			param+=" and wllb = '"+parameters.get("wllb")+"'";
		}else{
			param+=" and wllb in( '10','20','30','60' )";
		}
		Dataset dataset_kcxx = Sys.query("kc_sjwlkc"," czsj, wlgg, kcsl, kfid, kwid, wlid, wltm, pcid, mplh, sjwlkcid ", param, null, new Object[]{});
		bundle.put("kcxx", dataset_kcxx.getList());
	}
	/**查询库存全部数据信息
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public void query_kcxx_gz(Parameters parameters, Bundle bundle) {
		String param = " 1=1 ";
		if(null!=parameters.get("kwid")&&!"".equals(parameters.get("kwid"))){
			param+=" and kwid = '"+parameters.get("kwid")+"'";
		}
		if(null!=parameters.get("kfid")&&!"".equals(parameters.get("kfid"))){
			param+=" and kfid = '"+parameters.get("kfid")+"'";
		}
		if(null!=parameters.get("val_wlid")&&!"".equals(parameters.get("val_wlid"))){
			param+=" and wlid in "+parameters.get("val_wlid");
		}
		if(null!=parameters.get("wllb")&&!"".equals(parameters.get("wllb"))){
			param+=" and wllb = '"+parameters.get("wllb")+"'";
		}else{
			param+=" and wllb in( '10','20','30','60' )";
		}
		Dataset dataset_kcxx = Sys.query("kc_sjwlkc"," czsj, wlgg, kcsl, kfid, kwid, wlid, wltm, pcid, mplh, sjwlkcid,wllb ", param, null, new Object[]{});
		bundle.put("kcxx", dataset_kcxx.getList());
	}
	/**查询库存全部数据信息
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public void query_kfxx_by_kfmc(Parameters parameters, Bundle bundle) {
//		Dataset kfxx_dataset = Sys.query("wm_kfxxb", " zzjgid, kfbh, kfmc, kfid, qybsdm, kfwz ", " kfmc like '%"+parameters.getString("kfid")+"'%", null ,  new Object[]{});
		Dataset kfxx_dataset = Sys.query("wm_kfxxb", " kfid, kfbh, kfmc, kfwz, zzjgid, qybsdm ", "qybsdm = '10' " , " kfid desc ",new Object[] {});
		bundle.put("kfxx", kfxx_dataset.getList());
	}
	/**查询库存全部数据信息
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public void query_kwxx_by_kwmc(Parameters parameters, Bundle bundle) {
//		Dataset kfxx_dataset = Sys.query("wm_kfxxb", " zzjgid, kfbh, kfmc, kfid, qybsdm, kfwz ", " kfmc like '%"+parameters.getString("kfid")+"'%", null ,  new Object[]{});
		Dataset kfxx_dataset = Sys.query("wm_kwxxb", " kwid, kwbh, kwmc, kwwz, kfid, qybsdm ",
				"qybsdm = '10' and kfid=?", " kfid desc ", new Object[] { parameters.get("parent") });
		bundle.put("kwxx", kfxx_dataset.getList());
	}
	
	/**查询库存全部数据信息
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public void query_kwxx_by_kwid(Parameters parameters, Bundle bundle) {
		String param = " 1=1 ";
		if(null!=parameters.getString("val_kwid")&&!"".equals(parameters.getString("val_kwid"))){
			param+=" and kwid in "+parameters.getString("val_kwid");
		}
		Dataset kfxx_dataset = Sys.query("wm_kwxxb", " kwid, kwbh, kwmc, kwwz, kfid, qybsdm ",param, null, new Object[] {});
		bundle.put("kwxx", kfxx_dataset.getList());
	}
	/**查询库存全部数据信息
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public void query_kfxx_by_kfid(Parameters parameters, Bundle bundle) {
		String param = " 1=1 ";
		if(null!=parameters.getString("val_kfid")&&!"".equals(parameters.getString("val_kfid"))){
			param+=" and kfid in "+parameters.getString("val_kfid");
		}
		Dataset kfxx_dataset = Sys.query("wm_kfxxb", " zzjgid, kfbh, kfmc, kfid, qybsdm, kfwz ",param, null, new Object[] {});
		bundle.put("kfxx", kfxx_dataset.getList());
	}
	
	public void queryGzxxwltm(Parameters parameters, Bundle bundle) {
		String gzbh = parameters.getString("gzbh");
		Dataset dataset_gzxx = Sys.query("kc_sjwlkc","wlid", "wltm =? ", null, new Object[]{gzbh});
		bundle.put("gzxx",  dataset_gzxx.getList());
	}
	
}
