create table test(
  id numeric(18,0), 
  test character varying(50),
  comments character varying(100)
);

insert into test(id,test) values(11, 'INSERT first value');
insert into test(id,test) values(12, 'INSERT second value');
insert into test(id,test) values(13, 'Lisa kolmas väärtus (äöõüžšÄÖÕÜŠŽ)');
update test set test='INSERT second value updated.' WHERE id = 12 AND test='INSERT second value';