package com.stock.service;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.stock.config.StockConfig;

@Service("stocknoticeconverter")
public class StockNoticeConverter {

	@Autowired
	private StockConfig stockconfig;
	protected Logger logger = LoggerFactory.getLogger(getClass());
	public JSONObject stockmsg;

	public StockNoticeConverter() {
	}

	public void pdfConverter(String sourcepdf, String targetext) {
		String xpdf = stockconfig.getpdftotext();
		String layout = "-layout";
		String nopagebrk = "-nopgbrk";
		String[] convertcmd = new String[] { xpdf, layout, nopagebrk, sourcepdf, targetext };
		try {
			Runtime.getRuntime().exec(convertcmd);
			logger.debug("Converted: " + sourcepdf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// String encoding = "-enc";
	// String character = "GBK";
	// // 设置不打印任何消息和错误
	// String mistake = "-q";
	// 页面之间不加入分页
	// 如果isLayout为false，则设置不保持原来的layout
	// return new String[] { command, layout, encoding, character, mistake,
	// nopagebrk, source_absolutePath, target_absolutePath };

	public void transformer(String DownLoadPath, String TransformPath) {
		logger.info("Start to convert PDF......");
		File pdfpath = new File(DownLoadPath);
		if (pdfpath.isDirectory()) {
			File[] pdfileArray = pdfpath.listFiles();
			int convertedfile = 0;
			int filequantity = pdfileArray.length;
			if (pdfileArray != null) {
				for (int i = 0; i < pdfileArray.length; i++) {
					if (!pdfileArray[i].isHidden()) {
						String targetfile = TransformPath + pdfileArray[i].getName().split(".PDF")[0] + ".txt";
						logger.info("Convert: " + pdfileArray[i]);
						logger.info("To:" + targetfile);
						pdfConverter(pdfileArray[i].toString(), targetfile);
						// stocktexthandler.deleteSourceFile(pdfileArray[i],targetfile);
						convertedfile++;
					} else {
						filequantity--;
					}
				}
				try {// wait 1 second to wait the pdfconverter to finish
						// converting.
					Thread.currentThread();
					Thread.sleep(500);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
			if (convertedfile == filequantity) {
				logger.info("Done converting Notice PDF. converted :" + convertedfile);
			} else {
				logger.debug("Not all the PDF's converted;converted file : " + convertedfile + " Total file found: "
						+ filequantity);
			}
		}
	}

}