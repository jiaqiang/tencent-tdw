-- test for loading into tables with the correct file format
-- test for loading into partitions with the correct file format

DROP TABLE T1;
set tolerate.dataerror.write=tolerate;
set tolerate.dataerror.readext=tolerate;
set tolerate.numsplitechar.lessthan.numberfields.exread=tolerate;

CREATE TABLE T1(name STRING) STORED AS TEXTFILE;
LOAD DATA LOCAL INPATH '../data/files/kv1.txt' INTO TABLE T1;
SELECT COUNT(1) FROM T1;

DROP TABLE T2;
CREATE TABLE T2(name STRING) STORED AS SEQUENCEFILE;
LOAD DATA LOCAL INPATH '../data/files/kv1.seq' INTO TABLE T2;
SELECT COUNT(1) FROM T2;

DROP TABLE T3;
CREATE TABLE T3(name STRING, ds STRING) PARTITION BY list(ds) 
(PARTITION p0 VALUES IN ('2008-04-09'))
STORED AS TEXTFILE;
INSERT OVERWRITE TABLE T3 SELECT key, '2008-04-09' FROM src; 
SELECT COUNT(1) FROM T3 where T3.ds='2008-04-09';

DROP TABLE T4;
CREATE TABLE T4(name STRING, ds STRING) PARTITION BY list(ds) 
(PARTITION p0 VALUES IN ('2008-04-09'))
STORED AS SEQUENCEFILE;
INSERT OVERWRITE TABLE T3 SELECT key, '2008-04-09' FROM src;
SELECT COUNT(1) FROM T4 where T4.ds='2008-04-09';

DESCRIBE EXTENDED T1;
DESCRIBE EXTENDED T2;

DROP TABLE T1;
DROP TABLE T2;
DROP TABLE T3;
DROP TABLE T4;
