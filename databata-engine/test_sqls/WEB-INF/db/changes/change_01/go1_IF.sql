* if (*{DBMS} <> ORA)
  insert into test(id,test) values(-1, 'IF test: MUST go to non-Oracle database 1');
* end if
* if (*{DBMS} == ORA)
  insert into test(id,test) values(-1, 'IF test: MUST go to Oracle database 1');
* end if
