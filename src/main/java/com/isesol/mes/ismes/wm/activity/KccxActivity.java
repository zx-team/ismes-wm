package com.isesol.mes.ismes.wm.activity;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.isesol.ismes.platform.core.service.bean.Dataset;
import com.isesol.ismes.platform.module.Bundle;
import com.isesol.ismes.platform.module.Parameters;
import com.isesol.ismes.platform.module.Sys;
import com.isesol.ismes.platform.module.bean.File;

public class KccxActivity {
	
	public String query_kccx(Parameters parameters, Bundle bundle){	
		return "kcxx";
	}
	
	public String lj_table(Parameters parameters, Bundle bundle) throws ParseException{
		query_lj(parameters, bundle, 0);
		return "json:";
	}
	
	public String wl_table(Parameters parameters, Bundle bundle) throws ParseException{
		query_wl(parameters, bundle, 0);
		return "json:";
	}
	
	public String lj_export(Parameters parameters, Bundle bundle) throws ParseException{
		query_lj(parameters, bundle, 1);
		List<Map<String, Object>> list = (List<Map<String, Object>>)bundle.get("rows");
		
		for(int i = 0, len = list.size(); i < len; i++){
			if("10".equals(list.get(i).get("kczt") + "")){
				list.get(i).put("kczt", "在库");
			} else {
				list.get(i).put("kczt", "出库");
			}
		}
		
		Map<String, String> setting = new HashMap<String, String>();
		setting.put("cellTitle", "零件图号,零件名称,产品规格,库存状态,库存数量,库存天数,入库时间,入库数量,出库时间,出库数量,客户单号");
		setting.put("cellIndex", "ljbh,ljmc,clbh,kczt,kcsl,kcts,rksj,rksl,cksj,cksl,ckkhdh");

		File file = export(list, setting, "零件库存信息");

		bundle.put("data", file);
		return "file:data";
	}
	
	public String wl_export(Parameters parameters, Bundle bundle) throws ParseException{
		query_wl(parameters, bundle, 1);
		List<Map<String, Object>> list = (List<Map<String, Object>>)bundle.get("rows");
		
		for(int i = 0, len = list.size(); i < len; i++){
			if("10".equals(list.get(i).get("kczt") + "")){
				list.get(i).put("kczt", "在库");
			} else {
				list.get(i).put("kczt", "出库");
			}
		}
		Map<String, String> setting = new HashMap<String, String>();
		setting.put("cellTitle", "物料编号,件号,物料名称,产品规格,库存状态,库存数量,库存天数,入库时间,入库数量,出库时间,出库数量");
		setting.put("cellIndex", "wlbh,jh,wlmc,wlgg,kczt,kcsl,kcts,rksj,rksl,cksj,cksl");

		File file = export(list, setting, "物料库存信息");

		bundle.put("data", file);
		
		return "file:data";
	}
	
