/*
 * Copyright (c) ${year}, lhb All Rights Reserved.
 */
package ${packagePath}.dao;

import ${packagePath}.domain.pojo.${TableName};
import ${packagePath}.domain.query.${TableName}Query;
import com.lhb.plug.dynamicibatis.impl.ApiBaseDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * ${tableComment}Dao层
 *
 * @author ${author}
 * @since ${since}
 */
@Repository("api${TableName}Dao")
public class ${TableName}Dao extends ApiBaseDaoImpl<${TableName},${TableName}Query> {

    /**
     * 构造函数
     */
    public ${TableName}Dao() {
        super(${TableName}.class);
    }
}
