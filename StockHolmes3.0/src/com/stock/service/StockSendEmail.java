package com.stock.service;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import com.stock.util.StockTextHandler;
import com.stock.config.StockConfig;

@Service("stocksendemail")
public class StockSendEmail {
	@Autowired
	private StockConfig stockconfig;
	@Autowired
	private StockTextHandler stocktexthandler;
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public StockSendEmail() {

	}

	private boolean writeMail(String MsgToSend, SimpleMailMessage mailMessage) {
		StringBuffer messagebuffer = stocktexthandler.readToStringBuffer(MsgToSend);
		if (messagebuffer != null) {
			messagebuffer.append(System.getProperty("line.separator"));
			messagebuffer.append(System.getProperty("line.separator"));
			messagebuffer.append("--------------------------" + System.getProperty("line.separator"));
			messagebuffer.append(" " + stockconfig.getEmailSignature() + System.getProperty("line.separator"));
			messagebuffer.append("--------------------------" + System.getProperty("line.separator"));
			logger.info("写邮件正文;");
			mailMessage.setText(messagebuffer.toString());
			stocktexthandler.clearTextFile(MsgToSend);
			return true;
		} else {
			logger.info("没有消息发送.");
		}
		return false;
	}

	public void sendEmail(String MsgToSend) {
		String from = stockconfig.getEmailFrom();
		String to = stockconfig.getEmailTo();
		String subject = stockconfig.getEmailSubject();
		String host = stockconfig.getEmailSMTP();
		String user = stockconfig.getEmailUser();
		String pwd = stockconfig.getEmailPasswd();

		JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
		senderImpl.setHost(host);
		// 建立邮件消息
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		// 设置收件人，寄件人 用数组发送多个邮件
		// String[] array = new String[] {"sun111@163.com","sun222@sohu.com"};
		// mailMessage.setTo(array);

		if (writeMail(MsgToSend, mailMessage)) {
			logger.info("Start to send email..............");
			logger.info("设定邮箱寄信人、收信人，邮件标题;");
			mailMessage.setTo(to);
			mailMessage.setFrom(from);
			mailMessage.setSubject(subject);
			logger.info("设置邮箱用户名密码并认证;");
			senderImpl.setUsername(user);
			senderImpl.setPassword(pwd);

			Properties properties = new Properties();
			properties.put("mail.smtp.auth", "true"); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确
			properties.put("mail.smtp.timeout", "25000");
			senderImpl.setJavaMailProperties(properties);
			// 发送邮件
			logger.info("开始发送邮件;");
			senderImpl.send(mailMessage);
			logger.info("Email has been sent to: " + to);
		}

	}

}
