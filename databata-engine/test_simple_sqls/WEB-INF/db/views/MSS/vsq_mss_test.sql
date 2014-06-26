create view vsq_mss_test 
WITH ENCRYPTION
AS
select * from test
 where id>10
;