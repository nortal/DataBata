create table test1(
  id number(10) not null primary key, 
  test varchar2(100 char),
  test_date date
);

\i test1_uppercase.sql

insert into test1(id,test,test_date) values(-1, 'Oracle specific syntax. must be transformed to other databases by transformation rules', sysdate);
insert into test1(id,test,test_date) values(-1, 'MSS specific syntax. must be transformed and then skipped', getdate());