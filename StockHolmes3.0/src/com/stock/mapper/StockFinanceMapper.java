package com.stock.mapper;

import java.util.List;

import com.stock.entity.StockFinance;

public interface StockFinanceMapper {

	public void insertStockFinance(StockFinance stockFinance);

	public List<StockFinance> selectStockFinance();

	public List<String> selectStockCode();
}
