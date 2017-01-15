package com.stock.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.config.StockConfig;
import com.stock.policy.StockPolicy;
import com.stock.util.StockTextHandler;

@Service("StockCronJob")
public class StockCronJob {

	@Autowired
	private StockPolicy stockpolicyimpl;
	@Autowired
	private StockConfig stockconfig;
	@Autowired
	private StockTextHandler stocktexthandler;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	public void stockWork() {
		logger.info("TIME TO GO...........");
		stockpolicyimpl.grabStockNotice();
		stockpolicyimpl.carryStockNotice();
		stockpolicyimpl.refineStockFinance();
		stockpolicyimpl.analysisStock();
		stockpolicyimpl.saveWantedNotice();
		stockpolicyimpl.mailMsg();
	}

	public void stockPurge() {
		logger.info("PURGE THE WORKING SPACE...........");
		stocktexthandler.clearPath(stockconfig.getDownLoadPath());
		stocktexthandler.clearPath(stockconfig.getReadedPath());
		stocktexthandler.clearTextFile(stockconfig.getMsgFile());
		stocktexthandler.clearOldFile(System.getProperty("user.dir").replace("bin", "logs"),
				stockconfig.getLogSaveLong());
	}

}
