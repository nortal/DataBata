-- PG_BLOCK_START
BEGIN
  insert into test(id,test) values(-3, 'RAW test with slash and new line');
END;
-- PG_BLOCK_END
/
