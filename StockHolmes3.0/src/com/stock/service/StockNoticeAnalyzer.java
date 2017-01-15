package com.stock.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.stock.config.StockConfig;
import com.stock.dao.StockFinanceDao;
import com.stock.util.StockStringHandler;

@Service("stocknoticeanalyzer")
public class StockNoticeAnalyzer implements StockNoticeAnalysis {

	@Autowired
	private StockConfig stockconfig;
	@Autowired
	private StockStringHandler stockstringhandler;
	@Autowired
	private StockFinanceDao stockFinanceDao;
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public JSONObject readStockNotice(File noticeFile) {
		// File noticeFile = new File(noticeFileName);
		JSONObject stockfinancebit = new JSONObject();
		ArrayList<String> stockriselist = new ArrayList<String>();
		BufferedReader reader = null;

		// String patternrise
		// ="(?:"+stockconfig.getIncKeyword()+")((.+?%){1,})"; //
		// [1-9]\\d*,(\\d{3},?)*
		String patternrise = "(?=" + stockconfig.getIncKeyword() + ")((.+?%){1,})"; // [1-9]\\d*,(\\d{3},?)*
		String patterngains = "(?=" + stockconfig.getGainsKeyword() + ")((.+?万元){1,}).*";
		String patterngains2 = "(?=" + stockconfig.getGainsKeyword() + ")((.+?万元){2,}).*";
		try {
			logger.info("Start to analyzing " + noticeFile.toString());
			stockfinancebit.put("stockcode", noticeFile.getName().split("-")[0]);
			reader = new BufferedReader(new FileReader(noticeFile));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) { // 一次读入一行，直到读入null为文件结束
				if (!stockfinancebit.containsKey("rise")) {
					if (!stockstringhandler.patternMatcher(patternrise, tempString).equals("")) {
						String rise = stockstringhandler.patternMatcher(patternrise, tempString);
						logger.debug("盈利增幅: " + rise);
						if (rise.indexOf("下") < 0) {
							if (rise.indexOf("减") < 0) {
								rise = stockstringhandler.trimComma(rise);
								stockfinancebit.put("rise", rise);
							}
						}
					}
				}

				// 如果出现：
				// 归属于上市公司 比上年同期增长：62%—87%
				// 盈利：28,383万元
				// 股东的净利润 盈利：46,000 万元—53,000 万元
				// 这样几行，那么，应该取46,000 万元—53,000 万元才是正确的盈利，接下来这段代码152-160是此用意。
				if (!stockstringhandler.patternMatcher(patterngains2, tempString).equals("")) {
					String gains = stockstringhandler.patternMatcher(patterngains2, tempString);
					gains = stockstringhandler.trimComma(gains);
					// stockriselist.add(gains);
					stockfinancebit.put("gains", gains);
					logger.debug("获取季度盈利区间: " + (String) stockfinancebit.get("gains"));
				} else if (!stockstringhandler.patternMatcher(patterngains, tempString).equals("")) {
					String gains = stockstringhandler.patternMatcher(patterngains, tempString);
					gains = stockstringhandler.trimComma(gains);
					stockriselist.add(gains);
					logger.debug("获取季度盈利: " + (String) stockriselist.get(0));
				}
				if (stockfinancebit.size() >= 3) {
					logger.debug("股票代码: " + (String) stockfinancebit.get("stockcode"));
					logger.debug("盈利增幅: " + (String) stockfinancebit.get("rise"));
					logger.debug("盈利区间: " + (String) stockfinancebit.get("gains"));
					break;
				}

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		logger.info("Done analyzing: " + noticeFile.toString());
		if (!stockfinancebit.containsKey("gains")) {
			if (stockriselist.size() > 0) {
				stockfinancebit.put("gains", stockriselist.get(0));
			}
		}
		if (stockfinancebit.size() >= 3) {
			return stockfinancebit;
		} else {
			logger.info("Cann't find the finance number.");
		}
		return null;
	}

	@Override
	public JSONObject stockGains(JSONObject stockfinancebit) {
		JSONObject stockGains = new JSONObject();
		// ("rise", "450% - 480%");
		// ("gains", "盈利：12667.65 万元–2031.34 万元");
		if (!(stockfinancebit.isEmpty())) {
			stockGains.put("stockcode", stockfinancebit.get("stockcode"));
			// Pattern pattern_rise = Pattern.compile("([3-9]\\d{1,}|\\d{3,})");
			Pattern pattern_rise = Pattern.compile("(-?[3-9]\\d{2,}|\\d{3,})");
			Pattern pattern_gains = Pattern.compile("([2-9]\\d{3,}|\\d{4,})");
			// Pattern pattern_gains = Pattern.compile("(\\d{3,})");
			String rise = stockfinancebit.get("rise").toString();
			Matcher matchrise = pattern_rise.matcher(rise);
			while (matchrise.find()) {
				stockGains.put("rise", matchrise.group(1));
				logger.debug("盈利增幅高值: " + matchrise.group(1) + "%");
			}
			String gains = stockfinancebit.get("gains").toString();
			Matcher matchgains = pattern_gains.matcher(gains);
			while (matchgains.find()) {
				stockGains.put("gains", matchgains.group(1));
			}
			logger.debug("盈利高值: " + stockGains.get("gains") + "万元");
			if (stockGains.size() == 3)
				return stockGains;
		}
		return null;
	}

	@Override
	public void stockDecorater(String TransformPath) {
		File transformpath = new File(TransformPath);
		if (transformpath.isDirectory()) {
			File[] noticeArray = transformpath.listFiles();
			if (noticeArray != null) {
				int analyzedfile = 0;
				int filequantity = noticeArray.length;
				for (int i = 0; i < noticeArray.length; i++) {
					if (!noticeArray[i].isHidden()) {
						analyzedfile++;
						JSONObject stockfinancebit = readStockNotice(noticeArray[i]);
						JSONObject stockGains = null;
						if (stockfinancebit != null) {
							if ((stockGains = stockGains(stockfinancebit)) != null) {
								String stockgainreason = null;
								stockFinanceDao.insertStockFinance(stockfinancebit.getString("stockcode"),
										stockGains.getString("rise"), stockGains.getString("gains"),
										stockfinancebit.getString("rise"), stockfinancebit.getString("gains"),
										stockgainreason);
							}
						}
						logger.info("Move  notice file to: " + stockconfig.getReadedPath().toString());
						File targetfile = new File(stockconfig.getReadedPath() + noticeArray[i].getName());
						try {
							FileUtils.copyFile(noticeArray[i], targetfile);
							noticeArray[i].delete();
						} catch (IOException e) {
							logger.error("Moving fail: " + noticeArray[i]);
							e.printStackTrace();
						}
					} else {
						filequantity--;
					}
				}
				logger.info("Total file: " + filequantity + " analyzed file: " + analyzedfile);
			}
		}
	}

}