<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE sqlMap PUBLIC "-//iBATIS.com//DTD SQL Map 2.0//EN" "http://www.ibatis.com/dtd/sql-map-2.dtd">
<!-- ${tableComment} SQL MAP -->
<sqlMap namespace="API.${TABLE_NAME}">
    <typeAlias alias="${TableName}" type="${packagePath}.domain.pojo.${TableName}"/>
    <typeAlias alias="${TableName}Query" type="${packagePath}.domain.query.${TableName}Query"/>
    
</sqlMap>