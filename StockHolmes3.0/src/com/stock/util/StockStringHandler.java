package com.stock.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class StockStringHandler {

	public String trimComma(String string) {
		String regex = ",";
		Pattern pattern = Pattern.compile(regex);
		Matcher macher = pattern.matcher(string);
		return macher.replaceAll("");
	}

	public String patternMatcher(String patterner, String string) {
		Pattern pattern = Pattern.compile(patterner);
		Matcher matcher = pattern.matcher(string);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return "";
		}
	}

	public String trimTab(String xpath) {
		String tempstring;
		if (StringUtils.isNotEmpty(xpath)) {
			tempstring = xpath.split(">")[1];
			return tempstring.split("<")[0];
		}
		return "";
	}

}
