package com.stock.startup;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StockStartUp extends HttpServlet {

	private static final long serialVersionUID = 1L;
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.info("START TO DO THAT THING..................................");
		// ApplicationContext context =
		// ContextLoader.getCurrentWebApplicationContext();
		ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContextCommon.xml");
	}

}
