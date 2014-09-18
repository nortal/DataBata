create or replace FUNCTION f_get_next_year(@in_date date)
RETURNS DATE
AS
BEGIN
  DECLARE @out_date DATE
  select dateadd(mm,12, @in_date) into @out_date
  RETURN select ymd(datepart(yy,dateadd(yy,1,@out_date)),1,1)
END
go

create table test_f ( date1 date, date2 date);

create or replace PROCEDURE fill_dates
AS
BEGIN
  declare @old_date date
  declare @new_date date
  declare @i smallint

  select @i = 2000
  WHILE @i <= 2010
    begin
      select @old_date = ymd(i,1,1)
      select @new_date = f_get_next_year(@old_date)
      insert into test_f values(@old_date,@new_date)
      select @i = @i + 1
    END
END
go

call fill_dates();

select count(*) from test_f; --must be 11