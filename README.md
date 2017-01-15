# StockHolmes
StockHolmes是一个Java项目，部署于TOMCAT。
目前A股上市公司有3000家，年底可能会增加到3700家。每年的季度预告和正式报告超过2万份。
如此数量的财务预告和报告全都读一遍几乎是不可能的，将耗费大量的时间和精力。
StockHolmes实现定时从巨潮和东财抓取业绩预告，过滤出预告中的增长和盈利数据，
并使用一套策略，选取符合要求的股票邮件通知。

StockHolmes基于以下开源架构：
configuration2
httpclient
Spring 4.3
Mybatis 3.8
sqlite 3.0

