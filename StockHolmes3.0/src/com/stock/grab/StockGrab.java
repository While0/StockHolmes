package com.stock.grab;

import java.util.Map;

public interface StockGrab {

	public Map<String, String> getHeader();

	public void parseStockHtml();

}