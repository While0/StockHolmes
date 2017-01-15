CREATE TABLE "stock_finance"
  (
    `stock_id` INTEGER,
    `stock_code` TEXT NOT NULL,
    `stock_rise`  REAL NOT NULL,
    `stock_gains` REAL NOT NULL,
    `stock_rise_desc` TEXT NOT NULL,
    `stock_gains_desc` TEXT NOT NULL,
    `stock_gainreason` BLOB,
    `create_time` TEXT,
    PRIMARY KEY(`stock_id`)
  );
CREATE TABLE "stock_info"
  (
    `stock_id` INTEGER,
    `stock_code` TEXT,
    `stock_name` TEXT,
    `stock_pe` TEXT,
    `stock_value` TEXT,
    `create_time` TEXT,
    `Isanalysised` INTEGER,
    PRIMARY KEY(`stock_id`)
  );
CREATE TABLE "stock_notice"
  (
    `stock_id` INTEGER,
    `stock_code` TEXT NOT NULL,
    `notice_title` TEXT,
    `notice_link` TEXT NOT NULL,
    `download_time` TEXT,
    `isdownload` INTEGER,
    PRIMARY KEY(`stock_id`)
  );
CREATE TABLE "stock_score"
  (
    `stock_id` INTEGER,
    `stock_code` TEXT,
    `pe_score`      INTEGER,
    `rise_score`    INTEGER,
    `pe_rise_score` INTEGER,
    `value_score`   INTEGER,
    `gain_score`    INTEGER,
    `total_score`   INTEGER,
    PRIMARY KEY(`stock_id`)
  );
