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
DROP database IF EXISTS #{db.propagation.user};
DROP USER IF EXISTS #{db.propagation.user};

CREATE USER #{db.propagation.user} WITH PASSWORD '#{db.propagation.password}';
ALTER USER #{db.propagation.user} VALID UNTIL 'infinity';