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

create table <<TABLE_NAME>> (module_name varchar(30), db_change_code varchar(200), sql_text varchar(2000), rows_updated bigint, error_code bigint, error_state varchar(10), error_text varchar(1000), update_time timestamp(6), execution_time numeric(10,2))