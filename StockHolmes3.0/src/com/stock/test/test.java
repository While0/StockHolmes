package com.stock.test;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.stock.config.StockConfig;
import com.stock.grab.StockEastmyGrab;
import com.stock.grab.StockEmReportGrab;
import com.stock.grab.StockGrab;
import com.stock.grab.StockMetaInfoGrab;
import com.stock.mapper.StockNoticeMapper;
import com.stock.policy.StockPolicy;
import com.stock.policy.StockPolicyImpl;
import com.stock.service.StockNoticeAnalysis;
import com.stock.service.StockReportAnalyzer;
import com.stock.util.StockTextHandler;
import com.stock.startup.StockCronJob;

import org.springframework.context.expression.*;

public class test {

	static ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
	StockConfig StockConfig = (StockConfig) context.getBean("StockConfig");
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public void testStockTextHandler1() {
		StockTextHandler StockTextHandler = (StockTextHandler) context.getBean("StockTextHandler");
		Map<String, String> announcement = new HashMap<String, String>();
		announcement.put("username", "http://www.cnblogs.com/lovebread/archive/2009/11/23/1609122.html");
		announcement.put("username1", "http://www.cnblogs.com/lovebread/archive/12009/11/23/1609122.html");
		announcement.put("username2", "http://www.cnblogs.com/lovebread/archive/22009/11/23/1609122.html");
		announcement.put("username3", "http://www.cnblogs.com/lovebread/archive/32009/11/23/1609122.html");
		announcement.put("username4", "http://www.cnblogs.com/lovebread/archive/42009/11/23/1609122.html");
		StockTextHandler.retrieveText(announcement, "/Users/panfeng/Downloads/Notice/NoticeLink.list");
		for (Map.Entry<String, String> entry : announcement.entrySet()) {
			String k = entry.getKey();
			String v = entry.getValue();
			System.out.println(k + " = " + v);
		}
	}

	public void testStockAnnouceGrab1() {
		StockGrab stockcninfgrab = (StockGrab) context.getBean("stockcninfgrab");
		Map<String, String> announcement = new HashMap<String, String>();
		announcement.put("username", "http://127.0.0.1:8080/StockHome/");
		announcement.put("username1", "http://127.0.0.1:8080/StockHome2/");
		announcement.put("username2", "http://127.0.0.1:8080/StockHome3/");
		announcement.put("username3", "http://127.0.0.1:8080/StockHome4/");
		announcement.put("username4",
				"http://www.cninfo.com.cn/cninfo-new/disclosure/szse/bulletin_detail/true/1202956552?announceTime=2016-12-24");
		// StockTextHandler.updateLinkMemo(link, isdownload, linktextmemo);
	}

	public void testStockTextHandler() {
		StockTextHandler StockTextHandler = (StockTextHandler) context.getBean("StockTextHandler");
		Map<String, String> announcement = new HashMap<String, String>();
		announcement.put("username", "http://www.cnblogs.com/lovebread/archive/2009/11/23/1609122.html");
		announcement.put("username1", "http://www.cnblogs.com/lovebread/archive/12009/11/23/1609122.html");
		announcement.put("username2", "http://www.cnblogs.com/lovebread/archive/22009/11/23/1609122.html");
		announcement.put("username3", "http://www.cnblogs.com/lovebread/archive/32009/11/23/1609122.html");
		announcement.put("username4", "http://www.cnblogs.com/lovebread/archive/42009/11/23/1609122.html");
		StockTextHandler.retrieveText(announcement, "/Users/panfeng/Downloads/Notice/NoticeLink.list");
		for (Map.Entry<String, String> entry : announcement.entrySet()) {
			String k = entry.getKey();
			String v = entry.getValue();
			System.out.println(k + " = " + v);
		}
	}

	public void testcopyFile() {
		StockTextHandler StockTextHandler = (StockTextHandler) context.getBean("StockTextHandler");
		Map<String, String> announcement = new HashMap<String, String>();
		ArrayList<String> wantedstock = new ArrayList<String>();
		announcement.put("600400-红豆股份-2016年年度业绩预增公告", "600400-红豆股份-2016年年度业绩预增公告");
		announcement.put("200771-杭汽轮Ｂ-2016年年度业绩预告（已取消）", "71-杭汽轮Ｂ-2016年年度业绩预告（已取消）.PDF");
		announcement.put("200771-杭汽轮Ｂ-2016年年度业绩预告（英文版）（已取消）", "2222222222)");
		announcement.put("200771-杭汽轮Ｂ-2016年年度业绩预告（更新后）", "22221222222222)");
		wantedstock.add("600401");
		wantedstock.add("200771");
		if (announcement != null && wantedstock != null) {
			for (Entry<String, String> entry : announcement.entrySet()) {
				for (int i = 0; i < wantedstock.size(); i++) {
					System.out.println(wantedstock.get(i) + "    " + entry.getKey().split("-")[0]);
					System.out.println();
					if (wantedstock.get(i).equals(entry.getKey().split("-")[0])) {
						StockTextHandler.copyFile("/Users/panfeng/Downloads/Notice/Msg/",
								"/Users/panfeng/Downloads/Notice/wanted/", entry.getKey().concat(".PDF"));
						System.out.println(entry.getKey().concat(".PDF"));
						System.out.println("match");
					}
				}
			}
		}
	}

