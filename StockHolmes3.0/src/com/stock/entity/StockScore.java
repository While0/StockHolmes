package com.stock.entity;

public class StockScore {
	private int stockid;
	private String stockcode;
	private int pescore;
	private int risescore;
	private int perisescore;
	private int valuescore;
	private int gainscore;
	private int totalscore;
	private StockNotice stocknotice;

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

	public int getPescore() {
		return pescore;
	}

	public void setPescore(int pescore) {
		this.pescore = pescore;
	}

	public int getRisescore() {
		return risescore;
	}

	public void setRisescore(int risescore) {
		this.risescore = risescore;
	}

	public int getPerisescore() {
		return perisescore;
	}

	public void setPerisescore(int perisescore) {
		this.perisescore = perisescore;
	}

	public int getValuescore() {
		return valuescore;
	}

	public void setValuescore(int valuescore) {
		this.valuescore = valuescore;
	}

	public int getGainscore() {
		return gainscore;
	}

	public void setGainscore(int gainscore) {
		this.gainscore = gainscore;
	}

	public int getTotalscore() {
		return totalscore;
	}

	public void setTotalscore(int totalscore) {
		this.totalscore = totalscore;
	}

	public StockNotice getStocknotice() {
		return stocknotice;
	}

	public void setStocknotice(StockNotice stocknotice) {
		this.stocknotice = stocknotice;
	}

}
