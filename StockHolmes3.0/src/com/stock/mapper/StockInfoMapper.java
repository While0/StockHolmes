package com.stock.mapper;

import java.util.List;

import com.stock.entity.StockInfo;

public interface StockInfoMapper {

	public void insertStockInfo(StockInfo stockInfo);

	public List<StockInfo> selectFinanceInfo(int Isanalysised);

	public void updateStockStatus(StockInfo stockInfo);
}
