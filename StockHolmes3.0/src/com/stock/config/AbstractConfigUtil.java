
package com.stock.config;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConfigUtil {
	private static final String UTF_8 = "utf-8";

	private static final String TRUE_STR = "true";

	// private String propertiesFile = "";

	private Logger logger = LoggerFactory.getLogger(AbstractConfigUtil.class);

	/**
	 * properties
	 *
	 * @Description : properties instance
	 */
	private PropertiesConfiguration properties;

	private boolean isSucess = false;

	public AbstractConfigUtil() {
		initializeConfig();
	}

	/**
	 * @description 加载properties文件,文件改动动态生效
	 * @return true:加载成功
	 * 
	 */
	private boolean initializeConfig() {
		try {
			logger.debug("LOADING PROPERTIES........");
			Parameters params = new Parameters();
			File propertiesFile = new File(URLDecoder.decode(getPropertiesFilePath(), UTF_8));

			ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> reloadbuilder = new ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration>(
					PropertiesConfiguration.class)
							.configure(params.fileBased().setEncoding(UTF_8).setFile(propertiesFile));
			logger.debug("SETTING PROPERTIES TO AUTO SAVE........");
			reloadbuilder.setAutoSave(true);
			PeriodicReloadingTrigger trigger = new PeriodicReloadingTrigger(reloadbuilder.getReloadingController(),
					null, 1, TimeUnit.MINUTES);
			trigger.start();

			properties = (PropertiesConfiguration) reloadbuilder.getConfiguration();

		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException in AbstractConfigUtil.initializeConfig()", e);
		} catch (ConfigurationException e) {
			logger.error("ConfigurationException in AbstractConfigUtil.initializeConfig()", e);
		}
		isSucess = properties != null;
		return isSucess;
	}

	/**
	 * @description 返回整数
	 * @param xpath
	 * @return
	 * @author Green
	 */

	public int getIntValue(String xpath) {
		int result = 0;
		try {
			result = Integer.parseInt(getStrValue(xpath));
		} catch (final NumberFormatException e) {
			isSucess = false;
			logger.error("!!!Not integer value for : " + xpath + ". Return default integer value 0.");
		}
		return result;
	}

	/**
	 * @description 返回long
	 * @param xpath
	 * @return
	 * @author Green
	 */
	public long getLongValue(String xpath) {
		long result = 0;
		try {
			result = Long.parseLong(getStrValue(xpath));
		} catch (final NumberFormatException e) {
			isSucess = false;
			logger.error("!!!Not Long value for : " + xpath + ". Return default long value 0.");
		}
		return result;
	}

	public float getFloatValue(String xpath) {
		float result = 0;
		try {
			result = Float.parseFloat(getStrValue(xpath));
		} catch (final NumberFormatException e) {
			isSucess = false;
			logger.error("!!!Not Long value for : " + xpath + ". Return default long value 0.");
		}
		return result;
	}

	/**
	 * @Description : 返回String类型的值
	 * @param xpath
	 * @return
	 * @Return: String
	 */
	public String getStrValue(String xpath) {
		if (!isSucess) {
			logger.info("Properties not initialized yet, start to initialize.");
			initializeConfig();
		}

		String value = properties.getString(xpath);
		if (value == null) {
			isSucess = false;
			logger.info("!!!Configuration:<{}, NULL>", xpath);
		} else {
			logger.trace("Configuration:<{}={}>", xpath, value);
			value = value.trim();
		}
		return value;
	}

	public File getFileValue(String xpath) {
		if (!isSucess) {
			logger.info("Properties not initialized yet, start to initialize.");
			initializeConfig();
		}
		File result = new File(getStrValue(xpath));
		return result;
	}

	public List<String> getStrList(String xpath) {
		if (!isSucess) {
			logger.info("Properties not initialized yet, start to initialize.");
			initializeConfig();
		}
		List<String> strList = new ArrayList<String>();
		final String[] values = properties.getStringArray(xpath);
		if (values == null) {
			isSucess = false;
			logger.info("!!!Configuration:<{}, NULL>", xpath);
		} else {
			logger.trace("Configuration:<{}={}>", xpath, values);
			strList.addAll(Arrays.asList(values));
		}
		return strList;
	}

	/**
	 * @description 返回String的列表
	 * @param xpath
	 * @param sep
	 * @return
	 * @author Green
	 */
	public List<String> getStringList(String xpath, String sep) {
		ArrayList<String> strList = new ArrayList<String>();
		strList.addAll(Arrays.asList(getStrValue(xpath).split(sep)));
		logger.trace("Configuration:<{}={}>", xpath, strList);
		return strList;
	}

	/**
	 * @description 返回整数列表
	 * @param xpath
	 * @param sep
	 * @return
	 * @author Green
	 */
	public List<Integer> getIntList(String xpath, String sep) {
		ArrayList<Integer> intList = new ArrayList<Integer>();
		String[] intArray = getStrValue(xpath).split(sep);
		for (String aInt : intArray) {
			intList.add(Integer.parseInt(aInt.trim()));
		}
		logger.trace("Configuration:<{}={}>", xpath, intList);
		return intList;
	}

	/**
	 * @description 从properties初始化map,必须是以下形式key与value成对出现 <br>
	 *              xpath.key <br>
	 *              xpath.value
	 */
	public HashMap<String, String> getStringMap(String xpath, String sep) {
		HashMap<String, String> resultMap = new HashMap<String, String>();

		List<String> stringList = getStringList(xpath.concat(".key"), sep);
		String strValue = getStrValue(xpath.concat(".value"));

		if (stringList != null && !stringList.isEmpty() && strValue != null && !strValue.isEmpty()) {
			Iterator<String> iterator = stringList.iterator();
			while (iterator.hasNext()) {
				resultMap.put(iterator.next(), strValue);
			}
		} else {
			logger.error("getStringMap with xpath={}, either key or value is null, or both are null. ");
		}
		return resultMap;
	}

	/**
	 * @description 返回boolean值
	 * @param xpath
	 * @return
	 * @author Green
	 */
	public boolean getBoolean(String xpath) {
		String boolString = getStrValue(xpath);
		boolean result = false;
		if (boolString != null && !boolString.isEmpty()) {
			result = boolString.equalsIgnoreCase(TRUE_STR);
		}
		return result;
	}

	/**
	 * @description 判断properties文件是否加载成功
	 * @return
	 * @author Green
	 */
	public boolean isSucess() {
		return isSucess;
	}

	private String getPropertiesFilePath() {
		logger.info("LOAD PROPERTIES FROM: "
				+ Thread.currentThread().getContextClassLoader().getResource(getPropertiesFilename()).getPath());
		return Thread.currentThread().getContextClassLoader().getResource(getPropertiesFilename()).getPath();
	}

	/**
	 * @description 由子类提供properties文件名,properties文件必须位于resources目录下
	 * @return
	 */
	public abstract String getPropertiesFilename();

}
