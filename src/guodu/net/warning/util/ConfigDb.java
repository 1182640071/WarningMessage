package guodu.net.warning.util;

import guodu.net.warning.db.LoadConfigDb;
import guodu.net.warning.entity.Gd_information_configure;
import guodu.net.warning.entity.Gd_warning_class;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigDb {
    private static Map<String , Gd_warning_class> mapConfig = null; //class文件配置表
    private static List<Gd_information_configure> listConfig= null; //信息配置表
    private static Map<String , Boolean> mapStuts = new HashMap<String , Boolean>();
    /**
     * 根据不同的表加载数据库中配置信息
     * */
    public  static void load(){
    	mapConfig = loadFunction(ConfigContainer.getGd_configure() , "defult");
    	listConfig = loadListFunction(ConfigContainer.getGd_information_configure() , "defult");
    }
    
    /**
     * 通过表来加载其中的数据
     * @param form 配置表gd_information_configure
     * @return list
     * */
    public static  List<Gd_information_configure> loadListFunction(String form , String db){
    	List<Object[]> list = LoadConfigDb.loadConfig(form , db);
        List<Gd_information_configure> result = changeList(list);
    	return result;
    }
    /**
     * 将配置表中的信息和gd_information_configure类进行字段匹配
     * */
	public static  List<Gd_information_configure> changeList(List<Object[]> list){
    	List<Gd_information_configure> result = new ArrayList<Gd_information_configure>();
        try {
			for(Object[] o : list){
				Gd_information_configure x = new Gd_information_configure();
				x.setId(o[0].toString());
				x.setClass_id(o[1].toString());
				x.setRun_time(o[2].toString());
				x.setInterval(o[3].toString());
				x.setNext_time(o[4].toString());
				x.setLimt_time(o[5].toString());
				x.setState(o[6].toString());
                x.setSql(o[7].toString());
                x.setWarning_type(o[8].toString());
				x.setDb(o[9].toString());
				x.setStart_time(o[10].toString());
				x.setEnd_time(o[11].toString());
				result.add(x);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]Gd_information_configure类加载失败");
		}
    	return result;
    }
    
    
    /**
     * 通过表来加载其中的数据
     * @param form 配置表
     * @return map
     * */
    public static  Map<String , Gd_warning_class> loadFunction(String form , String db){
    	List<Object[]> list = LoadConfigDb.loadConfig(form , db);
    	Map<String , Gd_warning_class> map = null;
    	if(list == null || list.size() == 0){
    		return null;
    	}else{
        	map = changMap(list);
    	}
    	return map;
    }
    /**
     * 将list中的数据以map形式存储并返回
     * */
    public static Map<String , Gd_warning_class> changMap(List<Object[]> list){
    	Map<String , Gd_warning_class> map = new HashMap<String , Gd_warning_class>();
    	for(Object[] o : list){
    		map.put(o[0].toString(), changeLi(o));
    	}
    	return map;
    }
    
    
    /**
     * 将配置表中的信息和Gd_warning_class类进行字段匹配
     * */
	public static  Gd_warning_class changeLi(Object[] o){
		Gd_warning_class x = new Gd_warning_class();
        try {
				x.setId(o[0].toString());
				x.setClass_type(o[1].toString());
				x.setClass_file(o[2].toString());
				x.setModel(o[3].toString());
				x.setTime_diff(o[4].toString());
				x.setLimt_count(o[5].toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]Gd_warning_class类加载失败");
		}
    	return x;
    }

	public static Map<String, Gd_warning_class> getMapConfig() {
		return mapConfig;
	}

	public static void setMapConfig(Map<String, Gd_warning_class> mapConfig) {
		ConfigDb.mapConfig = mapConfig;
	}

//	public static Map<String, String> getMapMobileConfig() {
//		return mapMobileConfig;
//	}
//
//	public static void setMapMobileConfig(Map<String, String> mapMobileConfig) {
//		ConfigDb.mapMobileConfig = mapMobileConfig;
//	}
	
    public static List<Gd_information_configure> getListConfig() {
		return listConfig;
	}

	public static void setListConfig(List<Gd_information_configure> listConfig) {
		ConfigDb.listConfig = listConfig;
	}

	public static Map<String, Boolean> getMapStuts() {
		return mapStuts;
	}

	public static void setMapStuts(Map<String, Boolean> mapStuts) {
		ConfigDb.mapStuts = mapStuts;
	}

	public static void main(String[] args){
		ConfigContainer.load();
        load();
    	System.out.println("******" + mapConfig.get("1").getModel());
    }

}
