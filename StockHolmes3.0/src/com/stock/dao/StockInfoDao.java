package com.stock.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.stock.entity.StockInfo;
import com.stock.mapper.StockInfoMapper;

@Repository("stockInfoDao")
public class StockInfoDao {

	private StockInfoMapper stockInfoMapper;
	private StockInfo stockInfo = new StockInfo();

	public void insertStockInfo(String stockcode, String stockname, String stockpe, String stockvalue,
			int Isanalysised) {
		// stockInfo.setStockid(stockid);
		stockInfo.setStockcode(stockcode);
		stockInfo.setStockname(stockname);
		stockInfo.setStockpe(stockpe);
		stockInfo.setStockvalue(stockvalue);
		Date date = new Date();
		SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		stockInfo.setCreatetime(simpledateformat.format(date).toString());
		stockInfoMapper.insertStockInfo(stockInfo);
	}

	public void updateStockStatus(int stockid, int isanalysised) {
		stockInfo.setStockid(stockid);
		stockInfo.setIsanalysised(isanalysised);// 1表示已经过分析；0表示未分析
		stockInfoMapper.updateStockStatus(stockInfo);
	}

	public List<StockInfo> selectFinanceInfo(int Isanalysised) {
		return stockInfoMapper.selectFinanceInfo(Isanalysised);
	}

	public StockInfoMapper getStockInfoMapper() {
		return stockInfoMapper;
	}

	@Resource
	public void setStockInfoMapper(StockInfoMapper stockInfoMapper) {
		this.stockInfoMapper = stockInfoMapper;
	}

	public StockInfo getStockInfo() {
		return stockInfo;
	}

	public void setStockInfo(StockInfo stockInfo) {
		this.stockInfo = stockInfo;
	}

}
