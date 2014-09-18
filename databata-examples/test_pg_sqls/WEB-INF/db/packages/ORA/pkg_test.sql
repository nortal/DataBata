create or replace package body pkg_test as
FUNCTION test_func RETURN number IS
n number;

BEGIN
  SELECT 1 into n from dual;
  return n;
END;
END;