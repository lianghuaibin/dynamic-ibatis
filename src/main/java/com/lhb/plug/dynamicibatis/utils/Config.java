/*
 * Copyright (c) 2016, lhb All Rights Reserved.
 */
package com.lhb.plug.dynamicibatis.utils;

import com.ibatis.sqlmap.client.SqlMapClient;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Dynamic iBatis 配置类
 *
 * @author suntao1
 * @since 2016/03/16
 */
public class Config {

    private String umpAppName;

    private PlatformTransactionManager transactionManager;

    private SqlMapClient writeSqlMapClient;

    private SqlMapClient readSqlMapClient;

    public static Map<String,String> SQL_LOG = new HashMap<String,String>();

    public String getUmpAppName() {
        return umpAppName;
    }

    public void setUmpAppName(String umpAppName) {
        this.umpAppName = umpAppName;
    }

    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public SqlMapClient getWriteSqlMapClient() {
        return writeSqlMapClient;
    }

    public void setWriteSqlMapClient(SqlMapClient writeSqlMapClient) {
        this.writeSqlMapClient = writeSqlMapClient;
    }

    public SqlMapClient getReadSqlMapClient() {
        return readSqlMapClient;
    }

    public void setReadSqlMapClient(SqlMapClient readSqlMapClient) {
        this.readSqlMapClient = readSqlMapClient;

    }
}
