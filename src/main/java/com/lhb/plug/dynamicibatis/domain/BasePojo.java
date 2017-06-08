/*
 * Copyright (c) 2016, lhb All Rights Reserved.
 */
package com.lhb.plug.dynamicibatis.domain;

import java.io.Serializable;

/**
 * 基础实体类，用于映射数据库表结构
 *
 * @author lianghuaibin
 * @since 2016/03/07
 */
public class BasePojo implements Serializable {

    private static final long serialVersionUID = 1L;

    public final static String COLUMN_ID = "ID";

    protected Long id;    //自增主键

    /**
     * 自增主键
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 自增主键
     * @return id
     */
    public Long getId() {
        return this.id;
    }


}
