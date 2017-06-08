接口名称,负责人erp账号(多个以分号分隔),接口描述
<#list tables as item>
${item.packagePath}.service.${item.TableName}Service,lianghuaibin;,常规${item.tableComment}接口
</#list>
