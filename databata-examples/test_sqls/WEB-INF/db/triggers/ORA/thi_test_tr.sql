CREATE OR REPLACE TRIGGER thi_test_tr BEFORE INSERT OR UPDATE ON test_tr FOR EACH ROW 
BEGIN 
 IF inserting AND :new.ID IS NULL THEN SELECT s_test_tr.nextval INTO :new.ID FROM DUAL; END IF; 
 :new.sys_modify_time := systimestamp; 
END;