/*
* Copyright (c) ${year}, lhb All Rights Reserved.
*/
package ${packagePath}.domain.pojo;

import com.lhb.plug.dynamicibatis.domain.BasePojo;

import java.math.BigDecimal;
import java.util.Date;

/**
* ${tableComment}实体类
*
* @author ${author}
* @since ${since}
*/
public class ${TableName} extends BasePojo {

    <#list columns as item>
    /**
     * ${item.columnComment}
     */
    public final static String COLUMN_${item.COLUMN_NAME} = "${item.COLUMN_NAME}";
    </#list>

    <#list columns as item>
    private ${item.dataType} ${item.columnName};    //${item.columnComment}
    </#list>

    <#list columns as item>
    /**
     * ${item.columnComment}
     * @param ${item.columnName}
     */
    public void set${item.ColumnName}(${item.dataType} ${item.columnName}) {
        this.${item.columnName} = ${item.columnName};
    }

    /**
     * ${item.columnComment}
     * @return ${item.columnName}
     */
    public ${item.dataType} get${item.ColumnName}() {
        return this.${item.columnName};
    }

    </#list>

}