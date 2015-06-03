package jtail.util;

import java.text.DecimalFormat;

public class LoggingUtil {
	public static String getElapsedTimeInSeconds(long startTimeInMillis, int scale) {
		long endTimeInMillis = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder("###,###"); 
		if (scale < 0) {
			scale = 0;
		}
		for (int i = 0; i < scale; i++) {
			if (i == 0){
				sb.append(".");
			}
			sb.append("#");
		}
		
		DecimalFormat df = new DecimalFormat(sb.toString());
		double seconds = (endTimeInMillis - startTimeInMillis) / 1000d;
		return (df.format(seconds) + " seconds.");
	}
}
