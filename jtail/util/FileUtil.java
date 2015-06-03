package jtail.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import jtail.ui.dialog.DialogUtil;

/**
 * This class provides a variety of method for handling a file.
 * 
 * @author joohwan.oh
 * @since 13-Aug-08
 */
public class FileUtil {
	public static String NL = System.getProperty("line.separator");
		
	public static String readFile(File file) {
		return readFile(file, 0, (int) file.length());
	}
	
	public static String readFile(File file, int skipSize) {
		return readFile(file, skipSize, ((int) file.length()) - skipSize);
	}
	
	/**
	 * Read file and return its content as String.
	 * 
	 * @param file
	 * @return file content as String.
	 * @throws Exception
	 */
	public static String readFile(File file, int skipSize, int readSize) {
		InputStream is = null;
		byte[] bytes = null;
		try {
			is = new FileInputStream(file);

			// Get the size of the file
			long length = file.length();

			if (length > Integer.MAX_VALUE) {
				// File is too large
				throw new RuntimeException("File is too large so can't process it: "
						+ file.getName());
			}

			// don't read it from the start of the file.
			if (skipSize > 0) {
				is.skip(skipSize);
			}
			
			// Create the byte array to hold the data
			bytes = new byte[readSize];

			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			// Ensure all the bytes have been read in
			if (offset < bytes.length) {
				//DialogUtil.displayErrorDialog(new IOException("Could not completely read file: " + file.getName()));
			}
		} catch (IOException e) {
			DialogUtil.displayErrorDialog(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}

		String fileContent = new String(bytes);
		return fileContent;
	}
	
//	public static String readFile(File file, FilterType filterType) throws IOException {
//		return readFile(file, 0, (int) file.length(), filterType);
//	}
		
//	public static String readFile(File file, int skipSize, int readSize, FilterType filterType) {
//		String content = readFile(file, skipSize, readSize);
//		if (filterType == FilterType.EXCLUDE){
//			content = FilterHandler.getFilteredContent(content);
//		}
//		return content;
//	}

	// public static String readFile(File file, int startOffset) {
	// return null;
	// }

	/**
	 * clear file and write fileContent to the file.
	 * 
	 * @param file
	 *            file to be overwritten
	 * @param fileContent
	 *            new content for the file
	 * @throws Exception
	 */
	public static void writeFile(File file, String fileContent) throws IOException {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(file));
			out.write(fileContent);
			out.close();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				out.close();
			} catch (IOException e) {
			}
		}
	}

	public static String getFileNameInUnixFilePath(String unixFilePath) {
		return RegexUtil.getLastMatch(unixFilePath, RegexUtil.FILE_NAME_IN_UNIX_FILE_PATH_REGEX);
	}

	public static String getLocalFileName(String localFilePath) {
		File localFile = new File(localFilePath);
		String fileName = localFile.getName();
		return fileName;
	}

//	public static String readFile(File file, int skipSize, int length) {		
//	if (file.length() > Integer.MAX_VALUE) {
//		// File is too large
//		throw new RuntimeException("File is too large so can't process it: " + file.getName());
//	}
//
//	BufferedReader br = null;
//	String fileContent = "";
//	try {
//		br = new BufferedReader(new FileReader(file));
//
//		// don't read it from the start of the file.
//		if (skipSize > 0) {
//			br.skip(skipSize);
//		}
//
//		int numRead = 0;
//		int offset = 0;			
//		char[] chars = new char[length];
//		while (offset < chars.length
//				&& (numRead = br.read(chars, offset, chars.length - offset)) >= 0) {
//			offset += numRead;
//		}
//		fileContent = new String(chars);
//	} catch (IOException e) {
//		DialogUtil.displayErrorDialog(e);
//	} finally {
//		try {
//			br.close();
//		} catch (IOException e) {
//		}
//	}
//
//	return fileContent;
//}

}
