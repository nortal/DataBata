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

--{call META2.kill_user_sessions('#{db.propagation.user}')};
drop user #{db.propagation.user} cascade;

create user #{db.propagation.user} identified by #{db.propagation.password};
grant connect to #{db.propagation.user};
grant resource to #{db.propagation.user};
grant create view to #{db.propagation.user};
grant debug connect session to #{db.propagation.user};
grant create synonym to #{db.propagation.user};
grant create materialized view to #{db.propagation.user};
grant create job to #{db.propagation.user};
