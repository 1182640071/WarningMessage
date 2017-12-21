package guodu.net.warning.function;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendMessage {
    /**
     * ΢�ŷ�����Ϣͷ����������
     * @param mobile �ֻ���
     * @param todept ������Ϊ��          
     * @param content ��Ϣ����
     * @return response �������ص��Զ����ʽ�Ĵ�
     * */
 	public String postSendSynamicMsg(String mobile, String todept, String content , String url , String user, String password) {
		/* url��ַ */
		String URL = url;
		/* ��Ϣ���� */
		String str = "uname=" + user + "&upass=" + password + "&touser="+ mobile + "&todept=" + todept + "&content=" + content;		
		/* ʹ��post��ʽ������Ϣ */
		String response = this.postURL(str, URL);
		/* ������Ӧ */
		return response;
	}

	/** post��ʽ ����url�� */
	/**
	 * @param commString ��Ҫ���͵�url������
	 * @param address ��Ҫ���͵�url��ַ
	 * @return rec_string �������ص��Զ����ʽ�Ĵ�
	 * @catch Exception
	 */
	public String postURL(String commString, String address) {
		String rec_string = "";
		URL url = null;
		HttpURLConnection urlConn = null;
		try {
			/* �õ�url��ַ��URL�� */
			url = new URL(address);
			/* ��ô���Ҫ���͵�url���� */
			urlConn = (HttpURLConnection) url.openConnection();
			/* �������ӳ�ʱʱ�� */
			urlConn.setConnectTimeout(30000);
			/* ���ö�ȡ��Ӧ��ʱʱ�� */
			urlConn.setReadTimeout(30000);
			/* ����post���ͷ�ʽ */
			urlConn.setRequestMethod("GET");
			/* ����commString */
			urlConn.setDoOutput(true);
			OutputStream out = urlConn.getOutputStream();
			out.write(commString.getBytes());
			out.flush();
			out.close();
			/* ������� ��ȡ������������������ */
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream(), "GBK"));
			StringBuffer sb = new StringBuffer();
			int ch;
			while ((ch = rd.read()) > -1) {
				sb.append((char) ch);
			}
			rec_string = sb.toString().trim();
			/* ������Ϲر������� */
			rd.close();
		} catch (Exception e) {
			/* �쳣���� */
			rec_string = "-107";
			System.out.println(e);
		} finally {
			/* �ر�URL���� */
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}
		/* ������Ӧ���� */
		return rec_string;
	}
	
	public static void main(String[] args){
		System.out.println(new SendMessage().postSendSynamicMsg("15117956265" , "" , "ceshiyixia" , "http://218.241.67.222/weixin/SendWX" , "test" , "test" ));
	}
}
