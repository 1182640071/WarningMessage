package guodu.net.warning.interfaces;

import java.util.List;

import guodu.net.warning.entity.Gd_information_configure;

public interface CreateMessage {
    /**
	 * ����������Ϣ��ƥ����Ϣģ��󷵻�
	 * 
	 * @param Gd_information_configure 
	 * 						���ñ���Ϣ
	 * @return List<String> 
	 * 						��Ϣ
	 * */
	 public List<String> getMessage(Gd_information_configure A);
	 
}
