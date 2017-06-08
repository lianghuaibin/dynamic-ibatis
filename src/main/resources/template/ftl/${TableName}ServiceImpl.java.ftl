/*
 * Copyright (c) ${year}, lhb All Rights Reserved.
 */
package ${packagePath}.service.impl;

import com.lhb.plug.dynamicibatis.ApiBaseDao;
import com.lhb.plug.dynamicibatis.impl.ApiBaseServiceImpl ;
import ${packagePath}.dao.${TableName}Dao;
import ${packagePath}.domain.pojo.${TableName};
import ${packagePath}.domain.query.${TableName}Query;
import ${packagePath}.service.${TableName}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ${tableComment}Service实现
 *
 * @author ${author}
 * @since ${since}
 */
@Service("api${TableName}Service")
public class ${TableName}ServiceImpl extends ApiBaseServiceImpl<${TableName}, ${TableName}Query> implements ${TableName}Service {

    @Autowired
    private ${TableName}Dao ${tableName}Dao;

    @Override
    public ApiBaseDao<${TableName}, ${TableName}Query> getApiBaseDao() {
        return ${tableName}Dao;
    }
}
