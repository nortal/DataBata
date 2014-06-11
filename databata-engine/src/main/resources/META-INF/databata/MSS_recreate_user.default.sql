DECLARE @dbname sysname
SET @dbname = '#{db.propagation.user}'

DECLARE @spid int
SELECT @spid = min(spid) from master.dbo.sysprocesses where dbid = db_id(@dbname)
WHILE @spid IS NOT NULL
BEGIN
EXECUTE ('KILL ' + @spid)
SELECT @spid = min(spid) from master.dbo.sysprocesses where dbid = db_id(@dbname) AND spid > @spid
END;


DROP DATABASE #{db.propagation.user};

CREATE DATABASE #{db.propagation.user};

ALTER DATABASE #{db.propagation.user} SET COMPATIBILITY_LEVEL = 100;

/*IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [#{db.propagation.user}].[dbo].[sp_fulltext_database] @action = 'enable'
end
*/

ALTER DATABASE [#{db.propagation.user}] SET ANSI_NULL_DEFAULT OFF;
ALTER DATABASE [#{db.propagation.user}] SET ANSI_NULLS OFF;
ALTER DATABASE [#{db.propagation.user}] SET ANSI_PADDING OFF;
ALTER DATABASE [#{db.propagation.user}] SET ANSI_WARNINGS OFF;
ALTER DATABASE [#{db.propagation.user}] SET ARITHABORT OFF;
ALTER DATABASE [#{db.propagation.user}] SET AUTO_CLOSE OFF;
ALTER DATABASE [#{db.propagation.user}] SET AUTO_CREATE_STATISTICS ON;
ALTER DATABASE [#{db.propagation.user}] SET AUTO_SHRINK OFF;
ALTER DATABASE [#{db.propagation.user}] SET AUTO_UPDATE_STATISTICS ON;
ALTER DATABASE [#{db.propagation.user}] SET CURSOR_CLOSE_ON_COMMIT OFF; 
ALTER DATABASE [#{db.propagation.user}] SET CURSOR_DEFAULT  GLOBAL;
ALTER DATABASE [#{db.propagation.user}] SET CONCAT_NULL_YIELDS_NULL OFF; 
ALTER DATABASE [#{db.propagation.user}] SET NUMERIC_ROUNDABORT OFF;
ALTER DATABASE [#{db.propagation.user}] SET QUOTED_IDENTIFIER OFF;
ALTER DATABASE [#{db.propagation.user}] SET RECURSIVE_TRIGGERS OFF; 
ALTER DATABASE [#{db.propagation.user}] SET DISABLE_BROKER;
ALTER DATABASE [#{db.propagation.user}] SET AUTO_UPDATE_STATISTICS_ASYNC OFF; 
ALTER DATABASE [#{db.propagation.user}] SET DATE_CORRELATION_OPTIMIZATION OFF; 
ALTER DATABASE [#{db.propagation.user}] SET TRUSTWORTHY OFF;
ALTER DATABASE [#{db.propagation.user}] SET ALLOW_SNAPSHOT_ISOLATION OFF; 
ALTER DATABASE [#{db.propagation.user}] SET PARAMETERIZATION SIMPLE;
ALTER DATABASE [#{db.propagation.user}] SET READ_COMMITTED_SNAPSHOT OFF; 
ALTER DATABASE [#{db.propagation.user}] SET HONOR_BROKER_PRIORITY OFF;
ALTER DATABASE [#{db.propagation.user}] SET READ_WRITE;
ALTER DATABASE [#{db.propagation.user}] SET RECOVERY FULL; 
ALTER DATABASE [#{db.propagation.user}] SET MULTI_USER;
ALTER DATABASE [#{db.propagation.user}] SET PAGE_VERIFY CHECKSUM;  
ALTER DATABASE [#{db.propagation.user}] SET DB_CHAINING OFF;

BEGIN
USE [#{db.propagation.user}];
--CREATE SCHEMA [#{db.propagation.user}]; 
CREATE LOGIN [#{db.propagation.user}] WITH PASSWORD = '#{db.propagation.password}';
CREATE USER [#{db.propagation.user}] FOR LOGIN [#{db.propagation.user}]; --WITH DEFAULT_SCHEMA = #{db.propagation.user};
ALTER USER [#{db.propagation.user}] WITH DEFAULT_SCHEMA=[dbo];
--USE [master];
--GRANT db_ddladmin TO #{db.propagation.user};
--GRANT db_datawriter TO #{db.propagation.user};
EXEC sp_addrolemember 'db_ddladmin' , '#{db.propagation.user}';
EXEC sp_addrolemember 'db_datawriter' , '#{db.propagation.user}';
EXEC sp_addrolemember 'db_datareader' , '#{db.propagation.user}';
EXEC sp_addrolemember 'db_datareader' , '#{db.propagation.user}'
grant execute on schema::dbo to #{db.propagation.user};
END;
/
