package guodu.net.warning.interfaces;

import java.util.List;

import guodu.net.warning.db.LoadConfigDb;
import guodu.net.warning.entity.Gd_information_configure;
import guodu.net.warning.function.CreateMessagesImp;
import guodu.net.warning.function.GetMessage;
import guodu.net.warning.util.ConfigContainer;
import guodu.net.warning.util.ConfigDb;
import guodu.net.warning.util.JudgeLimt;
import guodu.net.warning.util.Loger;

public class HanderAchieveFour implements Hander{
	/**
	 * 创建无参构造，newInstance方法必需
	 * */
	public HanderAchieveFour(){	
		
	}
	@Override
	public void exe(Gd_information_configure A) {
		JudgeLimt JL = JudgeLimt.getInstance();
		CreateMessage cm = new CreateMessagesImp();
		List<String> messages = cm.getMessage(A); //填补信息模版
		for(String mes : messages)
		{
			Boolean flag = JL.getCount(mes , ConfigDb.getMapConfig().get(A.getClass_id()));//判断信息是否需要发送
			if(flag){
				String sql  = ConfigContainer.getGd_mobile_configure() + " where id = '"+ A.getClass_id() + "'";
				List<Object[]> desmobile = LoadConfigDb.loadConfig(sql, "defult"); //获取信息对应的手机号
		        for(Object[] o : desmobile){
					int ecode = LoadConfigDb.Save(o[0].toString().trim(),  mes ,A , "defult");
					if(ecode == 0){
						Loger.Info_log.info("[INFO]提交手机号：" + o[0].toString().trim() + "。 提交消息" + mes);
					}else{
						Loger.Info_log.info("[ERROR]提交手机号：" + o[0].toString().trim() + "。 提交消息" + mes +" 失败！");
					}
				}	
			}	
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
