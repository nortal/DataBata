create or replace function f_get_next_year(in_date date) return date
is
 l_date date;
begin
 l_date := add_months(in_date,12);
 return trunc(l_date,'YEAR');
end;
/

create table test_f ( date1 date, date2 date);

create or replace procedure fill_dates is
  l_old_date date;
  l_new_date date;
begin
  for i in 2000..2010
  loop
      l_old_date := to_date('01.01'||to_char(i),'dd.mm.yyyy');
      l_new_date := f_get_next_year(l_old_date);
      insert into test_f values(l_old_date,l_new_date);
  end loop;
end;
/
call fill_dates();

select count(*) from test_f; --must be 11