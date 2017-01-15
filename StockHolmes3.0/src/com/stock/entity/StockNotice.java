package com.stock.entity;

public class StockNotice {
	private int stockid;
	private String stockcode;
	private String noticetitle;
	private String noticelink;
	private String downloadtime;// yyyy-MM-dd HH:mm:ss
	private int isdownload;

	public int getStockid() {
		return stockid;
	}

	public void setStockid(int stockid) {
		this.stockid = stockid;
	}

	public String getStockcode() {
		return stockcode;
	}

	public void setStockcode(String stockcode) {
		this.stockcode = stockcode;
	}

	public String getNoticetitle() {
		return noticetitle;
	}

	public void setNoticetitle(String noticetitle) {
		this.noticetitle = noticetitle;
	}

	public String getNoticelink() {
		return noticelink;
	}

	public void setNoticelink(String noticelink) {
		this.noticelink = noticelink;
	}

	public String getDownloadtime() {
		return downloadtime;
	}

	public void setDownloadtime(String downloadtime) {
		this.downloadtime = downloadtime;
	}

	public int isIsdownload() {
		return isdownload;
	}

	public void setIsdownload(int isdownload) {
		this.isdownload = isdownload;
	}
}
