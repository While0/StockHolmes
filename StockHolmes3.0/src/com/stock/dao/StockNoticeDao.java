package com.stock.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.stock.entity.StockNotice;
import com.stock.mapper.StockNoticeMapper;

@Repository("stockNoticeDao")
public class StockNoticeDao {

	private StockNoticeMapper stockNoticeMapper;
	private StockNotice stockNotice = new StockNotice();

	public Integer selectIdByCode(String stockcode) {
		Integer stockid = stockNoticeMapper.selectIdByCode(stockcode);
		return stockid;
	}

	public void insertStockNotice(String stockcode, String stocktitle, String stocklink, int isdownload) {
		stockNotice.setStockcode(stockcode);
		stockNotice.setNoticetitle(stocktitle);
		stockNotice.setNoticelink(stocklink);
		stockNotice.setIsdownload(isdownload);
		stockNoticeMapper.insertStockNotice(stockNotice);
	}

	public int countNoticeLink(String stocklink) {
		return stockNoticeMapper.countNoticeLink(stocklink);
	}

	public List<StockNotice> selectLink(int isdownload) {
		return stockNoticeMapper.selectLink(isdownload);
	}

	public List<StockNotice> selectUnloadLink() {
		List<Integer> downloadstatus = new ArrayList<Integer>();
		downloadstatus.add(0);
		downloadstatus.add(3);
		return stockNoticeMapper.selectUnloadLink(downloadstatus);
	}

	public void updateStockNotice(int stockid, int isdownload) {
		stockNotice.setStockid(stockid);
		stockNotice.setIsdownload(isdownload);
		Date date = new Date();
		SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		stockNotice.setDownloadtime(simpledateformat.format(date).toString());
		stockNoticeMapper.updateStockNotice(stockNotice);
	}

	public StockNoticeMapper getStockNoticeMapper() {
		return stockNoticeMapper;
	}

	@Resource
	public void setStockNoticeMapper(StockNoticeMapper noticeMapper) {
		this.stockNoticeMapper = noticeMapper;
	}

	public StockNotice getStockNotice() {
		return stockNotice;
	}

	public void setStockNotice(StockNotice stockNotice) {
		this.stockNotice = stockNotice;
	}

}
