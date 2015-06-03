package jtail.config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.SortedSet;
import java.util.TreeSet;

import jtail.log.MyLogger;
import jtail.logic.filter.Filter;
import jtail.logic.filter.FilterType;
import jtail.util.FileUtil;
import jtail.util.Value;

import com.thoughtworks.xstream.XStream;

public class Config {
	public final static String USER_DIR = System.getProperty("user.dir");
	private static final String CONFIG_FILE_PATH = USER_DIR + Value.DIR_CHAR + "config.xml";
	private final static File configFile = new File(CONFIG_FILE_PATH);
	public static XMLConfigBean bean;	

	static {		
		try {
			MyLogger.debug("user.dir: " + USER_DIR);
			MyLogger.debug("config file path: " + CONFIG_FILE_PATH);

			// if config file was deleted, create new one.
			if (!configFile.exists()) {
				configFile.createNewFile();
			}
			
			/*
			 * If this is very first time to launch this application, there is no existing
			 * configuration file. In this case decoder.readObject() throws an exception. To prevent
			 * an exception, create empty configuration bean and set first open flag true.
			 */
			if (Value.isEmpty(FileUtil.readFile(configFile))) {
				bean = new XMLConfigBean();
				bean.setFirstOpen(true);
			} else {
				XStream xstream = new XStream();
				bean = (XMLConfigBean) xstream.fromXML(new BufferedInputStream(new FileInputStream(
						configFile)));
			}
		} catch (Throwable t) {
			MyLogger.debug("can't load " + CONFIG_FILE_PATH, t);
		}
	}

	public static void saveBean() {
		XStream xstream = new XStream();
		try {
//			SortedSet<Filter> filterSet = new TreeSet<Filter>();
//			Filter filter = new Filter("joEmergis1", FilterType.INCLUDE, "joEmergis1");
//			filterSet.add(filter);
//			bean.setFilterSet(filterSet);
			xstream.toXML(bean, new BufferedOutputStream(new FileOutputStream(configFile)));
		} catch (FileNotFoundException e) {
			MyLogger.debug("can't save " + CONFIG_FILE_PATH, e);
		}
	}
}
