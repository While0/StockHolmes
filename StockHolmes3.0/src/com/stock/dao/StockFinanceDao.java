package com.stock.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.stock.entity.StockFinance;
import com.stock.mapper.StockFinanceMapper;

@Repository("stockFinanceDao")
public class StockFinanceDao {

	private StockFinanceMapper stockFinanceMapper;
	private StockFinance stockFinance = new StockFinance();

	public void insertStockFinance(String stockcode, String stockrise, String stockgains, String stockrisedesc,
			String stockgainsdesc, String stockgainreason) {
		stockFinance.setStockcode(stockcode);
		stockFinance.setStockrise(stockrise);
		stockFinance.setStockgains(stockgains);
		stockFinance.setStockrisedesc(stockrisedesc);
		stockFinance.setStockgainsdesc(stockgainsdesc);
		stockFinance.setStockgainreason(stockgainreason);
		Date date = new Date();
		SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		stockFinance.setCreatetime(simpledateformat.format(date).toString());
		stockFinanceMapper.insertStockFinance(stockFinance);
	}

	public List<StockFinance> selectStockFinance() {
		return stockFinanceMapper.selectStockFinance();
	}

	public List<String> selectStockCode() {
		return stockFinanceMapper.selectStockCode();
	}

	public StockFinance getStockFinance() {
		return stockFinance;
	}

	public void setStockFinance(StockFinance stockFinance) {
		this.stockFinance = stockFinance;
	}

	public StockFinanceMapper getStockFinanceMapper() {
		return stockFinanceMapper;
	}

	@Resource
	public void setStockFinanceMapper(StockFinanceMapper stockFinanceMapper) {
		this.stockFinanceMapper = stockFinanceMapper;
	}

}
