package com.stock.grab;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.config.StockConfig;
import com.stock.dao.StockNoticeDao;
import com.stock.service.StockHttpGrab;

@Service("stockeastmygrab")
public class StockEastmyGrab implements StockGrab {
	@Autowired
	private StockConfig stockconfig;
	@Autowired
	private StockHttpGrab stockhttpgrab;
	@Autowired
	private StockNoticeDao stockNoticeDao;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	public Map<String, String> getHeader() {
		Map<String, String> header = new HashMap<String, String>();
		header.put("Host", "data.eastmoney.com");
		header.put("Connection", "keep-alive");
		header.put("Cookie", "st_pvi=62750235173910; emstat_bc_emcount=19799960313412653463;"
				+ " HAList=a-sz-002040-%u5357%u4EAC%u6E2F%2Ca-sz-002446-%u76DB%u8DEF%u901A%u4FE1;"
				+ " em_hq_fls=old; emstat_ss_emcount=0_1482333315_2278864179; st_si=" + System.currentTimeMillis());
		header.put("Pragma", "no-cache");
		header.put("Cache-Control", "no-cache");
		header.put("Accept", "application/json, text/javascript, */*; q=0.01");
		header.put("Accept-Encoding", "gzip, deflate, sdch");
		header.put("Upgrade-Insecure-Requests", "1");
		header.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6");
		header.put("User-Agent:", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36"
				+ " (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36");
		return header;
	}

