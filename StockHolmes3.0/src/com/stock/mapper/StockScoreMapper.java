package com.stock.mapper;

import com.stock.entity.StockScore;

import java.util.List;

public interface StockScoreMapper {

	public void insertStockScore(StockScore stockScore);

	public List<String> selectNoticeTitle(int stockminscore);
}
