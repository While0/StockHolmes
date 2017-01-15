package com.stock.service;

import java.io.File;

import com.alibaba.fastjson.JSONObject;

public interface StockNoticeAnalysis {

	public JSONObject readStockNotice(File noticeFile);

	public JSONObject stockGains(JSONObject stockfinancebit);

	public void stockDecorater(String TransformPath);

}