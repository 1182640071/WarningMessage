package guodu.net.warning.interfaces;

import java.util.List;

import guodu.net.warning.db.LoadConfigDb;
import guodu.net.warning.entity.Gd_information_configure;
import guodu.net.warning.function.CreateMessageImp;
import guodu.net.warning.util.ConfigContainer;
import guodu.net.warning.util.Loger;


public class HanderAchieveOne implements Hander{
	/**
	 * 创建无参构造，newInstance方法必需
	 * */
	public HanderAchieveOne(){	
		
	}
	/**
	 * 得到 并 发送 报警信息
	 * @param Gd_information_configure 配置表信息
	 * */
	public void exe(Gd_information_configure A) {
		String message = null;
		List<Object[]> desmobile = null;
		CreateMessage cm = new CreateMessageImp();
		try {
			List<String> rs = cm.getMessage(A);
			message = rs.get(0);
			String sql  = ConfigContainer.getGd_mobile_configure() + " where id = '"+ A.getClass_id() + "'";
			desmobile = LoadConfigDb.loadConfig(sql, "defult");
            for(Object[] o : desmobile){
				int ecode = LoadConfigDb.Save(o[0].toString().trim(),  message ,A , "defult");
				if(ecode == 0){
					Loger.Info_log.info("[INFO]提交手机号：" + o[0].toString().trim() + "。 提交消息" + message);
				}else{
					Loger.Info_log.info("[ERROR]提交手机号：" + o[0].toString().trim() + "。 提交消息" + message +" 失败！");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]微信发送失败,失败类型：" + A.getId() );
		}	
		try {
			//报警信息发送完毕，更新下一次执行时间
			LoadConfigDb.updateForm(A);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Loger.Info_log.info("[ERROR]next_time更新失败，类型：" + A.getId() );
		}
	}
}