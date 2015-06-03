package jtail.resources;

import jtail.log.MyLogger;
import jtail.util.Value;

import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public final class Resource {
	// use ResouceBundle mechanism to get locale specific message.
	public static final String RESOURCE_BUNDLE_BASE_NAME = "jtail.resources.properties.ApplicationResources";

	// regular resources
	public static final String LOG4J_PROPERTIES = "properties/log4j.properties";
	public static final String FILTER_PROPERTIES = "properties/filter.properties";
	public static final String MAIN_IMAGE_PATH = "images/main.ico";
	public static final String EXIT_IMAGE_PATH = "images/exit.ico";	
	public static final String OPEN_IMAGE_PATH = "images/open.ico";
	public static final String CLOSE_IMAGE_PATH = "images/close.ico";
	public static final String COPY_IMAGE_PATH = "images/copy.ico";
	public static final String CLEAR_IMAGE_PATH = "images/clear.png";
	public static final String RELOAD_IMAGE_PATH = "images/reload.ico";
	public static final String UP_ARROW_IMAGE_PATH = "images/up.gif";
	public static final String SEARCH_BACKWARD_IMAGE_PATH = "images/searchBackward.ico";
	public static final String SEARCH_FORWARD_IMAGE_PATH = "images/searchForward.ico";
	public static final String SELECT_ALL_IMAGE_PATH = "images/selectAll.ico";
	public static final String FILTER_IMAGE_PATH = "images/filter.ico";
	public static final String OPTIONS_IMAGE_PATH = "images/options.png";
	public static final String RESUME_TAIL_IMAGE_PATH = "images/start.png";
	public static final String PAUSE_TAIL_IMAGE_PATH = "images/pause.png";

	public static URL getResourceURL(String resourceName) {
		URL url = Resource.class.getResource(resourceName);
		return url;
	}

	public static InputStream getResourceStream(String resourceName) {
//		InputStream is = Resource.class.getResourceAsStream(resourceName);
        InputStream is = Resource.class.getClassLoader().getResourceAsStream(resourceName);
		return is;
	}
	
	private static Locale getLocale(){
		Locale locale = Locale.getDefault();
		return locale;
	}
	
	public static String getString(String key) {
		Locale locale = getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(Resource.RESOURCE_BUNDLE_BASE_NAME, locale);
		if (bundle == null) {
			MyLogger.debug("ERROR: Failed to obtain a ResourceBundle");
			return Value.EMPTY_STRING;
		} else {
			String message = "";
			try {
				message = bundle.getString(key); 
			} catch (Exception e){
				message = key;
				MyLogger.debug("ERROR: bundle.getString failed for the key:"+key, e);				
			}
			return message;
		}
	}
}
