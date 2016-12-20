DROP TABLE table1;
DROP TABLE table2;
DROP TABLE table3;
set tolerate.dataerror.write=tolerate;
set tolerate.dataerror.readext=tolerate;
set tolerate.numsplitechar.lessthan.numberfields.exread=tolerate;


CREATE TABLE table1 (a STRING, b STRING) STORED AS TEXTFILE;
DESCRIBE table1;
DESCRIBE EXTENDED table1;

CREATE TABLE table2 LIKE table1;
DESCRIBE table2;
DESCRIBE EXTENDED table2;

CREATE TABLE IF NOT EXISTS table2 LIKE table1;

CREATE EXTERNAL TABLE IF NOT EXISTS table2 LIKE table1;

CREATE EXTERNAL TABLE IF NOT EXISTS table3 LIKE table1;
DESCRIBE table3;
DESCRIBE EXTENDED table3;

INSERT OVERWRITE TABLE table1 SELECT key, value FROM src WHERE key = 86;
INSERT OVERWRITE TABLE table2 SELECT key, value FROM src WHERE key = 100;

SELECT * FROM table1;
SELECT * FROM table2;

DROP TABLE table1;
DROP TABLE table2;
DROP TABLE table3;
