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
	 * 发送信息至socket服务端
	 * @param content 内容
	 * @param modile 手机号
	 * @param limt 阀值
	 * @param oper 运算符
	 * @param warming_type 报警方式
	 * @param yw_type 业务类型
	 * @return ecode 返回响应
	 * */
   public int sendSocket(String content , String mobile , String limt , String oper , String warming_type , String yw_type){
	   PrintStream out = null;
	   DataInputStream in = null;
	   int res = -2;
	   String message = content + "!|" + mobile + "!|" + limt + "!|" + oper + "!|" + warming_type + "!|" + yw_type;
	   //接收信息为：[20160126]日，至[20160202]日，用户回复的上行TD数为：{199},15117956265,>,100,+,1,建行阈值报警
		/**
		 * 建立socket连接
		 * */
		Socket client;
		try {
			client = new Socket(ConfigContainer.getUrl(),ConfigContainer.getPort());
			//向服务端发送信息
			out = new PrintStream(client.getOutputStream());
			out.println(message);	
			//接收服务端返回信息
		    in  =  new DataInputStream(client.getInputStream());
		    res = in.read();
		} catch (UnknownHostException e) {
			Loger.Info_log.info("[ERROR]Socket连接失败!");
			e.printStackTrace();
		} catch (IOException e) {
			Loger.Info_log.info("[ERROR]Socket连接失败!");
			e.printStackTrace();
		}//192.168.168.188  //127.0.0.1
	    return res;
   }
}
