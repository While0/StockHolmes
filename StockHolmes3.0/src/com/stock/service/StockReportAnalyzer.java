package com.stock.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.stock.config.StockConfig;
import com.stock.dao.StockFinanceDao;
import com.stock.util.StockStringHandler;

@Service("stockReportAnalyzer")
public class StockReportAnalyzer extends StockNoticeAnalyzer {

	@Autowired
	private StockConfig stockconfig;
	@Autowired
	private StockStringHandler stockstringhandler;
	@Autowired
	private StockFinanceDao stockFinanceDao;
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public JSONObject readStockNotice(File ReportFile) {
		// File noticeFile = new File(noticeFileName);
		JSONObject stockfinancebit = new JSONObject();
		BufferedReader reader = null;
		int keywordmark = -1;
		StringBuffer stringbuffer = new StringBuffer("");
		int gainreasonmark = 0;
		String reportkeyword = stockconfig.getReportKeyword();
		String patternrise = "(-?[1-9]\\d*\\.?\\d*%.+?-?[1-9]\\d*\\.?\\d*%)";
		String patterngain = "(([1-9]\\d*,(\\d{3},?)*).+?([1-9]\\d*,(\\d{3},?)*))";
		// 5.00% 至 30.00%
		// 32,239.71 至 39,915.84（万元）
		try {
			logger.info("Start to analyzing " + ReportFile.toString());
			reader = new BufferedReader(new FileReader(ReportFile));
			String textline = null;
			while ((textline = reader.readLine()) != null) { // 一次读入一行，直到读入null为文件结束
				if (keywordmark == -1) {
					keywordmark = textline.indexOf(reportkeyword);
				}
				// logger.debug("debug......"+keywordmark+"xxxxxxxxx"+reportkeyword);

				if (keywordmark >= 0) {
					if (!stockstringhandler.patternMatcher(patternrise, textline).equals("")) {
						String rise = stockstringhandler.patternMatcher(patternrise, textline);
						rise = stockstringhandler.trimComma(rise);
						stockfinancebit.put("rise", rise);
					}

					if (!stockstringhandler.patternMatcher(patterngain, textline).equals("")) {
						String gains = stockstringhandler.patternMatcher(patterngain, textline);
						gains = stockstringhandler.trimComma(gains);
						stockfinancebit.put("gains", gains);
					}

					if (textline.indexOf(stockconfig.getGainTurningKeyword()) >= 0) {
						// if(textline.lastIndexOf("panfeng")>=0){
						gainreasonmark++;
						Pattern p = Pattern.compile(stockconfig.getGainTurningKeyword() + ".* "); // get
																									// a
																									// matcher
																									// object
						Matcher m = p.matcher(textline);
						textline = m.replaceAll("");
					}
					// logger.debug("debug....mark....."+textline+"......."+gainreasonmark+"....."+textline.lastIndexOf("2016"));
					if (textline.length() > 0) {
						// logger.debug("debug....append....."+textline+"......."+gainreasonmark+"....."+textline.lastIndexOf("2016"));
						String[] segment = textline.split(" ");
						for (int i = 0; i < segment.length; i++) {
							stringbuffer.append(segment[i].trim());
						}
						stringbuffer.append(System.getProperty("line.separator"));
					}
					if (textline.length() <= 0 && gainreasonmark == 0) {
						// logger.debug("debug....delete....."+textline+"......."+gainreasonmark+"....."+textline.lastIndexOf("2016"));
						stringbuffer = new StringBuffer("");
					}
					if (textline.length() <= 0 && gainreasonmark > 0) {
						// logger.debug("debug....return....."+textline+"......."+gainreasonmark+"....."+textline.lastIndexOf("2016"));
						stockfinancebit.put("gainreason", stringbuffer);
					}

					if (stockfinancebit.size() >= 3 && textline.length() <= 0) {
						stockfinancebit.put("stockcode", ReportFile.getName().split("-")[0]);
						logger.info("股票: " + (String) stockfinancebit.get("stockcode"));
						logger.info("盈利增幅: " + (String) stockfinancebit.get("rise"));
						logger.info("季度盈利: " + (String) stockfinancebit.get("gains"));
						if (stockfinancebit.containsKey("gainreason")) {
							logger.info("变动原因: " + stockfinancebit.get("gainreason").toString());
						}
						break;
					}
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
		logger.info("Done analyzing: " + ReportFile.toString());
		if (stockfinancebit.size() >= 3) {
			return stockfinancebit;
		} else {
			logger.info("Cann't find the thing.");
		}
		return null;
	}

	@Override
	public JSONObject stockGains(JSONObject stockfinancebit) {
		JSONObject stockGains = new JSONObject();
		// ("rise", "450% - 480%");
		// ("gains", "盈利：12667.65 万元–2031.34 万元");
		if (!(stockfinancebit.isEmpty())) {
			logger.info("Get the number: " + stockfinancebit.get("stockcode"));
			stockGains.put("stockcode", stockfinancebit.get("stockcode"));
			Pattern patterngain = Pattern.compile("([2-9]\\d{3,}|\\d{4,})");
			// Pattern pattern_gains = Pattern.compile("(\\d{3,})");
			String rise = stockfinancebit.get("rise").toString();
			// logger.debug("盈利增幅:"+rise);
			if (rise.indexOf(".") > 0) {
				Pattern patternrise_withpoint = Pattern.compile("([3-9]\\d{1,}\\.\\d+|[1-9]\\d{2,}\\.?\\d?)");
				Matcher matchrisepoint = patternrise_withpoint.matcher(rise);
				if (matchrisepoint.find()) {
					stockGains.put("rise", Float.parseFloat(matchrisepoint.group(1)));
				}
			} else if (rise.indexOf(".") == -1) {
				Pattern patternrise = Pattern.compile("([3-9]\\d{1,}\\.?\\d?|[1-9]\\d{2,}\\.?\\d?)");
				Matcher matchrise = patternrise.matcher(rise);
				while (matchrise.find()) {
					stockGains.put("rise", Float.parseFloat(matchrise.group(1)));
				}
			}
			String gains = stockfinancebit.get("gains").toString();
			Matcher matchgain = patterngain.matcher(gains);
			while (matchgain.find()) {
				stockGains.put("gains", Float.parseFloat(matchgain.group(1)));
			}
			logger.debug("增幅高值: " + stockGains.get("rise") + "%");
			logger.debug("盈利高值: " + stockGains.get("gains") + "万元");
			if (stockGains.size() == 3)
				return stockGains;
		}
		logger.info("Done getting number.");
		return null;
	}

	@Transactional
	@Override
	public void stockDecorater(String TransformPath) {
		File transformpath = new File(TransformPath);
		JSONObject stockfinancebit = new JSONObject();
		if (transformpath.isDirectory()) {
			File[] noticeArray = transformpath.listFiles();
			if (noticeArray != null) {
				int analyzedfile = 0;
				int filequantity = noticeArray.length;
				for (int i = 0; i < noticeArray.length; i++) {
					if (!noticeArray[i].isHidden()) {
						analyzedfile++;
						if (noticeArray[i].getName().indexOf("报告") > 0) {
							stockfinancebit = readStockNotice(noticeArray[i]);
						} else {
							stockfinancebit = super.readStockNotice(noticeArray[i]);
						}
						JSONObject stockGains = null;
						if (stockfinancebit != null) {
							if ((stockGains = stockGains(stockfinancebit)) != null) {
								String gainreason = "";
								if (stockfinancebit.containsKey("gainreason")) {
									gainreason = stockfinancebit.get("gainreason").toString();
								}
								stockFinanceDao.insertStockFinance(stockfinancebit.getString("stockcode"),
										stockGains.getString("rise"), stockGains.getString("gains"),
										stockfinancebit.getString("rise"), stockfinancebit.getString("gains"),
										gainreason);
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
				logger.info("Total file: " + filequantity + " analyzed : " + analyzedfile);
			}
		}
	}
}