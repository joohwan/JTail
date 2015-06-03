package jtail.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import jtail.log.MyLogger;
import jtail.resources.Resource;

public final class MessageBuilder {
	public final static String ERROR_MESSAGE_HEADER = "There is a problem. Click on 'Details' button and "
			+ "copy and paste the line "
			+ "into text editor to see the details." + Value.NL;
	public static final String CHECK_LOG_MSG = Value.NL + "Please check the log.";
	public static final String VALIDATION_ERROR_MSG = "Input data is not valid. Please check the followings and try again."
			+ Value.NL2 + "{0}";
	public static final String SAVE_SETTING_ERROR_MSG = "Save failed because of the following reason."
			+ Value.NL2 + "{0}";
	public static final String SAVE_SUCCESS_MSG = "Data saved successfully!";
	public static final String DELETE_SUCCESS_MSG = "Data deleted successfully!";
	public static final String NO_DATA_TO_RENAME_MSG = "No data to rename!";

	private static Locale getLocale(){
		Locale locale = Locale.getDefault();
		return locale;
	}
	
	public static String getMessage(String key) {
		Locale locale = getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(Resource.RESOURCE_BUNDLE_BASE_NAME, locale);
		if (bundle == null) {
			MyLogger.debug("ERROR: Failed to obtain a ResourceBundle");
			return Value.EMPTY_STRING;
		} else {
			String message = bundle.getString(key); 
			return message;
		}
	}

	public static String getMessage(String key, Object[] arguments) {
		String pattern = getMessage(key);
		MessageFormat messageFormat = new MessageFormat("");
		Locale locale = getLocale();
		messageFormat.setLocale(locale);
		messageFormat.applyPattern(pattern);
		String message = messageFormat.format(arguments);
		return message;
	}

}
