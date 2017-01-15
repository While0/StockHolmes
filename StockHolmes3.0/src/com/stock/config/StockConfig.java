package com.stock.config;

import org.springframework.stereotype.Service;

@Service("StockConfig")
public class StockConfig extends AbstractConfigUtil {

	public StockConfig() {
		super();
	}

	@Override
	public String getPropertiesFilename() {
		return "Stock.properties";
	}

	public String getDownLoadPath() {
		return getStrValue("DownLoadPath");
	}

	public String getTransformPath() {
		return getStrValue("TransformPath");
	}

	public String getWantedPath() {
		return getStrValue("WantedPath");
	}

	public long getNoticeSaveLong() {
		return getIntValue("NoticeSaveLong") * 60 * 1000;
	}

	public long getLogSaveLong() {
		return getLongValue("LogSaveLong") * 24 * 60 * 1000;
	}

	public String getNoticeLinkFile() {
		return getStrValue("NoticeLinkFile");
	}

	public String getReadedPath() {
		return getStrValue("ReadedPath");
	}

	public String getMsgFile() {
		return getStrValue("MsgFile");
	}

	public float getStockMaxValue() {
		return getFloatValue("policy.StockMaxValue");
	}

	public float getStockMaxPE() {
		return getFloatValue("policy.StockMaxPE");
	}

	public float getStockMinRise() {
		return getIntValue("policy.StockMinRise");
	}

	public int getStockMinGains() {
		return getIntValue("policy.StockMinGains");
	}

	public int getStockMinScore() {
		return getIntValue("policy.StockMinScore");
	}

	public boolean getStockRiseBiger() {
		return getBoolean("policy.StockRiseBiger");
	}

	public String getWebsiteUrl() {
		return getStrValue("cninfoUrl");
	}

	public String getXinpiUrl() {
		return getStrValue("XinpiUrl");
	}

	public String getXinpiFileDomain() {
		return getStrValue("XinpiFileDomain");
	}

	public String getEmUrl() {
		return getStrValue("eastmoneyurl");
	}

	public String getEmFileDomain() {
		return getStrValue("emFileDomain");
	}

	public String getGrabKeyword() {
		return getStrValue("GrabKeyword");
	}

	public String getReportKeyword() {
		return getStrValue("ReportKeyword");
	}

	public String getGainTurningKeyword() {
		return getStrValue("GainTurningKeyword");
	}

	public String getIncKeyword() {
		return getStrValue("IncKeyword");
	}

	public String getGainsKeyword() {
		return getStrValue("GainsKeyword");
	}

	public String getStockKeyword() {
		return getStrValue("StockKeyword");
	}

	public String getTencentInterface() {
		return getStrValue("TencentInterface");
	}

	public String getEmailSMTP() {
		return getStrValue("email.smtp");
	}

	public String getEmailFrom() {
		return getStrValue("email.from");
	}

	public String getEmailTo() {
		return getStrValue("email.to");
	}

	public String getEmailUser() {
		return getStrValue("email.user");
	}

	public String getEmailPasswd() {
		return getStrValue("email.pwd");
	}

	public String getEmailSubject() {
		return getStrValue("email.subject");
	}

	public String getEmailSignature() {
		return getStrValue("email.signature");
	}

	public String getSnowBallLink() {
		return getStrValue("SnowBallLink");
	}

	public String getpdftotext() {
		return getStrValue("pdftotext");
	}

}
