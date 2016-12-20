DROP TABLE table1;
set tolerate.dataerror.write=tolerate;
set tolerate.dataerror.readext=tolerate;
set tolerate.numsplitechar.lessthan.numberfields.exread=tolerate;


CREATE TABLE table1 (
       a STRING,
       b ARRAY<STRING>,
       c ARRAY<MAP<STRING,STRING> >,
       d MAP<STRING,ARRAY<STRING> >
       ) STORED AS TEXTFILE;
DESCRIBE table1;
DESCRIBE EXTENDED table1;

LOAD DATA LOCAL INPATH '../data/files/create_nested_type.txt' OVERWRITE INTO TABLE table1;

SELECT * from table1;

DROP TABLE table1;
