package jtail.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

public class Value {
	// string constants
	public final static String EMPTY_STRING = "";
	public static final String DIR_CHAR = "/";
	public static final String CUR_DIR_CHAR = ".";
	public static final String COMMAND_DELIMETER = " ; ";
	
	// ui constants
	public final static String OPEN_DIALOG_BUTTON_TEXT = "...";
	public final static String CLOSE_BUTTON_TEXT = "&Close";
	
	// platform independent new line character.
	public final static String NL = System.getProperty("line.separator");
	public final static String NL2 = NL+NL;
	public final static String TAB = "    ";
	public final static String DASH = "-";
	
	// deploy related constants
	public static final String INQUIRY = "inquiry";
	public static final String WEBLOGIC_USER_NAME = "weblogic";
	public static final String DOT_EAR = ".ear";
	public final static String DEFAULT_BUILD_TARGET = "wa";
	public static final String BUILD_PROPERTIES = "build.properties";
	public static final String TARGET_DEPLOYDIR = "target.deployDir";
	public static final String LOCAL_INQUIRY_EAR_FILE_NAME = "inquiry.ear";
	public static final String FILE_UPLOAD_SUCCESS_MSG = "File upload was completed sucessfully.";
	public static final String UNDEPLOY_SUCCESS_MSG = "undeploy completed";
	public static final String DEPLOY_SUCCESS_MSG = "deploy completed";
	public static final String LOCALHOST = "localhost";
		
	// file extension filter for file open dialog.	
	public static final String ALL_FILE_FILTER = "*.*";
	public static final String XML_FILE_FILTER = "*.xml";
	public static final String EXE_FILE_FILTER = "*.exe";
	public static final String BAT_FILE_FILTER = "*.bat";
	public static final String EAR_FILE_FILTER = "*.ear";
	
	
	/**
	 * User's home directory in Unix. 
	 */
	public static final String UNIX_HOME_DIR = "~";	
	
	public static final String PASSWORD_DISPLAY = "xxxxxxxx";
	
	public static boolean isEmptyOrNull(String value){
		return (value == null || value.trim().length() == 0);
	}
	
	public static boolean isArrayEmptyOrNull(Object[] values){
		return (values == null || values.length == 0);
	}
	
	public static String printStackToString(Throwable t){
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		String stackTrace = sw.toString();
		return stackTrace;
	}
	
	public static int intValue(String text) {
		int num = 0;
		try {
			num = Integer.parseInt(text.trim()); 
		} catch (NumberFormatException e){
			 throw e; 
		}
		return num;
	}
	
	public static long longValue(String text) {
		long num = 0;
		try {
			num = Long.parseLong(text.trim()); 
		} catch (NumberFormatException e){
			 throw e; 
		}
		return num;
	}
	
	/**
	 * filePath: D:\\bea\\user_projects\\domains\\Inquiry\\autodeploy
	 * return: D:/bea/user_projects/domains/Inquiry/autodeploy
	 * @param filePath
	 * @return
	 */
	public static String toUnixPath(String filePath){
		return filePath.replace('\\', '/');
	}

    public static boolean isEmpty(String list){
        return (list == null || list.length() == 0);
    }

	public static boolean isEmpty(List<?> list){
		return (list == null || list.size() == 0);
	}
	
	public static boolean isEmpty(Set<?> set){
		return (set == null || set.size() == 0);
	}
	
	public static String getCompactedString(String text, int headingPartSize, int tailPartSize){
		String startingText = "";
		String endingText = "";
		String textPart = "";
		if (text == null){
			textPart = text;
		} else {
			if (text.length() >= headingPartSize){
				startingText = text.substring(0, headingPartSize);
				
				if (text.length() - headingPartSize >= tailPartSize){
					endingText = text.substring(text.length() - tailPartSize, text.length());
					textPart = startingText + " ... "+endingText;
				} else {
					endingText = text.substring(headingPartSize, text.length());
					textPart = startingText + " ... "+endingText;
				}
			} else {
				textPart = text;
			}			
		}
		return textPart;
	}
	
}
