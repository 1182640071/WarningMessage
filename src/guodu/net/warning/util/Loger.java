package guodu.net.warning.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Loger {

	
	static {
		PropertyConfigurator.configure("config/Log4j.properties");
	}
	
	public static final Logger Info_log = Logger.getLogger("message");

//	public static final Logger Reader_log = Logger.getLogger("readerinfo");




}
