package com.stock.grab;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stock.config.StockConfig;
import com.stock.dao.StockFinanceDao;
import com.stock.dao.StockInfoDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.service.StockHttpGrab;

@Service("stockmetainfograb")
public class StockMetaInfoGrab implements StockGrab {

	@Autowired
	private StockConfig stockconfig;
	@Autowired
	private StockHttpGrab stockhttpgrab;
	@Autowired
	private StockInfoDao stockInfoDao;
	@Autowired
	private StockFinanceDao stockFinanceDao;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	public Map<String, String> getHeader() {
		Map<String, String> header = new HashMap<String, String>();
		header.put("Host", "qt.gtimg.cn\r\n");
		header.put("Connection", "keep-alive");
		header.put("Pragma", "no-cache");
		header.put("Cache-Control", "no-cache");
		header.put("Upgrade-Insecure-Requests", "1");
		header.put("Host", "  Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) "
				+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2883.87 Safari/537.36");
		header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		header.put("Accept-Encoding", "gzip, deflate, sdch");
		header.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6");
		return header;
	}

	private void grabStockInfo(String stockcode) {
		String url = stockhttpgrab.composeURL(stockconfig.getTencentInterface(), stockcode);
		logger.info("Grabing info of : " + stockcode + "; from URL: " + url);
		String resData = stockhttpgrab.httpGetBuffer(url, getHeader()).toString();
		logger.debug("Receive response ...");
		String stockname = resData.split("~")[1].toString();
		String stockpe = resData.split("~")[39];
		if (stockpe.trim().equals("")) {
			stockpe = "300";
		}
		String stockvalue = resData.split("~")[45];
		stockInfoDao.insertStockInfo(stockcode, stockname, stockpe, stockvalue, 0);
	}

	@Transactional
	public void parseStockHtml() {
		List<String> stockcodelist = stockFinanceDao.selectStockCode();
		for (int i = 0; i < stockcodelist.size(); i++) {
			grabStockInfo(stockcodelist.get(i));
		}
	}

}

/*
 * GET /q=sz002446 HTTP/1.1 Host: qt.gtimg.cn Connection: keep-alive Pragma:
 * no-cache Cache-Control: no-cache Upgrade-Insecure-Requests: 1 User-Agent:
 * Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML,
 * like Gecko) Chrome/55.0.2883.87 Safari/537.36 Accept:
 * text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*\/*;q=0 .8
 * Accept-Encoding: gzip, deflate, sdch Accept-Language:
 * zh-CN,zh;q=0.8,zh-TW;q=0.6
 * 
 * HTTP/1.1 200 OK Content-Length: 482 Connection: Keep-Alive Content-Type:
 * application/x-javascript; charset=GBK Cache-Control: must-revalidate Expires:
 * Mon, 01 Sep 1980 06:51:02 GMT Etag: 1481611285-184
 * 
 * v_sz002446="51~........~002446~25.00~24.64~24.70~32016~17150~14866~25.00~
 * 532~
 * 24.96~10~24.95~5~24.89~10~24.88~29~25.04~10~25.05~33~25.08~48~25.09~1~25. 10~
 * 57~14:41:21/25.00/2/S/5000/13924|14:41:19/25.03/20/B/50052/13921|14:41:15
 * /25.00/10/S/25000/
 * 13918|14:41:12/25.00/686/B/1713727/13914|14:41:09/24.95/13/S/32443/13912|
 * 14:41:03/24.96/2/B/
 * 4992/13905~20161213144121~0.36~1.46~25.03~24.06~24.96/31283/76862663~
 * 32016~7869~1.89 ~53.34~~25.03~24.06~3.94~42.28~112.07~4.55~27.10~22.18~0.78";
 */