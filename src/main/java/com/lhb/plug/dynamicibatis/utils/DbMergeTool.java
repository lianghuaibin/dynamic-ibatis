/*
 * Copyright (c) 2016, lhb All Rights Reserved.
 */
package com.lhb.plug.dynamicibatis.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库分表差异合并工具
 *
 * @author suntao1
 * @since 2016/03/08
 */
public class DbMergeTool {

    /**
     * 数据库分表差异合并工具，在Console中输出消除差异SQL
     * @param url 数据库连接串
     * @param schemaPattern schema
     * @param tableNamePattern 表名
     * @param tableNameNotLikePatternArray 排除的表名
     */
    public void execute(String url, String schemaPattern, String tableNamePattern, String[] tableNameNotLikePatternArray) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
            StringBuilder tableSql = new StringBuilder("SELECT t.TABLE_NAME FROM information_schema.TABLES t ");
            tableSql.append("WHERE t.TABLE_SCHEMA = ? AND t.TABLE_NAME LIKE ? ");
            List tableSqlParams = new ArrayList();
            tableSqlParams.add(schemaPattern);
            tableSqlParams.add(tableNamePattern);
            if (tableNameNotLikePatternArray != null) {
                for (String tableNameNotLikePattern : tableNameNotLikePatternArray) {
                    tableSql.append("AND t.TABLE_NAME NOT LIKE ? ");
                    tableSqlParams.add(tableNameNotLikePattern);
                }
            }
            PreparedStatement tablePs = connection.prepareStatement(tableSql.toString());
            for (int i = 0; i < tableSqlParams.size(); i++) {
                tablePs.setObject(i + 1, tableSqlParams.get(i));
            }
            ResultSet tableResultSet = tablePs.executeQuery();
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                List params = new ArrayList();

                StringBuilder sql = new StringBuilder();
                sql.append("SELECT T.TABLE_NAME,T.COLUMN_NAME,T.IS_NULLABLE,T.COLUMN_TYPE,T.COLUMN_COMMENT,T.COLUMN_DEFAULT FROM information_schema. COLUMNS t ");
                sql.append("WHERE t.TABLE_SCHEMA like ? AND t.TABLE_NAME LIKE ? ");
                params.add(schemaPattern);
                params.add(tableNamePattern);
                if (tableNameNotLikePatternArray != null) {
                    for (String tableNameNotLikePattern : tableNameNotLikePatternArray) {
                        sql.append("AND t.TABLE_NAME NOT LIKE ? ");
                        params.add(tableNameNotLikePattern);
                    }
                }
                sql.append("AND UPPER(t.COLUMN_NAME) NOT IN ");
                sql.append("( SELECT UPPER(COLUMN_NAME) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?) ");
                params.add(schemaPattern);
                params.add(tableName);
                sql.append("GROUP BY T.COLUMN_NAME");

                PreparedStatement ps = connection.prepareStatement(sql.toString());
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String TABLE_NAME = rs.getString("TABLE_NAME");
                    String COLUMN_NAME = rs.getString("COLUMN_NAME");
                    String IS_NULLABLE = rs.getString("IS_NULLABLE");
                    String COLUMN_TYPE = rs.getString("COLUMN_TYPE");
                    String COLUMN_DEFAULT = rs.getString("COLUMN_DEFAULT");
                    String COLUMN_COMMENT = rs.getString("COLUMN_COMMENT");

                    StringBuilder addSql = new StringBuilder();
                    addSql.append("ALTER TABLE ").append(tableName).append(" ").append("ADD COLUMN ").append(COLUMN_NAME.toUpperCase());
                    addSql.append(" ").append(COLUMN_TYPE);
                    /*if("YES".equals(IS_NULLABLE)){
                        addSql.append(" NOT NULL");
                    }
                    if(COLUMN_DEFAULT != null){
                        addSql.append(" DEFAULT ").append(COLUMN_DEFAULT);
                    }*/
                    StringBuilder comment = new StringBuilder("补充字段消除差异（");
                    comment.append("源表：").append(TABLE_NAME).append(",注释：").append(COLUMN_COMMENT).append(")");
                    addSql.append(" COMMENT '").append(comment).append("';");

                    System.out.println(addSql.toString());
                }
                rs.close();
                ps.close();
            }
            tableResultSet.close();
            tablePs.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void main(String[] args) {
        DbMergeTool dbMergeTool = new DbMergeTool();
        String url = "jdbc:mysql://172.24.7.87:3306/finsetts_test?user=mysql&password=123456";
        dbMergeTool.execute(url, "finsetts_test", "fin_fee_detail%", new String[]{
                "fin_fee_detail_100",
                "fin_fee_detail_100_history",
                "fin_fee_detail_102",
                "fin_fee_detail_107",
                "fin_fee_detail_114",
                "fin_fee_detail_130",
                "fin_fee_detail_888",
                "fin_fee_detail_12601",
                "fin_fee_detail_100001",
                "fin_fee_detail_100002",
                "fin_fee_detail_100003",
                "fin_fee_detail_121001",
                "fin_fee_detail_extend_100",
                "fin_fee_detail_extend_114",
                "fin_fee_detail_sub_888",
        });
    }
}
