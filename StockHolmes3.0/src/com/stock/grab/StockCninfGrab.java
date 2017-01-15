package com.stock.grab;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.config.StockConfig;
import com.stock.dao.StockNoticeDao;
import com.stock.service.StockHttpGrab;
import com.stock.util.StockStringHandler;

@Service("stockcninfgrab")
public class StockCninfGrab implements StockGrab {
	@Autowired
	private StockConfig stockconfig;
	@Autowired
	private StockHttpGrab stockhttpgrab;
	@Autowired
	private StockStringHandler stockstringhandler;
	@Autowired
	private StockNoticeDao stockNoticeDao;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.stock.grab.StockGrab#getHeader()
	 */
	@Override
	public Map<String, String> getHeader() {
		Map<String, String> header = new HashMap<String, String>();
		header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		header.put("Accept-Encoding", "gzip, deflate, sdch");
		header.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6");
		header.put("Cache-Control", "no-cache");
		header.put("Connection", "keep-alive");
		header.put("Cookie", "JSESSIONID=878E9DC7E5C89786FA1" + System.currentTimeMillis());
		header.put("Host", "www.cninfo.com.cn");
		header.put("Pragma", "no-cache");
		header.put("Referer", "https://www.baidu.com/");
		header.put("Upgrade-Insecure-Requests", "1");
		header.put("User-Agent:", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36"
				+ " (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36");
		return header;
	}

	@Override
	@Transactional
	public void parseStockHtml() {
		logger.info("Start Companies'Performance Forecast Grab ...");
		Document doc = Jsoup.parse(stockhttpgrab.httpGetBuffer(stockconfig.getWebsiteUrl(), getHeader()).toString());
		// logger.debug("Response Length:"+stockhttpgrab.getChunkLength());
		// stockhttpgrab.deleteChunkBuffer();
		int li = 1;
		int div = 3;
		int dd = 1;
		while (li >= 1) {
			while (div >= 3) {
				while (dd >= 1) {
					//// *[@id="con-a-1"]/ul/li[13]/div[4]/dd[1]/a
					String cssnoticetitle = "#con-a-1 > ul > li:nth-child(" + li + ") > div.t" + div + "> dd:nth-child("
							+ dd + ") >a";
					// #con-a-1 > ul > li:nth-child(1) > div.t1 > font
					String cssstockcode = "#con-a-1 > ul > li:nth-child(" + li + ") > div.t1 > font";
					String cssstockname = null;
					Elements enoticetitle = doc.select(cssnoticetitle);
					Elements estockcode = doc.select(cssstockcode);
					logger.debug("Try the CSS:" + cssnoticetitle);
					logger.debug("Catch  CSS:" + enoticetitle);

					boolean isEnd = StringUtils.isEmpty(enoticetitle.toString());

					if (isEnd) {
						dd = 1;
						break;
					}

					else {
						logger.debug("Handle: " + enoticetitle.toString());
						dd++;
						String isthistitle = "<a href=.*\">(.+?(" + stockconfig.getGrabKeyword() + ").*)</a>";
						// String isthistitle = "<a href=.*\">(.+?(公告).*)</a>";
						// Pattern pa_title = Pattern.compile("<a
						// href=.*\">(.+?(业绩|业绩预告|季度报告|年度业绩|季度业绩|季度预告|年度报告).*)</a>");
						Pattern patterntitle = Pattern.compile(isthistitle);
						// Pattern patterntitle = Pattern.compile("<a
						// href=.*\">(.+?(公告|业绩预告).*)</a>");
						// String keyword="业绩预告";
						Pattern patternlink = Pattern.compile("<a href=\"(/.+?)\".*</a>");
						Matcher matchertitle = null;
						Matcher matcherlink = null;
						String stockcode = "";
						String stocktitle = "";
						String stocklink = "";
						String stockname = "";
						matchertitle = patterntitle.matcher(enoticetitle.toString());
						// int index=enoticetitle.toString().indexOf(keyword);
						if (StringUtils.isNotEmpty(enoticetitle.toString()) && (matchertitle.find())) {
							// if(StringUtils.isNotEmpty(enoticetitle.toString())&&index>0){
							stocktitle = matchertitle.group(1).trim();
							// stocktitle=stockstringhandler.trimTab(enoticetitle.toString());
							if ((stockcode = stockstringhandler.trimTab(estockcode.toString())).equals("")) {
								cssstockcode = "#con-a-1 > ul > li:nth-child(" + li + ") > div.t2 > font";
								stockcode = stockstringhandler.trimTab(doc.select(cssstockcode).toString());
							}

							if (cssstockcode.equals("#con-a-1 > ul > li:nth-child(" + li + ") > div.t2 > font")) {
								// #con-a-1 > ul > li:nth-child(4) > div.t2
								cssstockname = "#con-a-1 > ul > li:nth-child(" + li + ") > div.t3 > font";
							} else {
								cssstockname = "#con-a-1 > ul > li:nth-child(" + li + ") > div.t2 > font";
							}
							Elements estockname = doc.select(cssstockname);
							stockname = stockstringhandler.trimTab(estockname.toString());
							logger.info("Match CSS:" + enoticetitle);
							logger.info("Find: " + stockcode + "-" + stockname + "-" + stocktitle);
							matcherlink = patternlink.matcher(enoticetitle.toString());
							if (matcherlink.find()) {
								stocklink = matcherlink.group(1);
								stocklink = stockhttpgrab.montageCninfoURL(stockconfig.getWebsiteUrl(), stocklink);
								logger.info("公告链接:" + stocklink);

								if (stockNoticeDao.countNoticeLink(stocklink) < 1) {
									stockNoticeDao.insertStockNotice(stockcode,
											stockcode + "-" + stockname + "-" + stocktitle, stocklink, 0);
								}
							}
						} else {
							logger.debug("Skip CSS: " + enoticetitle);
						}

					}
				}

				div++;

				if (div > 5) {
					div = 3;
					break;
				}
			}

			li++;

			if (li > 20) {
				break;
			}
		}
	}

}
