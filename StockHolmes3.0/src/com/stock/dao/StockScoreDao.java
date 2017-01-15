package com.stock.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;
import com.stock.entity.StockScore;
import com.stock.mapper.StockScoreMapper;

@Repository("stockScoreDao")
public class StockScoreDao {

	private StockScoreMapper stockScoreMapper;
	private StockScore stockScore = new StockScore();

	public void insertStockScore(StockScore stockScore) {
		stockScoreMapper.insertStockScore(stockScore);
	}

	public List<String> selectNoticeTitle(int stockminscore) {
		return stockScoreMapper.selectNoticeTitle(stockminscore);
	}

	public StockScoreMapper getStockScoreMapper() {
		return stockScoreMapper;
	}

	@Resource
	public void setStockScoreMapper(StockScoreMapper stockScoreMapper) {
		this.stockScoreMapper = stockScoreMapper;
	}

	public StockScore getStockScore() {
		return stockScore;
	}

	public void setStockScore(StockScore stockScore) {
		this.stockScore = stockScore;
	}

}
