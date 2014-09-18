CREATE FUNCTION f_get_next_year(@in_date date)
RETURNS DATE
AS
BEGIN
  DECLARE @out_date DATE;
  select @out_date = dateadd(mm,12, @in_date);
  select @out_date = convert(datetime, convert(varchar(4),datepart(yy,@out_date))+'-1-1');
  RETURN @out_date;
END
go

CREATE TABLE test_f ( date1 date, date2 date);


CREATE PROCEDURE fill_dates
AS
BEGIN
  declare @old_date date
  declare @new_date date
  declare @i smallint

  select @i = 2000
  WHILE @i <= 2010
    begin
      select @old_date = convert(datetime, convert(varchar(4),@i)+'-1-1')
      select @new_date = dbo.f_get_next_year(@old_date)
      insert into test_f values(@old_date,@new_date)
      select @i = @i + 1
    END
END
go

exec dbo.fill_dates;


 --must be 11
select count(*) from test_f;


