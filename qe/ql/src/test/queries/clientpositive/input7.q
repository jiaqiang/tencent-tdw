set tolerate.dataerror.write=tolerate;
set tolerate.dataerror.readext=tolerate;
set tolerate.numsplitechar.lessthan.numberfields.exread=tolerate;

CREATE TABLE dest1(c1 DOUBLE, c2 INT) STORED AS TEXTFILE;

EXPLAIN
FROM src1
INSERT OVERWRITE TABLE dest1 SELECT NULL, src1.key;

FROM src1
INSERT OVERWRITE TABLE dest1 SELECT NULL, src1.key;

SELECT dest1.* FROM dest1;
