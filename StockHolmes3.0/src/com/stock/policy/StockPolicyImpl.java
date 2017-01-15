package com.stock.policy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.config.StockConfig;
import com.stock.dao.StockInfoDao;
import com.stock.dao.StockScoreDao;

import com.stock.entity.StockInfo;
import com.stock.entity.StockScore;
import com.stock.grab.StockGrab;
import com.stock.service.StockSendEmail;
import com.stock.service.StockHttpGrab;
import com.stock.service.StockNoticeAnalysis;
import com.stock.util.StockTextHandler;
import com.stock.service.StockNoticeConverter;

@Service("stockpolicyimpl")
public class StockPolicyImpl implements StockPolicy {
	@Autowired
	private StockGrab stockcninfgrab;
	@Autowired
	private StockGrab stockeastmygrab;
	@Autowired
	private StockGrab stockEmReportGrab;
	@Autowired
	private StockGrab stockmetainfograb;
	@Autowired
	private StockNoticeConverter stocknoticeconverter;
	@Autowired
	private StockNoticeAnalysis stockReportAnalyzer;
	@Autowired
	private StockHttpGrab stockhttpgrab;
	@Autowired
	private StockConfig stockconfig;
	@Autowired
	private StockTextHandler stocktexthandler;
	@Autowired
	private StockSendEmail stockemail;

	@Autowired
	private StockInfoDao stockInfoDao;

	@Autowired
	private StockScoreDao stockScoreDao;

	private StockScore stockScore = new StockScore();
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public void grabStockNotice() {
		logger.info("Start to grab...........");
		stockcninfgrab.parseStockHtml();
		stockeastmygrab.parseStockHtml();
		stockEmReportGrab.parseStockHtml();
		logger.info("Finish Grabing...........");
	}

	public void carryStockNotice() {
		logger.info("Start to download Notice......");
		stocktexthandler.clearOldFile(stockconfig.getDownLoadPath(), stockconfig.getNoticeSaveLong());
		stockhttpgrab.downLoadLink(stockconfig.getDownLoadPath());
		stocknoticeconverter.transformer(stockconfig.getDownLoadPath(), stockconfig.getTransformPath());
	}

	public void refineStockFinance() {
		logger.info("Start to search notice finance data......");
		stockReportAnalyzer.stockDecorater(stockconfig.getTransformPath());
		logger.info("Finish searching......");
		logger.info("Start to grab stock basic info.......");
		stockmetainfograb.parseStockHtml();
		logger.info("Finish grabing info.......");
	}

	@Transactional
	public void analysisStock() {
		logger.info("Start to analysis.......");
		List<StockInfo> stockfinanceList = stockInfoDao.selectFinanceInfo(0);
		StringBuffer messagebuffer = new StringBuffer();
		for (int i = 0; i < stockfinanceList.size(); i++) {
			StockInfo stockinfo = (StockInfo) stockfinanceList.get(i);
			float stockrise = Float.parseFloat((String) stockinfo.getStockFinance().getStockrise());
			float stockPE = Float.parseFloat((String) stockinfo.getStockpe());
			float stockvalue = Float.parseFloat((String) stockinfo.getStockvalue());
			float stockgains = Float.parseFloat((String) stockinfo.getStockFinance().getStockgains());
			int totalscore = 0;
			stockScore.setTotalscore(0);
			if (stockPE <= stockconfig.getStockMaxPE()) {
				totalscore += 10;
				stockScore.setPescore(10);
			} else {
				stockScore.setPescore(0);
			}

			if (stockrise >= stockconfig.getStockMinRise()) {
				// logger.debug("debug............................."+stockrise);
				stockScore.setRisescore(10);
				totalscore += 10;
			} else {
				stockScore.setRisescore(0);
			}

			if (stockconfig.getStockRiseBiger() && stockrise >= stockPE) {
				stockScore.setPerisescore(10);
				totalscore += 10;
			} else {
				stockScore.setPerisescore(0);
			}

			if (stockvalue <= stockconfig.getStockMaxValue()) {
				stockScore.setValuescore(10);
				totalscore += 10;
			} else {
				stockScore.setValuescore(0);
			}

			if (stockgains >= stockconfig.getStockMinGains()) {
				stockScore.setGainscore(10);
				totalscore += 10;
			} else {
				stockScore.setGainscore(0);
			}

			stockScore.setStockid(stockinfo.getStockid());
			stockScore.setStockcode(stockinfo.getStockcode());
			stockScore.setTotalscore(totalscore);
			stockScoreDao.insertStockScore(stockScore);
			stockInfoDao.updateStockStatus(stockinfo.getStockid(), 1);
			String message = stockinfo.getStockname() + ":  " + stockinfo.getStockcode() + "  " + "PE:  "
					+ stockinfo.getStockpe() + "  " + "市值:  " + stockinfo.getStockvalue() + "亿 "
					+ stockinfo.getStockFinance().getStockrisedesc() + "  "
					+ stockinfo.getStockFinance().getStockgainsdesc();
			logger.debug(message);
			logger.debug("stockcode: " + stockinfo.getStockcode() + " score: " + totalscore);
			if (totalscore >= stockconfig.getStockMinScore()) {
				logger.info("Is that it?...................");
				logger.info(message);
				messagebuffer.append(message + System.getProperty("line.separator"));
				messagebuffer.append(
						"雪球链接: " + stockhttpgrab.composeURL(stockconfig.getSnowBallLink(), stockinfo.getStockcode())
								+ System.getProperty("line.separator"));
				messagebuffer.append(stockinfo.getStockFinance().getStockgainreason());
				messagebuffer.append(System.getProperty("line.separator"));
			}
			totalscore = 0;
		}
		if (messagebuffer.length() > 0) {
			stocktexthandler.writeTextFile(messagebuffer, stockconfig.getMsgFile(), false);
		}
		logger.info("Finish analysising.......");
	}

	public void mailMsg() {
		stockemail.sendEmail(stockconfig.getMsgFile());
	}

	@Transactional
	public void saveWantedNotice() {
		logger.info("Saving the notice file which is found.......");
		List<String> noticetitleList = stockScoreDao.selectNoticeTitle(stockconfig.getStockMinScore());
		int noticequantity = noticetitleList.size();
		if (noticequantity > 0) {
			for (int i = 0; i < noticequantity; i++) {
				stocktexthandler.copyFile(stockconfig.getDownLoadPath(), stockconfig.getWantedPath(),
						noticetitleList.get(i).concat(".PDF"));
			}
		}
	}

}
