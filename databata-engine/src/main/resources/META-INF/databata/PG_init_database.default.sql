--Required CASTs for database (These make "select '1'+2;" working). 
CREATE CAST (varchar AS bigint) WITH INOUT AS IMPLICIT;
CREATE CAST (varchar AS integer) WITH INOUT AS IMPLICIT;

-- reconnect to new created database as postgres user and run next scripts
CREATE EXTENSION IF NOT EXISTS hstore SCHEMA public;
CREATE EXTENSION IF NOT EXISTS dblink SCHEMA public; 
CREATE EXTENSION IF NOT EXISTS tablefunc SCHEMA public;