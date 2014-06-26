DataBata
========

DataBata is a tool for incremental database update based on HSQLDB SqlTool created for all types of workers: DBA, developers and QA. Currently Oracle and Postgres databases are supported. We plan to support all general RDBMSes.

For monitoring purposes there is DataBata Web Console.

[![logs](web_console_screen1_th.png)](web_console_screen1.png)
[![history](web_console_screen2_th.png)](web_console_screen2.png)
[![objects](web_console_screen3_th.png)](web_console_screen3.png)

What do you need to run DataBata?
========
- Java 6+
- Spring Framework
- Database driver
- HsqlDB sqltool (see databata-engine/lib folder)

Installation
========
Spring Framework: simply create following bean in your spring configuration
``` xml
<bean id="propagator" class="eu.databata.engine.spring.PropagatorSpringInstance"
		init-method="init">
		<property name="jdbcTemplate" ref="jdbcTemplate" />
		<property name="transactionManager" ref="transactionManager" />

		<property name="changes" value="WEB-INF/db/changes" />
		<property name="packageDir" value="WEB-INF/db/packages" />
		<property name="viewDir" value="WEB-INF/db/views" />
		<property name="triggerDir" value="WEB-INF/db/triggers" />
		<property name="useTestData" value="false" />
		<property name="disableDbPropagation" value="false" />
		<property name="enableAutomaticTransformation" value="true" />
		<property name="moduleName" value="DATABATA_TEST" />
</bean>
```
Note, you need reference to jdbcTemplate and transactionManager beans in your configuration.
Location of files in web application as you can see is inside WEB-INF directory. 

