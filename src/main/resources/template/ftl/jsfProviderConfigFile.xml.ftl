<!-- 京东jsf调用，服务端 -->
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:jsf="http://jsf.jd.com/schema/jsf"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
	    http://jsf.jd.com/schema/jsf http://jsf.jd.com/schema/jsf/jsf.xsd"
       default-lazy-init="true">
    <!-- 注册中心 address="192.168.209.74:40660" -->
    <jsf:registry id="jsfRegistry" protocol="jsfRegistry" index="i.jsf.jd.com"/>

    <jsf:server id="jsf" protocol="jsf"/>
    <#list tables as item>
    <!-- ${item.tableComment}接口 -->
    <jsf:provider id="jsfApi${item.TableName}Service" interface="${item.packagePath}.service.${item.TableName}Service" alias="${"$"}{api.jsf.suffix}"
                  ref="api${item.TableName}Service" server="jsf" >
    </jsf:provider>

    </#list>
</beans>
