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

CREATE TABLE <<TABLE_NAME>> (MODULE_NAME VARCHAR2(30 CHAR), DB_CHANGE_CODE VARCHAR2(200 CHAR), SQL_TEXT VARCHAR2(2000 CHAR), ROWS_UPDATED NUMBER(18), ERROR_CODE NUMBER(18), ERROR_TEXT VARCHAR2(1000 CHAR), UPDATE_TIME TIMESTAMP(6), EXECUTION_TIME NUMBER(10,2))