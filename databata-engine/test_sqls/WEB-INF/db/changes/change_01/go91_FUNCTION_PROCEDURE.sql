* if (*{DBMS} == ORA)
\i ORACLE_FUNCTION_PROCEDURE.sql
* end if

* if (*{DBMS} == SA)
\i SA_FUNCTION_PROCEDURE.sql
* end if

* if (*{DBMS} == MSS)
\i MSS_FUNCTION_PROCEDURE.sql
* end if
