* if (*{DBMS} == ORA)
  create or replace type ch_tab as TABLE OF VARCHAR2(4000 CHAR);
/
* end if
* if (*{DBMS} == SA)
  exec sp_addtype test_type, "varchar(2000)", "not null";
* end if
* if (*{DBMS} == MSS)
  exec sp_addtype test_type, "varchar(2000)", "not null";
* end if