	public void testStockEastmyGrab01() {
		StockEastmyGrab StockEastmyGrab = (StockEastmyGrab) context.getBean("stockeastmygrab");
		StockEastmyGrab.parseStockHtml();
	}

	public void testStockEmReportGrab01() {
		StockEmReportGrab StockEmReportGrab = (StockEmReportGrab) context.getBean("StockEmReportGrab");
		StockEmReportGrab.parseStockHtml();

	}

	public void testmap() {
		Map<String, String> announcement = new HashMap<String, String>();
		Map<String, Map<String, String>> announce = new HashMap<String, Map<String, String>>();
		announcement.put("1", "one");
		announcement.put("2", "two");
		announcement.put("3", "three");
		announcement.put("4", "four");
		announcement.put("5", "five");
		announcement.put("6", "six");
		announce.put("002123", announcement);
		System.out.println(announce.get("002123").get("1"));
	}

	public void testStockReportAnalyzer1() {
		StockNoticeAnalysis stocknoticeanalyzer = (StockNoticeAnalysis) context.getBean("stocknoticeanalyzer");
		File noticeFile = new File("/Users/panfeng/Downloads/Notice/transformed/002098-浔兴股份-2016年度业绩预告修正公告.txt");
		stocknoticeanalyzer.readStockNotice(noticeFile);
	}

	public void testStockReportAnalyzer2() {
		StockReportAnalyzer StockReportAnalyzer = (StockReportAnalyzer) context.getBean("stockReportAnalyzer");
		StockReportAnalyzer.stockDecorater(StockConfig.getTransformPath());
	}

	public void insertnextline() {
		StringBuffer ssb = new StringBuffer();
		StringBuffer sb = new StringBuffer("公司子公司深圳市合正汽车电子有限公司、南京恒电电子有限公司自被");
		System.out.println(sb.length());
		int index;
		while (sb.length() > 0) {
			if ((index = sb.indexOf("，")) > 0) {
				String[] cao = sb.substring(0, index).split(" ");
				for (int i = 0; i < cao.length; i++) {
					ssb.append(cao[i].trim());
				}
				ssb.append("," + System.getProperty("line.separator"));
				// sb.replace(index, index,
				// System.getProperty("line.separator"));
				sb.delete(0, index + 1);
				if (sb.length() <= 0) {
					break;
				}
			} else {
				String[] cao = sb.toString().split(" ");
				for (int i = 0; i < cao.length; i++) {
					ssb.append(cao[i].trim());
				}
				break;
			}

		}
		System.out.println("--111111111111111111111----" + ssb.toString());
		System.out.println("--111111111111111111111----" + ssb.length());
	}

	public void testpolicy1() {
		StockPolicy stockpolicyimpl = (StockPolicy) context.getBean("stockpolicyimpl");
		stockpolicyimpl.grabStockNotice();
	}

	public void testpolicy2() {
		StockPolicy stockpolicyimpl = (StockPolicy) context.getBean("stockpolicyimpl");
		stockpolicyimpl.carryStockNotice();
	}

	public void testpolicy3() {
		StockPolicyImpl stockpolicyimpl = (StockPolicyImpl) context.getBean("stockpolicyimpl");
		stockpolicyimpl.refineStockFinance();
	}

	public void testStockEastmyGrab02() {
		StockEastmyGrab StockEastmyGrab = (StockEastmyGrab) context.getBean("stockeastmygrab");
		// StockEastmyGrab.ll(0);

	}

	public void testpolicy4() {
		StockPolicy stockpolicyimpl = (StockPolicy) context.getBean("stockpolicyimpl");
		stockpolicyimpl.analysisStock();
	}

	public void testcrontab1() {
		StockCronJob StockCronJob = (StockCronJob) context.getBean("StockCronJob");
		StockCronJob.stockWork();
	}

	public static void main(String[] args) {
		System.out.println("Test-----------------------");
		test test = new test();
		test.testcrontab1();

	}

}
