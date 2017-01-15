package com.stock.mapper;

import java.util.List;

import com.stock.entity.StockNotice;

public interface StockNoticeMapper {

	public Integer selectIdByCode(String stockcode);

	public void insertStockNotice(StockNotice stockNotice);

	public int countNoticeLink(String noticelink);

	public List<StockNotice> selectLink(int isdownload);

	public List<StockNotice> selectUnloadLink(List<Integer> downloadstatus);

	public void updateStockNotice(StockNotice stockNotice);
}
