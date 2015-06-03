package jtail.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jtail.execute.CommandResult;
import jtail.logic.Range;


public class RegexUtil {
	public static final String ANY = ".*";
	public static final String ANYM = ".+";
	public static final String WS = "\\s*"; // white space
	public static final String WSM = "\\s+"; // mandatory white space
	public static final String WB = "\\b"; // word boundary
	public static final String BOL = "^"; // beginning of a line
	public static final String EOL = "$"; // end of a line
	public static final String NL = "(\\r)?\\n"; // end of a line
	public static final String INQUIRY_BUILD_NO_REGEX = "(\\d{3}[.]\\d{3}[.]\\d{3})";
	public static final String INQUIRY_DEPLOYMENT_NAME_LINE_REGEX = BOL + ANY + Value.INQUIRY
			+ INQUIRY_BUILD_NO_REGEX + ANY + EOL;
	public static final String INQUIRY_EAR_FILE_REGEX = BOL + WS + Value.INQUIRY + ".*[.]ear";
	public static final String INQUIRY_DEPLOYMENT_NAME_REGEX = WB + Value.INQUIRY
			+ INQUIRY_BUILD_NO_REGEX;
	public static final String INQUIRY_BUILD_NO_FILE_NAME_REGEX = "ApplicationResources(_\\w+)?.properties";

	/*
	 * English: application.build=Multiple Benefit 003.002.014. 
	 * French: application.build=Garanties multiples 003.002.014. 
	 * don't include last dot.
	 */
	public static final String INQUIRY_BUILD_NO_LINE_IN_PROPERTIES_FILE_REGEX = BOL
			+ "(application[.]build" + WS + "=[\\s\\w]+)" + RegexUtil.INQUIRY_BUILD_NO_REGEX;
	public static final String ANT_BUILD_SUCCESS_MSG_REGEX = WB + "BUILD SUCCESSFUL" + WB;

	// -rw-r--r--   1 weblogic weblogic 52841571 Aug 18 17:19 inquiry003.002.019.ear
	public static final String LS_L_LINE_REGEX = "[rwx-]{10}.+";

	public static final String FILE_UPLOAD_HIGHLIGHT_MSG_REGEX = "("
			+ Value.FILE_UPLOAD_SUCCESS_MSG + "|" + LS_L_LINE_REGEX + ")";
	public static final String SUDO_FAIL_MSG_REGEX = WB + "(?i:Sorry)" + WB;
	public static final String LS_FAIL_MSG_REGEX = WB + "(?i:No such file or directory)" + WB;
	public static final String WEBLOGIC_DEPLOYER_FAIL_MSG_REGEX = WB + "(?i:Unable to connect)"
			+ WB;
	public static final String STOP_SUCCESS_MSG_REGEX = WB + "(?i:stop completed)" + WB;
	public static final String UNDEPLOY_SUCCESS_MSG_REGEX = WB + "(?i:undeploy completed)" + WB;
	public static final String DEPLOY_SUCCESS_MSG_REGEX = WB + "(?i:deploy completed)" + WB;
	public static final String START_SUCCESS_MSG_REGEX = WB + "(?i:start completed)" + WB;

	// match for 'Aug 1 11:05'
	public static final String UNIX_LS_L_DATE_REGEX = "[a-zA-Z]+\\s+\\d{1,2}\\s+\\d{1,2}:\\d{1,2}";

	/**
	 * file name portion out of full unix file path.
	 * ex) inquiry003.002.022.ear in /upload/inquiry003.002.022.ear
	 */
	public static final String FILE_NAME_IN_UNIX_FILE_PATH_REGEX = "(?<=/)[.a-zA-Z0-9_-]+(?!/)";

	public static final String WEBLOGIC_REGEX = WB + Value.WEBLOGIC_USER_NAME + WB;

	public static final String ENCRYPTION_KEY_REGEX = ANY + WB + "saved" + WB + ANY + "password"
			+ ANY;

	/**
	 * Check if or not another user has owned edit lock. If another user has owned edit lock,
	 * undeploy command fails.
	 * [Deployer:149163]The domain edit lock is owned by another session in non-exclusive mode - 
	 * this deployment operation requires exclusive access to the edit lock and hence cannot proceed.
	 * @author joohwan.oh
	 * @since 1-Oct-08
	 */
	public static final String EDIT_LOCK_MSG_REGEX = WB + "(?i:edit lock is owned)" + WB;
	
	// -password 'dev!admin'
	public static final String DEPLOY_COMMAND_PASSWORD_PART_REGEX = WSM+"-password"+WSM+ANYM+WSM;
	
	public static final String NAME_REGEX = "[-\\w]+";

	/**
	 * return first match of regular expression from reversed output.
	 * @param output
	 * @param regex
	 * @return if regular expression is found then return last match,
	 *         othewise return empty string.
	 */
	public static String getLastMatch(String output, String regex) {
		String match = "";

		// search first occurence.
		Pattern p = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher m = p.matcher(output);
		int s = -1, e = -1;
		boolean found = false;
		while (m.find()) {
			found = true;
			s = m.start();
			e = m.end();
		}
		if (found) match = output.substring(s, e);
		return match;
	}

	public static boolean canFind(String output, String regex) {
		return (!Value.isEmptyOrNull(getLastMatch(output, regex)));
	}

	/**
	 * return updated string with replacement of first occurence of regular expression.
	 * @param output
	 * @param regex
	 * @return
	 */
	public static String updateFirstMatch(String source, String regex, String replacement) {
		String updatedSource = "";

		// search first occurence.
		Pattern p = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher m = p.matcher(source);
		int s = -1, e = -1;
		boolean found = false;

		if (m.find()) {
			found = true;
			s = m.start();
			e = m.end();
		}
		if (found) {
			updatedSource = updatedSource.substring(0, s) + replacement
					+ updatedSource.substring(e);
		} else {
			throw new RuntimeException("can't find first match for " + regex);
		}

		return updatedSource;
	}

	/**
	 * This method returns the list of all matched string in output against regex.
	 * @param output string searched via regex
	 * @param regex regular expression to search the matched string in output
	 * @return list of all matched string
	 */
	public static String[] getAllMatchStrings(String output, String regex) {
		List<String> list = new ArrayList<String>();
		Pattern p = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher m = p.matcher(output);
		int s = -1, e = -1;
		String match;
		while (m.find()) {
			s = m.start();
			e = m.end();
			match = output.substring(s, e);
			list.add(match);
		}
		return list.toArray(new String[] {});
	}

	public static Range[] getAllMatchRanges(String output, String regex) {
		List<Range> list = new ArrayList<Range>();
		Pattern p = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher m = p.matcher(output);
		int s = -1, e = -1;
		while (m.find()) {
			s = m.start();
			e = m.end();
			list.add(new Range(s, e-s));
		}
		return list.toArray(new Range[] {});
	}

	public static boolean canFind(CommandResult result, String regex) {
		return canFind(result.getStdouterr(), regex);
	}

	/**
	 * Escape text for SED replacement string and return it.
	 * @param text text to be escaped.
	 * @return
	 */
	public static String escapeSedReplacementString(String text) {
		if (text == null || text.trim().length() == 0) return text;
		String escaped = text.replaceAll("([/])", "\\\\$1");
		return escaped;
	}
	
	public static String getCompileErrorMessage(String regex) {
		try {
			Pattern.compile(regex, Pattern.MULTILINE);
		} catch (PatternSyntaxException e){
			return e.getDescription();
		}
		return "";
	}

}
