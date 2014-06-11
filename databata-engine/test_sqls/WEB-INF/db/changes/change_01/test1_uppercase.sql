create table test1_uppercase
 (
   ID                 NUMBER(18) not null,
   CODE               VARCHAR2(64 CHAR) not null,
   TYPE_DOVA_CODE     VARCHAR2(30 CHAR) not null,
   IP                 VARCHAR2(64),
   PORT               NUMBER(5),
   PROTOCOL_DOVA_CODE VARCHAR2(30 CHAR),
   START_DATE         DATE not null,
   END_DATE           DATE,
   SYS_VERSION        NUMBER(18) not null,
   SYS_MODIFY_TIME    TIMESTAMP(6) not null,
   SYS_MODIFY_SID     NUMBER(18),
   SYS_MODIFY_UID     NUMBER(18),
   SYS_DELETE_STATUS  VARCHAR2(1) default 'N' not null
 );

--alter table test1_uppercase add primary key (id) using index;
