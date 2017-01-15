package com.stock.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.stock.dao.StockNoticeDao;
import com.stock.entity.StockNotice;

@Service("StockHttpGrab")
public class StockHttpGrab {

	@Autowired
	private StockNoticeDao stockNoticeDao;
	protected Logger logger = LoggerFactory.getLogger(getClass());
	// public StringBuffer chunkbuffer = new StringBuffer();

	public String composeURL(String inferface, String stockCode) {
		String url = null;
		if (StringUtils.isNotEmpty(inferface) && StringUtils.isNotEmpty(stockCode)) {
			String subcode = stockCode.substring(0, 2);
			if (subcode.equals("30") || subcode.equals("00")) {
				url = String.format("%s%s%s", inferface, "sz", stockCode, inferface);
			} else if (subcode.equals("60")) {
				url = String.format("%s%s%s", inferface, "sh", stockCode, inferface);
			}
		}
		return url;
	}

	// I want to say,fuck Cninfo.Why do u change the notice link's format?For
	// what?
	public String montageCninfoURL(String cninfoStartPage, String noticeURI) {
		if (StringUtils.isNotEmpty(noticeURI)) {
			cninfoStartPage = cninfoStartPage.split("/cninfo-new")[0];
			return String.format("%s%s", cninfoStartPage, noticeURI);
		}
		return "";
	}

	public static CloseableHttpClient createHttpClient(int socketTimeout) {
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(9000).setSocketTimeout(socketTimeout)
				.build();
		return HttpClients.custom().setDefaultRequestConfig(requestConfig).build();

	}

	private static CloseableHttpClient HttpClient() {
		return createHttpClient(9000);
	}

	@Transactional
	public void downLoadLink(String DownLoadPath) {
		List<StockNotice> linkList = stockNoticeDao.selectUnloadLink();
		int linkquantity = linkList.size();
		int downloadcounter = 0;
		if (linkquantity > 0) {
			for (int i = 0; i < linkquantity; i++) {
				StockNotice stocknotice = (StockNotice) linkList.get(i);
				logger.info("Download: " + stocknotice.getNoticetitle());
				logger.info("Link is:" + stocknotice.getNoticelink());
				File file = new File(DownLoadPath + stocknotice.getNoticetitle() + ".PDF");
				if (file.exists()) {
					if (file.isFile() && file.length() > 0) {
						stockNoticeDao.updateStockNotice(stocknotice.getStockid(), 2);
					} // 2表示不下载；0表示未开始下载
				} else if (!file.exists()) {
					if (downLoadUrlResouce(DownLoadPath, stocknotice.getNoticelink(),
							stocknotice.getNoticetitle() + ".PDF")) {
						downloadcounter += 1;
						stockNoticeDao.updateStockNotice(stocknotice.getStockid(), 1);// 1表示下载成功
					} else {
						stockNoticeDao.updateStockNotice(stocknotice.getStockid(), 3);
					} // 3表示下载失败
				}
			}

			if (downloadcounter == linkquantity) {
				logger.info("Total notice: " + linkquantity);
				logger.info("Downloaded: " + downloadcounter);
				logger.info("Finish downloading all the notice!");
			} else {
				logger.info("Total notice: " + linkquantity);
				logger.info("Downloaded: " + downloadcounter);
				logger.info("Not all the notice's been downloaded!");
			}
		} else {
			logger.info("Nothing to download.The link's number is 0.");
		}
	}

