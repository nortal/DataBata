* if (*DBMS == ORA)
create sequence s_test_tr start with 1;

create table test_tr ( id integer not null, str1 varchar(20), sys_modify_time timestamp(6) not null);

CREATE OR REPLACE TRIGGER thi_test_tr BEFORE INSERT OR UPDATE ON test_tr FOR EACH ROW 
BEGIN 
 IF inserting AND :new.ID IS NULL THEN SELECT s_test_tr.nextval INTO :new.ID FROM DUAL; END IF; 
 :new.sys_modify_time := systimestamp; 
END;
/
* end if

* if (*DBMS == SA)
create table test_tr ( id INTEGER NOT NULL DEFAULT AUTOINCREMENT, str1 varchar(20), sys_modify_time timestamp not null);

CREATE OR REPLACE TRIGGER thi_test_tr BEFORE INSERT, UPDATE ORDER 1 ON test_tr
REFERENCING NEW AS nrow
FOR EACH ROW 
BEGIN
 if nrow.sys_modify_time is null then
    set nrow.sys_modify_time = getdate();
 end if;
END;
go
* end if

* if (*DBMS == MSS)
create table test_tr ( id INTEGER IDENTITY(1,1) NOT NULL, str1 varchar(20), sys_modify_time1 datetime null, sys_modify_time2 timestamp not null);


CREATE TRIGGER thi_test_tr ON test_tr AFTER INSERT,UPDATE
AS 
BEGIN
   update test_tr set sys_modify_time1 = getdate() 
     from inserted i
    where test_tr.id=i.id and i.sys_modify_time1 is null;
END;
/

* end if

* if (*DBMS == PG)
CREATE SEQUENCE test_tr_seq START 1;
 
CREATE TABLE test_tr (id numeric(10) NOT NULL, str1 varchar(20), sys_modify_time timestamp NOT NULL);
 
CREATE OR REPLACE FUNCTION test_tr_trigf() RETURNS TRIGGER AS $test_tr_trigf$
   BEGIN
       IF TG_OP = 'INSERT'
       THEN
           IF NEW.id IS NULL THEN
               NEW.id = nextval('test_tr_seq');
           END IF;
       END IF;
       NEW.sys_modify_time = now();
       RETURN NEW;
   END;
$test_tr_trigf$ LANGUAGE plpgsql;
 
CREATE TRIGGER thi_test_tr BEFORE INSERT OR UPDATE ON test_tr FOR EACH ROW EXECUTE PROCEDURE test_tr_trigf();
/ 
* end if


-- PG_BLOCK_START
begin
insert into test_tr(str1) values ('a');
insert into test_tr(str1) values ('b');
end;
-- PG_BLOCK_END
/

update test_tr set str1='aa' where id=1;

--select * from test_tr;
