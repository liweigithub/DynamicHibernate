<?xml version="1.0" encoding="utf-8"?>
<!--
<!DOCTYPE dynamic-statement SYSTEM "http://localhost:8080/dynamic-statement-1.0.dtd">
 -->
<dynamic-statement>
	<sql-query name="emp.findByName">
		<![CDATA[
			select
				id as id,
				name as name
			from
				oa.employee
			where 1=1
			<#if name != ''>
				and name like '${name}%'
			</#if>
			group by
				id
		 ]]>
	</sql-query>

	 <sql-query name="emp.deleteEmp">
		 <![CDATA[
			delete from oa.employee where id=${id}
		 ]]>
	</sql-query>

    <hql-query name="emp.findById">
        <![CDATA[
        	 from com.hyaroma.domain.Employee where 1=1
			<#if id != '' && id != 0>
				and id=${id}
			</#if>
        ]]>
    </hql-query>
</dynamic-statement>

	