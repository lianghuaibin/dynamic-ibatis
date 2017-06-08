/*
 * Copyright (c) ${year}, lhb All Rights Reserved.
 */
package ${packagePath}.service;

import com.jd.fastjson.JSONObject;
import ${packagePath}.domain.pojo.${TableName};
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * ${tableComment}Service测试类
 *
 * @author ${author}
 * @since ${since}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/spring-config.xml", "/spring/springmvc.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class ${TableName}ServiceTest {

    public final static Logger logger = LoggerFactory.getLogger(${TableName}ServiceTest.class);

    @Autowired
    private ${TableName}Service ${tableName}Service;

    @Test
    public void testCRUD() {
        try {
            ${TableName} ${tableName} = new ${TableName}();
            //${tableName}.setCreatedTime(new Date());
            //${tableName}.setModifiedTime(new Date());
            Long newId = ${tableName}Service.insert(${tableName});
            logger.info("新增实体完成(newId:" + newId + ")：" + JSONObject.toJSONString(${tableName}));

            ${tableName} = ${tableName}Service.findById(newId);
            logger.info("查询实体完成：" + JSONObject.toJSONString(${tableName}));

            //${tableName}.setModifiedTime(new Date());
            int rows = ${tableName}Service.update(${tableName});
            logger.info("更新实体完成(共影响" + rows + "行)：" + JSONObject.toJSONString(${tableName}));

            rows = ${tableName}Service.delete(newId);
            logger.info("删除实体完成(共影响" + rows + "行)：" + JSONObject.toJSONString(newId));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
