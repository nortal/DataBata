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

--Required CASTs for database (These make "select '1'+2;" working). 
CREATE CAST (varchar AS bigint) WITH INOUT AS IMPLICIT;
CREATE CAST (varchar AS integer) WITH INOUT AS IMPLICIT;

-- reconnect to new created database as postgres user and run next scripts
CREATE EXTENSION IF NOT EXISTS hstore SCHEMA public;
CREATE EXTENSION IF NOT EXISTS dblink SCHEMA public; 
CREATE EXTENSION IF NOT EXISTS tablefunc SCHEMA public;