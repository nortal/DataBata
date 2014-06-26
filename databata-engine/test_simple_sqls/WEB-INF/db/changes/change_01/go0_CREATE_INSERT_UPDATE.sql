create table test(
  id number(10), 
  test varchar2(50 char),
  comments varchar2(100)
);

insert into test(id,test) values(11, 'INSERT first value');
insert into test(id,test) values(12, 'INSERT second value');
insert into test(id,test) values(13, 'Lisa kolmas väärtus (äöõüžšÄÖÕÜŠŽ)');
update test set test='INSERT second value updated.' WHERE id = 12 AND test='INSERT second value';