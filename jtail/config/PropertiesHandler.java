package jtail.config;

import java.io.InputStream;
import java.util.Properties;

import jtail.util.Value;

public class PropertiesHandler {
	private final Properties prop = new Properties();

	public PropertiesHandler(InputStream in) {
		try {
			prop.load(in);
		} catch (Exception e) {
			System.err.println((new StringBuilder("can't create Properties from: ")).append(in)
					.toString());
			e.printStackTrace();
		}
	}

	public String getValue(String key) {
		if (Value.isEmptyOrNull(key))
			return "";
		String value = prop.getProperty(key);
		if (value == null)
			value = "";
		return value;
	}

	public void setValue(String key, String value) {
		if (Value.isEmptyOrNull(key)) {
			return;
		} else {
			prop.put(key, value);
			return;
		}
	}
}
