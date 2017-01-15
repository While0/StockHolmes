package com.stock.entity;

public class StockInfo {
	private int stockid;
	private String stockcode;
	private String stockname;
	private String stockpe;
	private String stockvalue;
	private String createtime;// yyyy-MM-dd HH:mm:ss
	private int Isanalysised;
	private StockFinance stockFinance;

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

	public String getStockname() {
		return stockname;
	}

	public void setStockname(String stockname) {
		this.stockname = stockname;
	}

	public String getStockpe() {
		return stockpe;
	}

	public void setStockpe(String stockpe) {
		this.stockpe = stockpe;
	}

	public String getStockvalue() {
		return stockvalue;
	}

	public void setStockvalue(String stockvalue) {
		this.stockvalue = stockvalue;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public StockFinance getStockFinance() {
		return stockFinance;
	}

	public void setStockFinance(StockFinance stockFinance) {
		this.stockFinance = stockFinance;
	}

	public int getIsanalysised() {
		return Isanalysised;
	}

	public void setIsanalysised(int isanalysised) {
		Isanalysised = isanalysised;
	}

}