	public String getQueryParameters() {
		ArrayList<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		// http://data.eastmoney.com/notices/getdata.ashx?StockCode=&FirstNodeType=1&
		// CodeType=1&PageIndex=1&PageSize=20&jsObj=HmoKFyuy&SecNodeType=5&Time=&rt=49427674
		parameters.add(new BasicNameValuePair("StockCode", ""));
		parameters.add(new BasicNameValuePair("FirstNodeType", "1"));
		parameters.add(new BasicNameValuePair("CodeType", "1"));
		parameters.add(new BasicNameValuePair("PageIndex", "1"));
		parameters.add(new BasicNameValuePair("PageSize", "20"));
		parameters.add(new BasicNameValuePair("jsObj", "HmoKFyuy"));
		parameters.add(new BasicNameValuePair("SecNodeType", "5"));
		parameters.add(new BasicNameValuePair("Time", ""));
		parameters.add(new BasicNameValuePair("rt", "49427674"));
		try {
			return EntityUtils.toString(new UrlEncodedFormEntity(parameters, "UTF-8"));
		} catch (ParseException e) {
			logger.error("Parse query parameter Fail.");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			logger.error("Encoding is wrong.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Transactional
	@Override
	public void parseStockHtml() {
		logger.info("Start Companies'Performance Forecast Grab ...");
		String emurl = String.format("%s%s", stockconfig.getEmUrl(), getQueryParameters());
		logger.debug("Grab from URL: " + emurl);
		logger.info("End  request,prepare to receive response ...");
		String resData = stockhttpgrab.httpGetBuffer(emurl, getHeader()).toString();// charset=gb2312
		// logger.debug("Response Length:"+stockhttpgrab.getChunkLength());
		// stockhttpgrab.deleteChunkBuffer();
		logger.info(resData);
		JSONObject stockNotice = new JSONObject(resData.split("= ")[1].split(";")[0]);
		JSONArray data = stockNotice.getJSONArray("data");
		for (int i = 0; i < data.length(); i++) {
			JSONObject noticelist = (JSONObject) data.get(i);
			JSONArray CDSY_SECUCODES = (JSONArray) noticelist.get("CDSY_SECUCODES");
			JSONObject basicdata = (JSONObject) CDSY_SECUCODES.get(0);
			String titile = basicdata.get("SECURITYCODE") + "-" + basicdata.get("SECURITYSHORTNAME") + "-"
					+ noticelist.get("NOTICETITLE").toString().split(":")[1];
			String link = String.format("%s%s%s", stockconfig.getEmFileDomain(), noticelist.get("INFOCODE"), "_1.pdf");
			logger.info("Find: " + titile + " " + link);
			if (stockNoticeDao.countNoticeLink(link) < 1) {
				stockNoticeDao.insertStockNotice(basicdata.get("SECURITYCODE").toString(), titile, link, 0);
			}
		}
	}

}

// @Override
// public Map<String, String> parseStockHtml() {
// logger.info("Start Companies'Performance Forecast Grab ...");
// JSONObject stockNotice = new JSONObject();
// Map<String, String> announcement = new HashMap<String, String>();
// String emurl = String.format("%s%s", stockconfig.getEmUrl(),
// getQueryParameters());
// logger.debug("Grab from URL: " + emurl);
// logger.info("End request,prepare to receive response ...");
// String resData = stockhttpgrab.httpGetStockResource(emurl,
// getHeader(),"gb2312");//charset=gb2312
// //logger.info(resData);
// stockNotice = JSON.parseObject(resData.split("= ")[1].split(";")[0]);
// JSONArray data = stockNotice.getJSONArray("data");
// for (int i = 0; i < data.size(); i++) {
// JSONObject noticelist = (JSONObject) data.get(i);
// JSONArray CDSY_SECUCODES = (JSONArray) noticelist.get("CDSY_SECUCODES");
// JSONObject basicdata = (JSONObject) CDSY_SECUCODES.get(0);
// String titile=basicdata.get("SECURITYCODE") + "-" +
// basicdata.get("SECURITYSHORTNAME") + "-"
// + noticelist.get("NOTICETITLE").toString().split(":")[1];
// String link=String.format("%s%s%s", stockconfig.getEmFileDomain(),
// noticelist.get("INFOCODE"), "_1.pdf");
// announcement.put(titile,link);
// logger.info("Find: "+titile+" "+link);
// }
// return announcement;
// }
// }

//
// Request
// URL:http://data.eastmoney.com/notices/getdata.ashx?StockCode=&FirstNodeType=1&CodeType=1&PageIndex=1&PageSize=20&jsObj=HmoKFyuy&SecNodeType=5&Time=
// Request Method:GET
// Status Code:200 OK
// Remote Address:211.162.48.54:80
// Response Headers
// view source
// Cache-Control:max-age=300
// Connection:keep-alive
// Content-Encoding:gzip
// Content-Type:text/html; charset=gb2312
// Date:Tue, 27 Dec 2016 09:28:48 GMT
// Expires:Tue, 27 Dec 2016 09:33:48 GMT
// Server:nginx/0.8.46
// Transfer-Encoding:chunked
// X-AspNet-Version:4.0.30319
// X-Powered-By:ASP.NET
// X-Via:1.1 guangzhou20:8 (Cdn Cache Server V2.0)
// Request Headers
// view source
// Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
// Accept-Encoding:gzip, deflate, sdch
// Accept-Language:zh-CN,zh;q=0.8,zh-TW;q=0.6
// Cache-Control:no-cache
// Connection:keep-alive
// Cookie:st_pvi=62750235173910; emstat_bc_emcount=19799960313412653463;
// HAList=a-sz-002040-%u5357%u4EAC%u6E2F%2Ca-sz-002446-%u76DB%u8DEF%u901A%u4FE1;
// em_hq_fls=old; emstat_ss_emcount=0_1482333315_2278864179;
// st_si=19430928596321
// Host:data.eastmoney.com
// Pragma:no-cache
// Upgrade-Insecure-Requests:1
// User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36
// (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36
// Query String Parameters
// view source
// view URL encoded
// StockCode:
// FirstNodeType:1
// CodeType:1
// PageIndex:1
// PageSize:20
// jsObj:HmoKFyuy
// SecNodeType:5
// Time:
//
//

//
// {
// "data":[
// {
// "NOTICEDATE":"2016-12-27T00:00:00+08:00",
// "ATTACHTYPE":"0",
// "ATTACHSIZE":64,
// "ENDDATE":"2016-12-27T00:00:00+08:00",
// "NOTICETITLE":"600400:红豆股份2016年年度业绩预增公告",
// "INFOCODE":"AN201612260219284665",
// "CDSY_SECUCODES":[
// {
// "SECURITYVARIETYCODE":"1000008764",
// "SECURITYTYPECODE":"058001001",
// "SECURITYCODE":"600400",
// "SECURITYFULLNAME":"红豆股份",
// "SECURITYSHORTNAME":"红豆股份",
// "SECURITYTYPE":"A股",
// "TRADEMARKETCODE":"069001001001",
// "TRADEMARKET":"上交所主板",
// "LISTINGSTATE":"0",
// "COMPANYCODE":"10000320",
// "Eid":0
// }
// ],
// "ANN_RELCOLUMNS":[
// {
// "COLUMNCODE":"001002004001",
// "COLUMNNAME":"业绩预告",
// "NodeName":null,
// "Eid":0,
// "Order":189
// }
// ],
// "ANN_RELCODES":[
// {
// "CODETYPE":"058001001",
// "CODEMARKET":"069001001001",
// "SECURITYCODE":"600400",
// "COMPANYCODE":null,
// "Eid":0
// }
// ],
// "EUTIME":"2016-12-26T18:17:12+08:00",
// "TABLEID":160000002524905860,
// "Order":189
// },