	public File export(List<Map<String, Object>> list, Map<String, String> setting, String fileName){
		InputStream result = Sys.exportFile("xlsx", null, list, setting);
		File file = null;
		try {
			file = new File(fileName+getDate()+".xlsx", null, result, "xlsx", Long.valueOf(String.valueOf(result.available())));
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
			throw new NumberFormatException();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return file;
	}
	
	public void query_lj(Parameters parameters, Bundle bundle, int flag) throws ParseException{
		
		int page = parameters.get("page") == null ? 1 : parameters.getInteger("page");
		int pageSize = parameters.get("pageSize") == null ? 10 : parameters.getInteger("pageSize");
		
		if(flag == 1){
			pageSize = Integer.MAX_VALUE;
		}
		
		String date_rkbegin = parameters.getString("date_rkbegin");//入库时间begin
		String date_rkend = parameters.getString("date_rkend");//入库时间end
		String date_ckbegin = parameters.getString("date_ckbegin");//出库时间begin
		String date_ckend = parameters.getString("date_ckend");//出库时间end
		
		StringBuffer con = new StringBuffer();
		con.append("1 = 1");
		List<Object> val = new ArrayList<Object>();
		
		if(StringUtils.isNotBlank(date_rkbegin)) 
		{
			con.append(" and rksj > " + "'"+date_rkbegin+"'");
		}
		if(StringUtils.isNotBlank(date_rkend)) 
		{
			con.append(" and rksj < " + "'"+date_rkend+"'");
		}
		if(StringUtils.isNotBlank(date_ckbegin)) 
		{
			con.append(" and cksj > " + "'"+date_ckbegin+"'");
		}
		if(StringUtils.isNotBlank(date_ckend)) 
		{
			con.append(" and cksj < " + "'"+date_ckend+"'");
		}
		
		Dataset kcmxSet = Sys.query("lj_kcmx", "kcmxid,kcid,ljid,kfid,kwid,kczt,rksj,rksl,ckkhdh,"
				+ "cksj,cksl,xh,scph,mplh,zksj", con.toString(), null, (page - 1) * pageSize, pageSize, new Object[]{});
		
		List<Map<String, Object>>  kcmx_list = kcmxSet.getList();
		
		StringBuffer ljids = new StringBuffer();
		
		ljids.append("(");
		
		for(int i = 0, len = kcmx_list.size(); i < len; i++){
			ljids.append(kcmx_list.get(i).get("ljid"));
			if(i != (len - 1)){
				ljids.append(",");
			}
		}
		if(kcmx_list.size() == 0){
			ljids.append(" -1 ");
		}
		ljids.append(")");
		
		parameters.set("val_lj", ljids.toString());
		
		Bundle ljxx_bundle = Sys.callModuleService("pm", "pmservice_ljxx", parameters);
		
		List<Map<String, Object>> ljxx_list = (List<Map<String, Object>>) ljxx_bundle.get("ljxx");
		
		for(int i = 0, len = kcmx_list.size(); i < len; i++){
			Map<String, Object> kcmx_map = kcmx_list.get(i);
			for(int j = 0, _len = ljxx_list.size(); j < _len; j++){				
				Map<String, Object> ljxx_map = ljxx_list.get(j);
				
				if(kcmx_map.get("ljid").equals(ljxx_map.get("ljid"))){
					kcmx_map.put("ljbh", ljxx_map.get("ljbh"));
					kcmx_map.put("ljmc", ljxx_map.get("ljmc"));
					kcmx_map.put("clbh", ljxx_map.get("clbh"));
					break;
				}
			}
			String rksj = (kcmx_map.get("rksj")+"").split("\\.")[0];
			kcmx_map.put("rksj", rksj);
			String cksj = kcmx_map.get("cksj") == null?"":(kcmx_map.get("cksj")+"");
			kcmx_map.put("cksj", cksj.indexOf(".")>0?cksj.split("\\.")[0]:"".equals(cksj.split("\\.")[0])?"无":cksj.split("\\.")[0]);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        if("".equals(cksj)){
	        	cksj = sdf.format(new Date());
	        	kcmx_map.put("ckkhdh", "无");
	        }
			kcmx_map.put("kcts", getDays(sdf.format(sdf.parse(rksj)), cksj));
			kcmx_map.put("cksl", kcmx_map.get("cksl")==null?"0":kcmx_map.get("cksl"));
			
			BigDecimal rksl = new BigDecimal(kcmx_map.get("rksl")+"");
			BigDecimal cksl = new BigDecimal(kcmx_map.get("cksl")==null?"0":kcmx_map.get("cksl")+"");
			
			kcmx_map.put("kcsl", rksl.subtract(cksl));
		}
		int totalPage = kcmxSet.getTotal()%pageSize==0?kcmxSet.getTotal()/pageSize:kcmxSet.getTotal()/pageSize+1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", kcmxSet.getTotal());
		bundle.put("rows", kcmx_list);
	}
	
	public String query_wl(Parameters parameters, Bundle bundle, int flag) throws ParseException{
		
		int page = parameters.get("page") == null ? 1 : parameters.getInteger("page");
		int pageSize = parameters.get("pageSize") == null ? 10 : parameters.getInteger("pageSize");
		
		if(flag == 1){
			pageSize = Integer.MAX_VALUE;
		}
		
		String date_rkbegin = parameters.getString("date_rkbegin");//入库时间begin
		String date_rkend = parameters.getString("date_rkend");//入库时间end
		String date_ckbegin = parameters.getString("date_ckbegin");//出库时间begin
		String date_ckend = parameters.getString("date_ckend");//出库时间end
		
		StringBuffer con = new StringBuffer();
		
		if(StringUtils.isNotBlank(date_rkbegin)) 
		{
			con.append(" and rksj > " + "'"+date_rkbegin+"'");
		}
		if(StringUtils.isNotBlank(date_rkend)) 
		{
			con.append(" and rksj < " + "'"+date_rkend+"'");
		}
		if(StringUtils.isNotBlank(date_ckbegin)) 
		{
			con.append(" and cksj > " + "'"+date_ckbegin+"'");
		}
		if(StringUtils.isNotBlank(date_ckend)) 
		{
			con.append(" and cksj < " + "'"+date_ckend+"'");
		}
		
		String wllb = "\'40\', \'50\'";
		
		parameters.set("query_wllbdm_ids", wllb);
		
		Bundle wlxx_bundle = Sys.callModuleService("mm", "mmservice_wlxxkc", parameters);
		
		List<Map<String, Object>> wlxx_list = (List<Map<String, Object>>) wlxx_bundle.get("wlxx");
		
		if(wlxx_list.size() == 0){
			bundle.put("rows", new ArrayList<Map<String, Object>>());
			return "json";
		}
		
		StringBuffer wlids = new StringBuffer();
		wlids.append("(");
		for(int i = 0, len = wlxx_list.size(); i < len; i++){
			wlids.append("'"+wlxx_list.get(i).get("wlid")+"'");
			
			if(i != (len - 1)){
				wlids.append(",");
			}
		}
		wlids.append(")");
		
		Dataset kcxx = Sys.query("kc_wlkcmx","jh,kczt,rksj,rksl,cksj,cksl,wlid",
				"(wllb = '40' or wllb = '50') and wlid in "+ wlids.toString() + con, null, (page - 1) * pageSize, pageSize,new Object[]{});
		
		List<Map<String, Object>> kcxx_list = kcxx.getList();
		
		for(int i = 0, len = kcxx_list.size(); i < len; i++){
			for(int j = 0, _len = wlxx_list.size(); j < _len; j++){
				Map<String, Object> kcxx_map = kcxx_list.get(i);
				Map<String, Object> wlxx_map = wlxx_list.get(j);				
				if(kcxx_map.get("wlid").toString().equals(wlxx_map.get("wlid").toString())){					
					String rksj = (kcxx_map.get("rksj")+"").split("\\.")[0];
					String cksj = (kcxx_map.get("cksj")+"").split("\\.")[0];
					kcxx_map.put("rksj", rksj);
					kcxx_map.put("cksj", "null".equals(cksj)?"无":cksj);
					kcxx_map.put("wlbh", wlxx_map.get("wlbh"));
					kcxx_map.put("wlmc", wlxx_map.get("wlmc"));
					kcxx_map.put("wlgg", wlxx_map.get("wlgg"));
					BigDecimal rksl = new BigDecimal(kcxx_map.get("rksl")+"");
					BigDecimal cksl = new BigDecimal(kcxx_map.get("cksl")+"");
					kcxx_map.put("kcsl", rksl.subtract(cksl));
					String _cksj = "";
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			        if("10".equals(kcxx_map.get("kczt")+"")){
			        	_cksj = sdf.format(new Date());
			        } else {
			        	_cksj = cksj;
			        }
			        kcxx_map.put("kcts", getDays(sdf.format(sdf.parse(rksj)), _cksj));
					break;
				}
			}
		}
		int totalPage = kcxx.getTotal()%pageSize==0?kcxx.getTotal()/pageSize:kcxx.getTotal()/pageSize+1;
		bundle.put("totalPage", totalPage);
		bundle.put("currentPage", page);
		bundle.put("totalRecord", kcxx.getTotal());
		bundle.put("rows", kcxx_list);
		
		return "json:";
	}
	
	public String getDate(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sdf.format(new Date(Long.parseLong(String.valueOf(time))));	
		return date;
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
		
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmss");
		
		return sdf.format(new Date());
	}

}
