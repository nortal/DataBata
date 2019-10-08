--
--   Copyright 2014 Nortal AS
--
--   Licensed under the Apache License, Version 2.0 (the "License");
--   you may not use this file except in compliance with the License.
--   You may obtain a copy of the License at
--
--       http://www.apache.org/licenses/LICENSE-2.0
--
--   Unless required by applicable law or agreed to in writing, software
--   distributed under the License is distributed on an "AS IS" BASIS,
--   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--   See the License for the specific language governing permissions and
--   limitations under the License.
--

create sequence s_test_junit_tr start with 1;

create table test_junit_tr ( id integer not null, str1 varchar(20), sys_modify_time timestamp(6) not null);

CREATE OR REPLACE TRIGGER thi_test_junit_tr BEFORE INSERT OR UPDATE ON test_junit_tr FOR EACH ROW 
BEGIN 
 IF inserting AND :new.ID IS NULL THEN SELECT s_test_junit_tr.nextval INTO :new.ID FROM DUAL; END IF; 
 :new.sys_modify_time := systimestamp; 
END;
