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
