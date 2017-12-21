package guodu.net.warning.function;

import guodu.net.warning.util.ConfigContainer;
import guodu.net.warning.util.Loger;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SendMessageSocket {
	/**
	 * ������Ϣ��socket�����
	 * @param content ����
	 * @param modile �ֻ���
	 * @param limt ��ֵ
	 * @param oper �����
	 * @param warming_type ������ʽ
	 * @param yw_type ҵ������
	 * @return ecode ������Ӧ
	 * */
   public int sendSocket(String content , String mobile , String limt , String oper , String warming_type , String yw_type){
	   PrintStream out = null;
	   DataInputStream in = null;
	   int res = -2;
	   String message = content + "!|" + mobile + "!|" + limt + "!|" + oper + "!|" + warming_type + "!|" + yw_type;
	   //������ϢΪ��[20160126]�գ���[20160202]�գ��û��ظ�������TD��Ϊ��{199},15117956265,>,100,+,1,������ֵ����
		/**
		 * ����socket����
		 * */
		Socket client;
		try {
			client = new Socket(ConfigContainer.getUrl(),ConfigContainer.getPort());
			//�����˷�����Ϣ
			out = new PrintStream(client.getOutputStream());
			out.println(message);	
			//���շ���˷�����Ϣ
		    in  =  new DataInputStream(client.getInputStream());
		    res = in.read();
		} catch (UnknownHostException e) {
			Loger.Info_log.info("[ERROR]Socket����ʧ��!");
			e.printStackTrace();
		} catch (IOException e) {
			Loger.Info_log.info("[ERROR]Socket����ʧ��!");
			e.printStackTrace();
		}//192.168.168.188  //127.0.0.1
	    return res;
   }
}
