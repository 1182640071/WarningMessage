package guodu.net.warning.util;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ConfigContainer {
	
	private static String userName = null; //�û���
	private static String passWord = null; //����
	private static String url = null; //��ַ
	private static String timeRun = null; //�����ļ����ؼ��
	private static String sqlUpdate = null; //�������
	private static String gd_configure = null; //class����
	private static String gd_mobile_configure = null; //�ֻ�������
	private static String gd_information_configure = null;//��Ϣ��ϸ����
	private static int threadCount = 0;//�̳߳�������߳��� 
	private static String sqlMonitor = null;//����limit_time����state״̬����ֹ�������Ӱ��next_time����
	private static long limt_time = 0; //�߳����г�ʱ����
	private static int port = 0; //�˿ں�
	private static String[] desmobile = null; //����Ա�ֻ���
	private static String sleepTime; //main��������ʱ��
	
	
		/**
		 * �������ṩ�����ļ���Ϣ���ع���
		 * */
     public static void load(){   	 
    	 Map<?,?> map = loadFunction("common");
    	 userName = getInfo("userName","test", map);
    	 passWord = getInfo("passWord","test", map);
    	 url = getInfo("URL","127.0.0.1", map);
    	 timeRun = getInfo("timeRun","60000* *", map);
    	 sleepTime = getInfo("sleepTime","30000", map);
    	 sqlUpdate = getInfo("sqlUpdate","update gd_information_configure set next_time = run_time + interval '''||INTERVAL||''' day to minute where type = '''||type||'''' FROM GD_INFORMATION_CONFIGURE t where type = ", map);
    	 gd_configure = getInfo("gd_configure","select * from gd_configure", map);
    	 gd_mobile_configure = getInfo("gd_mobile_configure","select * from gd_mobile_configure", map);
    	 gd_information_configure = getInfo("gd_information_configure","select * from gd_information_configure where state = '1'", map);
    	 threadCount = Integer.parseInt(getInfo("threadCount","10", map));
    	 limt_time = Long.parseLong(getInfo("limt_time","2", map));
    	 port = Integer.parseInt(getInfo("prot","9988", map));
    	 String str = getInfo("administrator","15117956265,18910319184,18911935592", map);
    	 desmobile = str.split(",");
    	 sqlMonitor = getInfo("sqlMonitor","update gd_information_configure set state = '0' where state = '1' and sysdate > limt_time ; update gd_information_configure set next_time = next_time + 3/24/60 where sysdate > next_time and state = 1 and sysdate < limt_time", map);
    	 Loger.Info_log.info(userName + "," + passWord + "," + url + "," + limt_time + "," + threadCount + "," + sqlUpdate + "," + gd_configure + "," + gd_mobile_configure + "," +  gd_information_configure + "," + sqlMonitor + "," + str);
     }
   
     /**
     * �˷������Ի�ýڵ�����
     * @param e �ڵ�
     * @param defult �ڵ�����Ϊ��ʱ��Ĭ��ֵ
     * @param map
     * @return result ��ѯ���
     * */
     public static String getInfo(String e , String  defult , Map<?,?> map)
     {
    	 String result = (String) map.get(e);
    	 if("".equals(result))
    	 {
    		 result = defult;
    	 }
    	 return result;
     }
     
     /**
      * �˷���ʵ�ֽ��ڵ�������ӽڵ�����Դ�ŵ�map�в�����
      * @param e �ڵ�
      * @return map 
      * */
     public  static Map<?, ?> loadFunction(String e)
     {
    	//����������
   	  SAXReader saxreader = new SAXReader();
   	  
   	  //��ȡ�ĵ�
   	  Document doc = null;
   	  Map<String,String> map = null;
		try {
			doc = saxreader.read(new File("config/config.xml"));
			map = new HashMap<String,String>();
	    	  //��ȡ�����ڵ�
	    	Element root = doc.getRootElement().element(e);
	    	  //�����нڵ�����Դ�ŵ�map��
	    	for ( Iterator<?> iterInner = root.elementIterator(); iterInner.hasNext(); ) {   
	    		Element elementInner = (Element) iterInner.next();
	    	    map.put(elementInner.getName(), root.elementText(elementInner.getName()));
	    	}
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	 return map;
     }
     


	public static String getUserName() {
		return userName;
	}

	public static void setUserName(String userName) {
		ConfigContainer.userName = userName;
	}

	public static String getPassWord() {
		return passWord;
	}

	public static void setPassWord(String passWord) {
		ConfigContainer.passWord = passWord;
	}

	public static String getUrl() {
		return url;
	}

	public static void setUrl(String url) {
		ConfigContainer.url = url;
	}

	public static String getTimeRun() {
		return timeRun;
	}

	public static void setTimeRun(String timeRun) {
		ConfigContainer.timeRun = timeRun;
	}

    
	public static String getSqlUpdate() {
		return sqlUpdate;
	}

	public static void setSqlUpdate(String sqlUpdate) {
		ConfigContainer.sqlUpdate = sqlUpdate;
	}

	public static String getGd_configure() {
		return gd_configure;
	}

	public static void setGd_configure(String gd_configure) {
		ConfigContainer.gd_configure = gd_configure;
	}

	public static String getGd_mobile_configure() {
		return gd_mobile_configure;
	}

	public static void setGd_mobile_configure(String gd_mobile_configure) {
		ConfigContainer.gd_mobile_configure = gd_mobile_configure;
	}

	public static String getGd_information_configure() {
		return gd_information_configure;
	}

	public static void setGd_information_configure(String gd_information_configure) {
		ConfigContainer.gd_information_configure = gd_information_configure;
	}

	 
	public static int getThreadCount() {
		return threadCount;
	}

	public static void setThreadCount(int threadCount) {
		ConfigContainer.threadCount = threadCount;
	}
	
	public static String getSqlMonitor() {
		return sqlMonitor;
	}

	public static void setSqlMonitor(String sqlMonitor) {
		ConfigContainer.sqlMonitor = sqlMonitor;
	}

	public static long getLimt_time() {
		return limt_time;
	}

	public static void setLimt_time(long limt_time) {
		ConfigContainer.limt_time = limt_time;
	}	 
	
	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		ConfigContainer.port = port;
	}

	
	public static String[] getDesmobile() {
		return desmobile;
	}

	public static void setDesmobile(String[] desmobile) {
		ConfigContainer.desmobile = desmobile;
	}
	
	public static String getSleepTime() {
		return sleepTime;
	}

	public static void setSleepTime(String sleepTime) {
		ConfigContainer.sleepTime = sleepTime;
	}

	public static void main(String[] args)
     {
    	 load();
     }
}