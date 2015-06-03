package jtail.log;

import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.Callable;

import jtail.resources.Resource;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class MyLogger {
	private static boolean enableDebugLog = false;
	
	private static boolean enableErrorLog = true;
	
	static {
		Properties p = new Properties();
		try {
			p.load(Resource.getResourceStream(Resource.LOG4J_PROPERTIES));
		} catch (Exception e) {
			System.err.println("can't load log4j.properties file.");
			e.printStackTrace();
		}
		PropertyConfigurator.configure(p);
	}

	public static void debug(Object message) {	
		if (MyLogger.enableDebugLog){
			Logger.getRootLogger().debug(message);
		}
	}

	public static void debug(Object message, Throwable t) {
		if (MyLogger.enableDebugLog){
			Logger.getRootLogger().debug(message, t);
		}
	}
	
	public static void error(Object message) {	
		if (MyLogger.enableErrorLog){
			Logger.getRootLogger().error(message);
		}
	}

	public static void error(Object message, Throwable t) {
		if (MyLogger.enableErrorLog){
			Logger.getRootLogger().error(message, t);
		}
	}
	
	public static void executeAndLogDuration(Runnable r, String description){
		long startTime = System.currentTimeMillis();
		r.run();
		long endTime = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(endTime - startTime);		
		debug("Duration for "+description+":"+
				(cal.get(Calendar.MINUTE))+" minutes "+
				(cal.get(Calendar.SECOND))+" secodes ");		
	}
	
	public static <T> T executeAndLogDuration(Callable<T> r, String description){
		long startTime = System.currentTimeMillis();
		T result = null;
		try {
			result = r.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(endTime - startTime);		
		debug("Duration for "+description+":"+
				(cal.get(Calendar.MINUTE))+" minutes "+
				(cal.get(Calendar.SECOND))+" secodes ");
		return result;
	}

	public static void enableDebugLog() {
		enableDebugLog = true;
	}

	public static void disableDebugLog() {
		enableDebugLog = false;
	}

	public static void enableErrorLog() {
		enableErrorLog = true;
	}

	public static void disableErrorLog() {
		enableErrorLog = false;
	}
	 
}
