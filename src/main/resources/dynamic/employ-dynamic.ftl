<?xml version="1.0" encoding="utf-8"?>
<!--
<!DOCTYPE dynamic-statement SYSTEM "http://localhost:8080/dynamic-statement-1.0.dtd">
 -->
<dynamic-statement>
	<sql-query name="emp.findByName">
		<![CDATA[
			select id,name from employee where 1=1 group by  id order by id
		 ]]>
	</sql-query>

	 <sql-query name="emp.deleteEmp">
		 <![CDATA[
			delete from employee where 1=1
		 ]]>
	</sql-query>

    <hql-query name="emp.findById">
        <![CDATA[
        	select id,name from com.hyaroma.domain.Employee where 1=1
			<#if id !="" && id!="0">
				and id=${id}
			</#if>
        ]]>
    </hql-query>
    <hql-query name="emp.findEmp1">
        <![CDATA[
        	select * from oa.employee
        ]]>
    </hql-query>
</dynamic-statement>

	