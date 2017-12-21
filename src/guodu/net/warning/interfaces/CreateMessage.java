package guodu.net.warning.interfaces;

import java.util.List;

import guodu.net.warning.entity.Gd_information_configure;

public interface CreateMessage {
    /**
	 * 接收配置信息，匹配消息模版后返回
	 * 
	 * @param Gd_information_configure 
	 * 						配置表信息
	 * @return List<String> 
	 * 						信息
	 * */
	 public List<String> getMessage(Gd_information_configure A);
	 
}
