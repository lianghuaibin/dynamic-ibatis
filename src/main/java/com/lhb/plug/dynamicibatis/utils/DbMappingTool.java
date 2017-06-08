/*
 * Copyright (c) 2016, lhb All Rights Reserved.
 */
package com.lhb.plug.dynamicibatis.utils;


import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.net.URLDecoder;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * ApiBaseCodeTool代码工具
 *
 * @author suntao1
 * @since 2016/03/08
 */
public class DbMappingTool {

    private static final Map<String, String> converMap = new HashMap<String, String>();

    static {
        converMap.put("INT", "Integer");
        converMap.put("BIGINT", "Long");
        converMap.put("BIGINT UNSIGNED", "Long");
        converMap.put("TINYINT UNSIGNED","Integer");
        converMap.put("INT UNSIGNED", "Integer");
        converMap.put("VARCHAR", "String");
        converMap.put("VARBINARY", "String");
        converMap.put("CHAR", "String");
        converMap.put("TEXT", "String");
        converMap.put("DATE", "Date");
        converMap.put("DATETIME", "Date");
        converMap.put("TIMESTAMP", "Date");
        converMap.put("BIT", "Integer");
        converMap.put("SMALLINT", "Integer");
        converMap.put("DECIMAL", "BigDecimal");
        converMap.put("DOUBLE", "Double");
        converMap.put("TINYINT", "Integer");

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String replaceComment(String source) {
        String rtnVal = "";
        rtnVal = source.replaceAll("[\n\r]", " ");
        rtnVal = rtnVal.replaceAll("--", "-");
        return rtnVal;
    }

    /**
     * 执行生成操作
     * @param url 数据库连接URL
     * @param templatePath 模板文件地址
     * @param outputPathMap 生成输出文件配置Map，key：模板名称 value：输出路径
     * @param configFileMap 生成配置文件配置Map，key：配置文件模板名称 value：输出路径
     * @param tableNamePattern 要生成表名称，可以使用%进行模糊匹配
     * @param extParamMap 模板附加参数
     * @param replaceExistFile 是否替换已存在文件
     */
    public void execute(String url, String templatePath, Map<String, String> outputPathMap, Map<String, String> configFileMap, String tableNamePattern, Map<String, Object> extParamMap, boolean replaceExistFile) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tablesResultSet = metaData.getTables(null, "%", tableNamePattern, new String[]{"TABLE"});
            List<Map<String, Object>> tableList = new ArrayList<Map<String, Object>>();
            while (tablesResultSet.next()) {
                Map<String, Object> tableModel = new HashMap<String, Object>();
                String tableName = tablesResultSet.getString("TABLE_NAME");
                tableModel.put("table_name", tableName.toLowerCase());
                tableModel.put("TABLE_NAME", tableName.toUpperCase());
                tableModel.put("tableName", CamelCaseUtils.toCamelCase(tableName));
                tableModel.put("TableName", CamelCaseUtils.toCamelCaseCapitalize(tableName));

                //获取表Comment
                PreparedStatement ps = connection.prepareStatement("select t.TABLE_COMMENT from information_schema.`TABLES` t where t.TABLE_NAME = ?");
                ps.setString(1, tableName);
                ResultSet tableCommentInfoRs = ps.executeQuery();
                while (tableCommentInfoRs.next()) {
                    String tableComment = tableCommentInfoRs.getString(1);
                    if (tableComment != null) {

                    }
                    tableModel.put("tableComment", replaceComment(tableCommentInfoRs.getString(1)));
                }
                ps.close();
                tableModel.putAll(extParamMap);
                tableList.add(tableModel);

                List<Map<String, String>> columns = new ArrayList<Map<String, String>>();
                ResultSet columnsResultSet = metaData.getColumns(null, "%", tableName, "%");
                while (columnsResultSet.next()) {
                    Map<String, String> column = new HashMap<String, String>();
                    String columnName = columnsResultSet.getString("COLUMN_NAME");
                    if (!"ID".equals(columnName.toUpperCase())) { //不生成ID相关，在Base类中已定义
                        column.put("column_name", columnName.toLowerCase());
                        column.put("COLUMN_NAME", columnName.toUpperCase());
                        column.put("columnName", CamelCaseUtils.toCamelCase(columnName));
                        column.put("ColumnName", CamelCaseUtils.toCamelCaseCapitalize(columnName));
                        String dataType = converMap.get(columnsResultSet.getString("TYPE_NAME"));
                        if (dataType == null) {
                            throw new Exception("未支持的数据类型：" + columnsResultSet.getString("TYPE_NAME"));
                        }
                        column.put("dataType", dataType);
                        column.put("columnComment", replaceComment(columnsResultSet.getString("REMARKS")));
                        columns.add(column);
                    }
                }
                tableModel.put("columns", columns);

                File templatePathFile = new File(URLDecoder.decode(templatePath));
                if (templatePathFile.exists() && templatePathFile.isDirectory()) {
                    Configuration configuration = new Configuration();
                    configuration.setDirectoryForTemplateLoading(templatePathFile);
                    File[] files = templatePathFile.listFiles();
                    for (File file : files) {
                        String ftlFileName = file.getName();
                        if (outputPathMap.containsKey(ftlFileName)) {
                            //解析保存的文件名
                            Writer writer = new CharArrayWriter();
                            Template fileNameTemplate = new Template(null, new StringReader(ftlFileName), null);
                            fileNameTemplate.process(tableModel, writer);
                            String savedFileName = writer.toString().replace(".ftl", "");
                            //解析文件内容
                            Template fileContentTemplate = configuration.getTemplate(ftlFileName);
                            OutputStreamWriter outputStreamWriter = null;
                            try {
                                File outFile = new File(URLDecoder.decode(outputPathMap.get(ftlFileName)) + "/" + savedFileName);
                                File outputPathFile = new File(outputPathMap.get(ftlFileName));
                                if (!outputPathFile.exists()) {
                                    outputPathFile.mkdirs();
                                }
                                if (outFile.exists()) {
                                    if (replaceExistFile) {
                                        outFile.delete();
                                        outFile.createNewFile();
                                        System.out.println("已存在（覆盖）：" + outFile.getAbsolutePath());
                                    } else {
                                        System.out.println("已存在（跳过）：" + outFile.getAbsolutePath());
                                        continue;
                                    }
                                }
                                outputStreamWriter = new OutputStreamWriter(new FileOutputStream(outFile));
                                fileContentTemplate.process(tableModel, outputStreamWriter);
                                System.out.println("文件：" + outFile.getAbsolutePath() + "已生成！");
                            } catch (Exception ex) {
                                System.out.println("error-----表：" + tableName + "生成失败！");
                                ex.printStackTrace();
                            } finally {
                                if (null != outputStreamWriter) {
                                    outputStreamWriter.flush();
                                    outputStreamWriter.close();
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("ftl模板目录不存在");
                }
            }

            //写入配置文件
            Map<String, Object> configModel = new HashMap<String, Object>();
            configModel.putAll(extParamMap);
            for (String configFileName : configFileMap.keySet()) {
                Writer configFileWriter = new FileWriter(configFileMap.get(configFileName) + "\\" + configFileName.replace(".ftl", ""));
                Template configFileWriterTemplate = new Template(null, new FileReader(templatePath + "\\" + configFileName), null);
                configModel.put("tables", tableList);
                configFileWriterTemplate.process(configModel, configFileWriter);
                configFileWriter.close();
                System.out.println("配置文件已生成：" + configFileMap.get(configFileName) + "\\" + configFileName.replace(".ftl", ""));
            }
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
        DbMappingTool dbMappingTool = new DbMappingTool();
        String url = "jdbc:mysql://localhost:3306/lhb?user=foo&password=123";
        String templatePath = DbMappingTool.class.getClassLoader().getResource("template/ftl").getPath();
        String tableName = "lhb_user";
        Map<String, Object> extParams = new HashMap<String, Object>();
        extParams.put("packagePath", "com.lhb.api");
        extParams.put("author", "lianghuaibin");
        //extParams.put("tableComment","用户表");
        extParams.put("since", (new SimpleDateFormat("yyyy/MM/dd")).format(new Date()));
        extParams.put("year", (new Date()).getYear());
        String basePath = "E:\\output";
        Map<String, String> outputPathMap = new HashMap<String, String>();
        /**
         * 以下文件的生成路径设置为项目中文件存放路径，则自动生成的文件就不需要再copy到项目中了
         */
//        outputPathMap.put("${TableName}.java.ftl", basePath + "\\lhb_project\\src\\main\\java\\com\\jd\\finsetts\\api\\domain\\pojo");
        outputPathMap.put("${TableName}.java.ftl", basePath + "\\api\\domain\\pojo");
        outputPathMap.put("${TableName}Query.java.ftl", basePath + "\\api\\domain\\query");
        outputPathMap.put("${TableName}Dao.java.ftl", basePath + "\\api\\dao");
        outputPathMap.put("${TableName}DaoTest.java.ftl", basePath + "\\api\\dao");
        outputPathMap.put("${TableName}Service.java.ftl", basePath + "\\api\\service");
        outputPathMap.put("${TableName}ServiceImpl.java.ftl", basePath + "\\api\\service\\impl");
        outputPathMap.put("${TableName}ServiceTest.java.ftl", basePath + "\\api\\service");
        outputPathMap.put("${TABLE_NAME}.xml.ftl", basePath + "\\api");
//        outputPathMap.put("${TABLE_NAME}.xml.ftl", basePath + "\\lhb_project\\src\\main\\resources\\sqlMap\\api");

        String configFilePath = "E:\\output";
        Map<String, String> configFileMap = new HashMap<String, String>();
        configFileMap.put("jsfConsumerConfigFile.xml.ftl", configFilePath);
        configFileMap.put("jsfProviderConfigFile.xml.ftl", configFilePath);
        configFileMap.put("jsfRegistConfig.csv.ftl", configFilePath);
        dbMappingTool.execute(url, templatePath, outputPathMap, configFileMap, tableName, extParams, true);
    }
}
