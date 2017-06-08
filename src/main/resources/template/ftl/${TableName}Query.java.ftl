/*
* Copyright (c) ${year}, lhb All Rights Reserved.
*/
package ${packagePath}.domain.query;

import com.lhb.plug.dynamicibatis.domain.BaseQuery;

import java.math.BigDecimal;
import java.util.Date;

/**
 * ${tableComment}查询实体类
 *
 * @author ${author}
 * @since ${since}
 */
public class ${TableName}Query extends BaseQuery<${TableName}Query> {

    private static final long serialVersionUID = 1L;

<#list columns as item>
    private ${item.dataType} ${item.columnName};    //${item.columnComment}
    private ${item.dataType}[] ${item.columnName}_IN;   //${item.columnComment} IN查询
    private ${item.dataType} ${item.columnName}_NE;   //${item.columnComment} 不等于
    <#if item.dataType != "String">
    private ${item.dataType} ${item.columnName}_LT;   //${item.columnComment} 小于
    private ${item.dataType} ${item.columnName}_LE;   //${item.columnComment} 小于等于
    private ${item.dataType} ${item.columnName}_GT;   //${item.columnComment} 大于
    private ${item.dataType} ${item.columnName}_GE;   //${item.columnComment} 大于等于
    </#if>
    <#if item.dataType == "String">
    private ${item.dataType} ${item.columnName}_LK;   //${item.columnComment} LIKE查询
    </#if>
</#list>

<#list columns as item>
    /**
     * ${item.columnComment}
     * @return
     */
    public ${item.dataType} get${item.ColumnName}() {
        return ${item.columnName};
    }

    /**
     * ${item.columnComment}
     * @return
     */
    public void set${item.ColumnName}(${item.dataType} ${item.columnName}) {
        this.${item.columnName} = ${item.columnName};
    }

    /**
     * ${item.columnComment} IN查询
     * @return
     */
    public ${item.dataType}[] get${item.ColumnName}_IN() {
        return ${item.columnName}_IN;
    }

    /**
     * ${item.columnComment} IN查询
     * @return
     */
    public void set${item.ColumnName}_IN(${item.dataType}[] ${item.columnName}_IN) {
        this.${item.columnName}_IN = ${item.columnName}_IN;
    }

    /**
     * ${item.columnComment} 不等于
     * @return
     */
    public ${item.dataType} get${item.ColumnName}_NE() {
        return ${item.columnName}_NE;
    }

    /**
     * ${item.columnComment} 不等于
     * @return
     */
    public void set${item.ColumnName}_NE(${item.dataType} ${item.columnName}_NE) {
        this.${item.columnName}_NE = ${item.columnName}_NE;
    }

    <#if item.dataType != "String">
    /**
     * ${item.columnComment} 小于
     * @return
     */
    public ${item.dataType} get${item.ColumnName}_LT() {
        return ${item.columnName}_LT;
    }

    /**
     * ${item.columnComment} 小于
     * @return
     */
    public void set${item.ColumnName}_LT(${item.dataType} ${item.columnName}_LT) {
        this.${item.columnName}_LT = ${item.columnName}_LT;
    }

    /**
     * ${item.columnComment} 小于等于
     * @return
     */
    public ${item.dataType} get${item.ColumnName}_LE() {
        return ${item.columnName}_LE;
    }

    /**
     * ${item.columnComment} 小于等于
     * @return
     */
    public void set${item.ColumnName}_LE(${item.dataType} ${item.columnName}_LE) {
        this.${item.columnName}_LE = ${item.columnName}_LE;
    }

    /**
     * ${item.columnComment} 大于
     * @return
     */
    public ${item.dataType} get${item.ColumnName}_GT() {
        return ${item.columnName}_GT;
    }

    /**
     * ${item.columnComment} 大于
     * @return
     */
    public void set${item.ColumnName}_GT(${item.dataType} ${item.columnName}_GT) {
        this.${item.columnName}_GT = ${item.columnName}_GT;
    }

    /**
     * ${item.columnComment} 大于等于
     * @return
     */
    public ${item.dataType} get${item.ColumnName}_GE() {
        return ${item.columnName}_GE;
    }

    /**
     * ${item.columnComment} 大于等于
     * @return
     */
    public void set${item.ColumnName}_GE(${item.dataType} ${item.columnName}_GE) {
        this.${item.columnName}_GE = ${item.columnName}_GE;
    }
    </#if>

    <#if item.dataType == "String">
	/**
     * ${item.columnComment} LIKE查询
     * @return
     */
    public ${item.dataType} get${item.ColumnName}_LK() {
        return ${item.columnName}_LK;
    }

    /**
     * ${item.columnComment} LIKE查询
     * @return
     */
    public void set${item.ColumnName}_LK(${item.dataType} ${item.columnName}_LK) {
        this.${item.columnName}_LK = ${item.columnName}_LK;
    }
    </#if>

</#list>

}