	public boolean downLoadUrlResouce(String savepath, String resurl, String fileName) {
		URL url = null;
		HttpURLConnection httpurlconnection = null;
		InputStream Inputstream = null;
		FileOutputStream fileoutputstream = null;
		try {
			url = new URL(resurl);// 建立http连接，得到连接对象
			httpurlconnection = (HttpURLConnection) url.openConnection();
			httpurlconnection.setConnectTimeout(9000);
			httpurlconnection.setReadTimeout(9000);
			httpurlconnection.connect();
			if (httpurlconnection.getResponseCode() == 200) {
				Inputstream = httpurlconnection.getInputStream();
				byte[] data = getByteData(Inputstream);// 转化为byte数组
				File file = new File(savepath);
				if (!file.exists()) {
					file.mkdirs();
				}
				File targetfile = new File(file + File.separator + fileName);
				fileoutputstream = new FileOutputStream(targetfile);
				fileoutputstream.write(data);
				logger.info("Download successfully!");
				return true;
			} else {
				logger.error("Response ERROR: " + httpurlconnection.getResponseCode() + " "
						+ httpurlconnection.getResponseMessage());
				return false;
			}
		} catch (ConnectException e) {
			logger.error("Download refused: " + resurl);
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			logger.error("Download TIMED OUT: " + resurl);
			e.printStackTrace();
		} catch (MalformedURLException e) {
			logger.error("Can not download from this URL: " + fileName);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Writing ERROR: " + fileName);
			e.printStackTrace();
		} finally {
			try {
				if (null != fileoutputstream)
					fileoutputstream.close();
				if (null != Inputstream)
					Inputstream.close();
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Download error:+ " + fileName);
			}

		}
		return false;
	}

	private byte[] getByteData(InputStream inputstream) throws IOException {
		byte[] by = new byte[1024];
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		int length = 0;
		while ((length = inputstream.read(by)) != -1) {
			bytearrayoutputstream.write(by, 0, length);
		}
		if (null != bytearrayoutputstream) {
			bytearrayoutputstream.close();
		}
		return bytearrayoutputstream.toByteArray();
	}

	private StringBuffer getBufferData(InputStream inputstream, String charset) throws IOException {
		BufferedReader bufferedreader;
		try {
			bufferedreader = new BufferedReader(new InputStreamReader(inputstream, charset), 2048);
			StringBuffer stringbuffer = new StringBuffer();
			String line = null;
			while ((line = bufferedreader.readLine()) != null) {
				stringbuffer.append(line);
			}
			return stringbuffer;
		} catch (UnsupportedEncodingException e) {
			logger.error("Wrong CharSet: " + charset);
			e.printStackTrace();
		}
		return null;
	}

	public StringBuffer httpGetBuffer(String url, Map<String, String> headers) {
		CloseableHttpClient httpclient = HttpClient();
		HttpGet httpGet = new HttpGet(url);
		InputStream inputstream = null;
		String charset = null;
		String encoding="";
		Header Encoding, ContentType;
		int inputstreamsize = 0;
		byte[] chunkbyte = new byte[130];
		StringBuffer chunkbuffer = new StringBuffer();
		try {
			if (headers != null) {
				for (String name : headers.keySet())
					httpGet.setHeader(name, headers.get(name));
				HttpGet httpget = new HttpGet(url);
				CloseableHttpResponse httpResponse = httpclient.execute(httpget);
				try {
					HttpEntity httpentity = httpResponse.getEntity();
					inputstream = httpentity.getContent();
					int status = httpResponse.getStatusLine().getStatusCode();
					Encoding = httpResponse.getFirstHeader("Transfer-Encoding");
					ContentType = httpResponse.getFirstHeader("Content-Type");
					if(Encoding!=null){
						encoding=Encoding.getValue();
					}
					if (ContentType.getValue().indexOf("charset") > 0) {
						charset = ContentType.getValue().split("=")[1].trim();
						logger.debug("Use the charset to parse response: " + charset);
					}
					if (httpentity != null && status == 200) {
						if (encoding.equalsIgnoreCase("chunked")) {
							while ((inputstreamsize = inputstream.read(chunkbyte, 0, chunkbyte.length)) != -1) {
								chunkbuffer.append(new String(chunkbyte, 0, inputstreamsize, charset));
							}
							logger.debug("chunk length: " + chunkbuffer.length());
							return chunkbuffer;
						} else {
							return getBufferData(inputstream, charset);
						}
					} else if (status != 200) {
						logger.error("HttpResponse Error: " + status);
					}
				} finally {
					httpResponse.close();
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// public int getChunkLength() {
	// return chunkbuffer.length();
	// }
	//
	// public void deleteChunkBuffer() {
	// chunkbuffer.delete(0, getChunkLength());
	// }

}
