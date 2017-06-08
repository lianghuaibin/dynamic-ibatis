/*
 * Copyright (c) 2016, lhb All Rights Reserved.
 */
package com.lhb.plug.dynamicibatis;

/**
 * ApiSql异常类，为RuntimeExcepton
 *
 * @author lianghuaibin
 * @since 2016/03/07
 */
public class ApiException extends RuntimeException{

    private ApiExceptionErrorCodeEnum errorCodeEnum;

    /**
     * ApiSQLException异常
     * @param errorCodeEnum
     * @param message
     */
    public ApiException(ApiExceptionErrorCodeEnum errorCodeEnum, String message){
        super(message);
        this.errorCodeEnum = errorCodeEnum;
    }

    /**
     * 获得异常类型枚举
     * @return
     */
    public ApiExceptionErrorCodeEnum getErrorCodeEnum(){
        return this.errorCodeEnum;
    }

    /**
     * 获得异常类型编码
     * @return
     */
    public int getCode() {
        return this.errorCodeEnum.getCode();
    }

    /**
     * 获得异常类型描述
     * @return
     */
    public String getDesc(){
        return this.errorCodeEnum.getDesc();
    }

    /**
     * 获得异常详情
     * @return
     */
    @Override
    public String getMessage(){
        return super.getMessage();
    }

}
