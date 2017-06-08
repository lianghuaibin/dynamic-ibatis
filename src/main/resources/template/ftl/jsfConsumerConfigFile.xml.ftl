<!-- 京东jsf调用，客户端 -->
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:jsf="http://jsf.jd.com/schema/jsf"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
	   http://jsf.jd.com/schema/jsf http://jsf.jd.com/schema/jsf/jsf.xsd"
       default-lazy-init="true">
    <#list tables as item>
    <!-- ${item.tableComment}接口 -->
    <jsf:consumer id="jsfApi${item.TableName}Service" interface="${item.packagePath}.service.${item.TableName}Service" protocol="jsf"
                  alias="${"$"}{api.jsf.suffix}" timeout="5000" retries="0" check="false">
    </jsf:consumer>

    </#list>

</beans>



