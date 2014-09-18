-- PG_BLOCK_START
BEGIN
  insert into test(id,test) values(-3, 'RAW test with go and new line');
END;
-- PG_BLOCK_END
go
