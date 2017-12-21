package guodu.net.warning.function;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendMessage {
    /**
     * 微信发送消息头，参数设置
     * @param mobile 手机号
     * @param todept 可设置为空          
     * @param content 消息内容
     * @return response 国都返回的自定义格式的串
     * */
 	public String postSendSynamicMsg(String mobile, String todept, String content , String url , String user, String password) {
		/* url地址 */
		String URL = url;
		/* 消息参数 */
		String str = "uname=" + user + "&upass=" + password + "&touser="+ mobile + "&todept=" + todept + "&content=" + content;		
		/* 使用post方式发送消息 */
		String response = this.postURL(str, URL);
		/* 返回响应 */
		return response;
	}

	/** post方式 发送url串 */
	/**
	 * @param commString 需要发送的url参数串
	 * @param address 需要发送的url地址
	 * @return rec_string 国都返回的自定义格式的串
	 * @catch Exception
	 */
	public String postURL(String commString, String address) {
		String rec_string = "";
		URL url = null;
		HttpURLConnection urlConn = null;
		try {
			/* 得到url地址的URL类 */
			url = new URL(address);
			/* 获得打开需要发送的url连接 */
			urlConn = (HttpURLConnection) url.openConnection();
			/* 设置连接超时时间 */
			urlConn.setConnectTimeout(30000);
			/* 设置读取响应超时时间 */
			urlConn.setReadTimeout(30000);
			/* 设置post发送方式 */
			urlConn.setRequestMethod("GET");
			/* 发送commString */
			urlConn.setDoOutput(true);
			OutputStream out = urlConn.getOutputStream();
			out.write(commString.getBytes());
			out.flush();
			out.close();
			/* 发送完毕 获取返回流，解析流数据 */
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream(), "GBK"));
			StringBuffer sb = new StringBuffer();
			int ch;
			while ((ch = rd.read()) > -1) {
				sb.append((char) ch);
			}
			rec_string = sb.toString().trim();
			/* 解析完毕关闭输入流 */
			rd.close();
		} catch (Exception e) {
			/* 异常处理 */
			rec_string = "-107";
			System.out.println(e);
		} finally {
			/* 关闭URL连接 */
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}
		/* 返回响应内容 */
		return rec_string;
	}
	
	public static void main(String[] args){
		System.out.println(new SendMessage().postSendSynamicMsg("15117956265" , "" , "ceshiyixia" , "http://218.241.67.222/weixin/SendWX" , "test" , "test" ));
	}
}
