DROP TABLE nulltest;
set analysisbuffer.tmp.addr=/tmp;
set tolerate.dataerror.write=tolerate;
set tolerate.dataerror.readext=tolerate;
set tolerate.numsplitechar.lessthan.numberfields.exread=tolerate;

CREATE TABLE nulltest(int_data1 INT, int_data2 INT, boolean_data BOOLEAN, double_data DOUBLE, string_data STRING) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

LOAD DATA LOCAL INPATH '../data/files/nulltest.txt' INTO TABLE nulltest;

SELECT * FROM (SELECT int_data1 , int_data2, boolean_data, double_data, string_data, AVG(int_data2) OVER(partition by boolean_data) FROM nulltest) tmp ORDER BY double_data, string_data;

SELECT * FROM (SELECT int_data1 , int_data2, boolean_data, double_data, string_data, AVG(distinct int_data2) OVER(partition by boolean_data) FROM nulltest) tmp ORDER BY double_data, string_data;

SELECT * FROM (SELECT int_data1 , int_data2, boolean_data, double_data, string_data, AVG(double_data) OVER(partition by int_data1) FROM nulltest) tmp  ORDER BY double_data, string_data;

SELECT * FROM (SELECT int_data1 , int_data2, boolean_data, double_data, string_data, AVG(distinct double_data) OVER(partition by int_data1) FROM nulltest) tmp ORDER BY double_data, string_data;

DROP TABLE nulltest;