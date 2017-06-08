/*
 * Copyright (c) 2016, lhb All Rights Reserved.
 */
package com.lhb.plug.dynamicibatis;

/**
 * ApiException，错误类型枚举
 *
 * @author lianghuaibin
 * @since 2016/03/07
 */
public enum ApiExceptionErrorCodeEnum {

    ERROR_UNKNOWN(0, "未知错误"),
    ERROR_SQL_EXCEPTION(1, "java.sql.SQLException"),
    ERROR_SHARDING_MISSING(2, "缺失分表规则"),
    ERROR_SHARDING_ERROR(3, "分表规则错误"),
    ERROR_UPDATE_NO_ID(4,"更新操作未限定ID范围"),
    ERROR_ENTITY_IS_NULL(5,"传入的实体为NULL"),
    ERROR_ENTITY_IS_EMPTY(6,"传入的实体为空"),
    ERROR_QUERY_IS_NULL(7,"传入的查询实体为NULL"),
    ERROR(999,"错误");

    private int code;
    private String desc;

    ApiExceptionErrorCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }
}
