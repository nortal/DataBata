SELECT 
    pg_terminate_backend(procpid) 
FROM 
    pg_stat_activity 
WHERE 
    -- don't kill my own connection!
    procpid <> pg_backend_pid()
    -- don't kill the connections to other databases
    AND datname = '#{db.propagation.user}'
    ;
DROP SCHEMA sch_#{db.propagation.user};
DROP DATABASE #{db.propagation.user};
DROP USER #{db.propagation.user};

CREATE USER #{db.propagation.user} WITH PASSWORD '#{db.propagation.password}';
CREATE DATABASE #{db.propagation.user};
GRANT ALL PRIVILEGES ON DATABASE #{db.propagation.user} to #{db.propagation.user};
CREATE SCHEMA sch_#{db.propagation.user} AUTHORIZATION #{db.propagation.user};

--Required CASTs for database. These should be removed from here. Only schema creation should be here. Database should be set-up previously
CREATE CAST (varchar AS bigint) WITH INOUT AS IMPLICIT;
CREATE CAST (varchar AS integer) WITH INOUT AS IMPLICIT;
