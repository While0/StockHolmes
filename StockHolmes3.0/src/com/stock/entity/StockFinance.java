package com.stock.entity;

public class StockFinance {
	private int stockid;
	private String stockcode;
	private String stockrise;
	private String stockgains;
	private String stockrisedesc;
	private String stockgainsdesc;
	private String stockgainreason;
	private String createtime; // yyyy-MM-dd HH:mm:ss

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

	public String getStockrise() {
		return stockrise;
	}

	public void setStockrise(String stockrise) {
		this.stockrise = stockrise;
	}

	public String getStockgains() {
		return stockgains;
	}

	public void setStockgains(String stockgains) {
		this.stockgains = stockgains;
	}

	public String getStockrisedesc() {
		return stockrisedesc;
	}

	public void setStockrisedesc(String stockrisedesc) {
		this.stockrisedesc = stockrisedesc;
	}

	public String getStockgainsdesc() {
		return stockgainsdesc;
	}

	public void setStockgainsdesc(String stockgainsdesc) {
		this.stockgainsdesc = stockgainsdesc;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getStockgainreason() {
		return stockgainreason;
	}

	public void setStockgainreason(String stockgainreason) {
		this.stockgainreason = stockgainreason;
	}

}
