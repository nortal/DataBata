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

-- terminate user sessions to propagation database 
 SELECT
     pg_terminate_backend(t.pid)
 FROM
     pg_stat_activity t
 WHERE
     -- don't kill my own connection!
     t.pid <> pg_backend_pid()
     -- don't kill the connections to other databases
     AND t.datname = '#{db.propagation.user}'
    ;

DROP DATABASE IF EXISTS #{db.propagation.user};
CREATE DATABASE #{db.propagation.user} WITH ENCODING='UTF8' OWNER=#{db.propagation.user};

-- owner have all privileges by default
GRANT ALL PRIVILEGES ON DATABASE #{db.propagation.user} to #{db.propagation.user};
GRANT CREATE ON DATABASE #{db.propagation.user} TO #{db.propagation.user};
GRANT TEMP ON DATABASE #{db.propagation.user} TO #{db.propagation.user};
