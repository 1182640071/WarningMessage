package guodu.net.warning.function;

import java.util.ArrayList;
import java.util.List;

import guodu.net.warning.db.LoadConfigDb;
import guodu.net.warning.entity.Gd_information_configure;
import guodu.net.warning.interfaces.CreateMessageEx;
import guodu.net.warning.util.ConfigDb;
import guodu.net.warning.util.Loger;

public class CreateMessageImp extends CreateMessageEx{
	/**
	 * 接收配置信息，匹配消息模版后返回
	 * 
	 * @param Gd_information_configure 
	 * 							配置表信息
	 * @return List<String>
	 * 							信息
	 * */
	@Override
	public List<String> getMessage(Gd_information_configure A){
    	Object[] list = null;
    	Object[] result = null;
    	List<String> listrs = new ArrayList<String>();
    	String sql[] = A.getSql().split("!\\|");
    	for(int i = 0 ; i < sql.length ; i ++){
    		try {
				list = LoadConfigDb.loadConfig(sql[i] ,  A.getDb()).get(0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Loger.Info_log.info("[ERROR]sql语句执行失败，class_id：" + A.getId());
				listrs.add(ConfigDb.getMapConfig().get(A.getClass_id()).getModel());
				return listrs;
			}
    		if(list == null || list.length == 0){
    			continue;
    		}else if(result == null || result.length == 0){
    			result = list;
    		}else{
        		result = concat(result , list);   			
    		}
    	}
    	String rs = addInfo(result , ConfigDb.getMapConfig().get(A.getClass_id()).getModel());
    	listrs.add(rs);
    	return listrs;
    }
	
}
