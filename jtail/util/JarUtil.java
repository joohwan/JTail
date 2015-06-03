package jtail.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * This class provides a way to extract a file from jar file format (.jar, .ear, etc).
 *  
 * @author joohwan.oh
 * @since 26-Aug-08
 */
public class JarUtil {
	private static final int BUFFER_SIZE = 2048;

	public static File extractFile(File jarFile, String extractFileEntry, String outputDirectoryPath)
			throws IOException {
		File extractedFile = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			JarFile jar = new JarFile(jarFile);
			ZipEntry entry = jar.getEntry(extractFileEntry);
			extractedFile = new File(outputDirectoryPath, entry.getName());
			in = new BufferedInputStream(jar.getInputStream(entry));
			out = new BufferedOutputStream(new FileOutputStream(extractedFile));
			byte[] buffer = new byte[BUFFER_SIZE];
			while (true) {
				int nBytes = in.read(buffer);
				if (nBytes <= 0) break;
				out.write(buffer, 0, nBytes);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				out.flush();
				out.close();
				in.close();
			} catch (IOException e) {}
		}

		return extractedFile;
	}

	public static String getFileContent(File jarFile, String extractFileEntry) throws IOException {
		InputStream in = null;
		StringBuilder sb = new StringBuilder(0);
		try {
			JarFile jar = new JarFile(jarFile);
			ZipEntry entry = jar.getEntry(extractFileEntry);
			in = new BufferedInputStream(jar.getInputStream(entry));
			byte[] buffer = new byte[BUFFER_SIZE];
			while (true) {
				int nBytes = in.read(buffer);
				if (nBytes <= 0) break;
				sb.append(new String(buffer));
			}
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				in.close();
			} catch (IOException e) {}
		}

		return sb.toString();
	}
}
