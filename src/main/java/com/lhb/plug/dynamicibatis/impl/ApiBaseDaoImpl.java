/*
 * Copyright (c) 2016, lhb All Rights Reserved.
 */
package com.lhb.plug.dynamicibatis.impl;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapException;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;
import com.ibatis.sqlmap.engine.mapping.parameter.InlineParameterMapParser;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMap;
import com.ibatis.sqlmap.engine.mapping.result.AutoResultMap;
import com.ibatis.sqlmap.engine.mapping.result.ResultMap;
import com.ibatis.sqlmap.engine.mapping.sql.Sql;
import com.ibatis.sqlmap.engine.mapping.sql.SqlText;
import com.ibatis.sqlmap.engine.mapping.sql.simple.SimpleDynamicSql;
import com.ibatis.sqlmap.engine.mapping.sql.stat.StaticSql;
import com.ibatis.sqlmap.engine.mapping.statement.*;
import com.lhb.plug.dynamicibatis.ApiBaseDao;
import com.lhb.plug.dynamicibatis.ApiException;
import com.lhb.plug.dynamicibatis.ApiExceptionErrorCodeEnum;
import com.lhb.plug.dynamicibatis.domain.BasePojo;
import com.lhb.plug.dynamicibatis.domain.BaseQuery;
import com.lhb.plug.dynamicibatis.utils.CamelCaseUtils;
import com.lhb.plug.dynamicibatis.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.ReflectUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.*;

/**
 * Dao层基类
 * 提供了基本的数据库增删改查操作方法
 *
 * @param <T> 数据库映射对象实体类
 * @param <Q> 数据库查询对象类
 * @author lianghuaibin
 * @since 2016/03/07
 * @see ApiBaseDao
 * @see BasePojo
 * @see BaseQuery
 * @since 1.00
 */
public class ApiBaseDaoImpl<T extends BasePojo, Q extends BaseQuery> implements ApiBaseDao<T, Q> {

    protected static final Logger logger = LoggerFactory.getLogger(ApiBaseDaoImpl.class);

    private final static String SQL_MAP_TYPE_SELECT_STATEMENT = "SELECT_STATEMENT";  //查询语句
    private final static String SQL_MAP_TYPE_INSERT_STATEMENT = "INSERT_STATEMENT";  //插入语句
    private final static String SQL_MAP_TYPE_UPDATE_STATEMENT = "UPDATE_STATEMENT";  //插入语句
    private final static String SQL_MAP_TYPE_DELETE_STATEMENT = "DELETE_STATEMENT";  //插入语句

    private final static String UPDATE_QUERY_PREFIX = "SYS_QUERY_"; //update查询时，为避免与entity参数混淆，故在query参数中增加前缀

    @Autowired
    private Config config;

    protected SqlMapClient getWriteSqlMapClient() {
        return config.getWriteSqlMapClient();
    }

    protected SqlMapClient getReadSqlMapClient() {
        return config.getReadSqlMapClient();
    }

    private Class entityClass;  //实体类

    /**
     * 获取表名,默认实现通过entityClass类名转化。子类可重载此方法用于数据库分表。
     * 默认实现部分表，直接通过实体类型，转化表名
     *
     * @param id       数据ID
     * @param sharding 指定分表规则
     * @param entity   实体
     * @param query    查询实体
     * @return 分表表名
     * @throws ApiException
     */
    public String getShardingTableName(String sharding, Long id, T entity, Q query) throws ApiException {
        return CamelCaseUtils.toUnderlineName(entityClass.getSimpleName());
    }

    /**
     * 构造函数
     *
     * @param entityClass 实体类
     */
    protected ApiBaseDaoImpl(Class entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * 根据ID查询
     *
     * @param id 记录ID
     * @return 查找到的实体，未找到则返回null
     * @throws ApiException
     */
    public T findById(Long id) throws ApiException {
        return findById(id, false);
    }

    /**
     * 根据ID查询，附分表逻辑
     *
     * @param sharding 分表规则
     * @param id       记录ID
     * @return 查找到的实体，未找到则返回null
     * @throws ApiException
     */
    public T findById(String sharding, Long id) throws ApiException {
        return findById(sharding, id, false);
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @param useWriteDataSource 是否使用写数据源
     * @return 查找到的实体，未找到则返回null
     * @throws ApiException
     */
    public T findById(Long id, boolean useWriteDataSource) throws ApiException {
        return findById(null, id, useWriteDataSource);
    }

    /**
     * 根据ID查询
     *
     * @param sharding           分表规则
     * @param id
     * @param useWriteDataSource 是否使用写数据源
     * @return 查找到的实体，未找到则返回null
     * @throws ApiException
     */
    public T findById(String sharding, Long id, boolean useWriteDataSource) throws ApiException {
        //根据实体Bean，拼装select语句
        String sql = new StringBuilder("SELECT ").append(getSelectStatement()).append(" FROM ").append(getShardingTableName(sharding, id, null, null)).append(" WHERE id = #id#").toString();
        logger.info("执行SQL：" + sql);
        //启用UMP监控
        String umpKey = new StringBuilder(this.getClass().getName()).append(".findById").append("_").append(sql.hashCode()).toString();
        if (Config.SQL_LOG.containsKey(umpKey))
            Config.SQL_LOG.put(umpKey, sql);

        SqlMapClient sqlMapClient = getReadSqlMapClient();
        if (useWriteDataSource)
            sqlMapClient = getWriteSqlMapClient();
        //构造返回值实例
        Object entity = null;
        try {
            //拼装Sql参数
            Map<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.put("id", id);
            //执行查询，返回Map
            setMappedStatement(sqlMapClient, SQL_MAP_TYPE_SELECT_STATEMENT, sql, paramsMap.getClass());   //重新设置MappedStatement
            Map<String, Object> rsMap = (Map<String, Object>) sqlMapClient.queryForObject(sql, paramsMap);
            //构造返回实体
            entity = (rsMap == null ? null : convertMap(entityClass, rsMap));
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            logger.error(sql);
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_SQL_EXCEPTION, e.getMessage() + "\n" + sql);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_UNKNOWN, e.getMessage() + "\n" + sql);
        }
        return (T) entity;
    }

    /**
     * 根据查询条件进行查询
     *
     * @param query
     * @return 查找结果值List<Map<String, Object>>
     * @throws ApiException
     */
    public List<Map<String, Object>> findForMap(Q query) throws ApiException {
        return findForMap(null, query, false);
    }

    /**
     * 根据查询条件进行查询
     *
     * @param sharding 分表规则
     * @param query
     * @return 查找结果值List<Map<String, Object>>
     * @throws ApiException
     */
    public List<Map<String, Object>> findForMap(String sharding, Q query) throws ApiException {
        return findForMap(sharding, query, false);
    }

    /**
     * 根据查询条件进行查询
     *
     * @param query
     * @param useWriteDataSource 是否使用写数据源
     * @return 查找结果值List<Map<String, Object>>
     * @throws ApiException
     */
    public List<Map<String, Object>> findForMap(Q query, boolean useWriteDataSource) throws ApiException {
        return findForMap(null, query, useWriteDataSource);
    }

    /**
     * 根据查询条件进行查询
     *
     * @param sharding           分表规则
     * @param query
     * @param useWriteDataSource 是否使用写数据源
     * @return 查找结果值List<Map<String, Object>>
     * @throws ApiException
     */
    public List<Map<String, Object>> findForMap(String sharding, Q query, boolean useWriteDataSource) throws ApiException {
        //拼装动态SQL
        String sql = buildSqlFindByQuery(sharding, query).toString();
        logger.info("执行SQL：" + sql);
        //启用UMP监控
        String umpKey = new StringBuilder(this.getClass().getName()).append(".findForMap").append("_").append(sql.hashCode()).toString();
        if (!Config.SQL_LOG.containsKey(umpKey))
            Config.SQL_LOG.put(umpKey, sql);

        SqlMapClient sqlMapClient = getReadSqlMapClient();
        if (useWriteDataSource)
            sqlMapClient = getWriteSqlMapClient();
        List<Map<String, Object>> rsList;
        try {
            setMappedStatement(sqlMapClient, SQL_MAP_TYPE_SELECT_STATEMENT, sql, Map.class);   //重新设置MappedStatement
            rsList = sqlMapClient.queryForList(sql, convertBeanToMap(query, ""));
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            logger.error(sql);
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_SQL_EXCEPTION, e.getMessage() + "\n" + sql);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_UNKNOWN, e.getMessage() + "\n" + sql);
        }
        return rsList;
    }

    /**
     * 根据查询条件进行查询
     *
     * @param query
     * @return 查找结果值List<T>
     * @throws ApiException
     */
    public List<T> find(Q query) throws ApiException {
        return find(null, query, false);
    }

    /**
     * 根据查询条件进行查询
     *
     * @param sharding 分表规则
     * @param query
     * @return 查找结果值List<T>
     * @throws ApiException
     */
    public List<T> find(String sharding, Q query) throws ApiException {
        return find(sharding, query, false);
    }

    /**
     * 根据查询条件进行查询
     *
     * @param query
     * @param useWriteDataSource 是否使用写数据源
     * @return 查找结果值List<T>
     * @throws ApiException
     */
    public List<T> find(Q query, boolean useWriteDataSource) throws ApiException {
        return find(null, query, useWriteDataSource);
    }

    /**
     * 根据查询条件进行查询
     *
     * @param sharding           分表规则
     * @param query
     * @param useWriteDataSource 是否使用写数据源
     * @return 查找结果值List<T>
     * @throws ApiException
     */
    public List<T> find(String sharding, Q query, boolean useWriteDataSource) throws ApiException {
        List<T> rsList = new ArrayList<T>();
        List<Map<String, Object>> rsMapList = findForMap(sharding, query, useWriteDataSource);
        for (Map<String, Object> rowMap : rsMapList) {
            try {
                rsList.add((T) convertMap(entityClass, rowMap));
            } catch (Exception e) {
                logger.error("实体类转换异常：", e);
            }
        }
        return rsList;
    }

    /**
     * 查找总数
     *
     * @param query
     * @return 数据条数
     * @throws ApiException
     */
    public int count(Q query) throws ApiException {
        return count(null, query, false);
    }

    /**
     * 查找总数
     *
     * @param sharding 分表规则
     * @param query
     * @return 数据条数
     * @throws ApiException
     */
    public int count(String sharding, Q query) throws ApiException {
        return count(sharding, query, false);
    }

    /**
     * 查找总数
     *
     * @param query
     * @param useWriteDataSource 是否使用写数据源
     * @return 数据条数
     * @throws ApiException
     */
    public int count(Q query, boolean useWriteDataSource) throws ApiException {
        return count(null, query, useWriteDataSource);
    }

    /**
     * 查找总数
     *
     * @param sharding           分表规则
     * @param query
     * @param useWriteDataSource 是否使用写数据源
     * @return 数据条数
     * @throws ApiException
     */
    public int count(String sharding, Q query, boolean useWriteDataSource) throws ApiException {
        int count;
        //拼装动态SQL
        String sql = buildSqlCountByQuery(sharding, query).toString();
        logger.info("执行SQL：" + sql);
        //启用UMP监控
        String umpKey = new StringBuilder(this.getClass().getName()).append(".count").append("_").append(sql.hashCode()).toString();
        if (!Config.SQL_LOG.containsKey(umpKey))
            Config.SQL_LOG.put(umpKey, sql);

        SqlMapClient sqlMapClient = getReadSqlMapClient();
        if (useWriteDataSource)
            sqlMapClient = getWriteSqlMapClient();
        try {
            setMappedStatement(sqlMapClient, SQL_MAP_TYPE_SELECT_STATEMENT, sql, Map.class);   //重新设置MappedStatement
            Map<String, Object> rsMap = (Map<String, Object>) sqlMapClient.queryForObject(sql, convertBeanToMap(query, ""));
            count = ((Long) (rsMap.get("COUNT(1)"))).intValue();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            logger.error(sql);
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_SQL_EXCEPTION, e.getMessage() + "\n" + sql);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_UNKNOWN, e.getMessage() + "\n" + sql);
        }
        return count;
    }

    /**
     * 插入
     *
     * @param entity
     * @return 新插入数据ID
     * @throws ApiException
     */
    public Long insert(T entity) throws ApiException {
        return insert(null, entity);
    }

    /**
     * 插入
     *
     * @param entityList
     * @return 插入数据总数
     * @throws ApiException
     */
    public int insert(List<T> entityList) throws ApiException {
        return insert(null, entityList);
    }

    /**
     * 插入
     *
     * @param sharding 分表规则
     * @param entity
     * @return 新插入数据ID
     * @throws ApiException
     */
    public Long insert(String sharding, T entity) throws ApiException {
        Long newId;
        //根据实体Bean，拼装select语句
        String sql = buildSqlInsert(sharding, entity).toString();
        logger.info("执行SQL：" + sql);

        //启用UMP监控
        String umpKey = new StringBuilder(this.getClass().getName()).append(".insert").append("_").append(sql.hashCode()).toString();
        if (!Config.SQL_LOG.containsKey(umpKey))
            Config.SQL_LOG.put(umpKey, sql);

        try {
            setMappedStatement(getWriteSqlMapClient(), SQL_MAP_TYPE_INSERT_STATEMENT, sql, entity.getClass());
            Long oldId = 0L;
            if (entity.getId() != null && entity.getId() != 0) {
                oldId = entity.getId(); //如果实体中已指定ID值，那么返回指定ID值
            }
            newId = (Long) getWriteSqlMapClient().insert(sql, entity);
            if (oldId != 0L) {
                newId = oldId;
                entity.setId(oldId);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            logger.error(sql);
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_SQL_EXCEPTION, e.getMessage() + "\n" + sql);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_UNKNOWN, e.getMessage() + "\n" + sql);
        }
        return newId;
    }

    /**
     * 插入
     *
     * @param sharding   分表规则
     * @param entityList
     * @return 插入数据总数
     * @throws ApiException
     */
    public int insert(String sharding, List<T> entityList) throws ApiException {
        if (entityList == null || entityList.size() == 0)
            return 0;
        int rows = 0;
        //根据实体Bean，拼装select语句
        String sql = buildSqlInsert(sharding, entityList.get(0)).toString();
        logger.info("执行SQL：" + sql);

        //启用UMP监控
        String umpKey = new StringBuilder(this.getClass().getName()).append(".insert").append("_").append(sql.hashCode()).toString();
        if (!Config.SQL_LOG.containsKey(umpKey))
            Config.SQL_LOG.put(umpKey, sql);

        try {
            setMappedStatement(getWriteSqlMapClient(), SQL_MAP_TYPE_INSERT_STATEMENT, sql, entityList.get(0).getClass());
            SqlMapClient writeSqlMapClient = getWriteSqlMapClient();
            writeSqlMapClient.startBatch();
            for (T entity : entityList) {
                Long oldId = 0L;
                if (entity.getId() != null && entity.getId() != 0) {
                    oldId = entity.getId(); //如果实体中已指定ID值，那么保留此ID
                }
                writeSqlMapClient.insert(sql, entity);
                ++rows;
                if (oldId != 0L) {
                    entity.setId(oldId);    //如果指定ID进行插入操作，则返回原指定ID
                }
            }
            writeSqlMapClient.executeBatch();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            logger.error(sql);
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_SQL_EXCEPTION, e.getMessage() + "\n" + sql);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_UNKNOWN, e.getMessage() + "\n" + sql);
        }
        return rows;
    }

    /**
     * 插入或更新
     *
     * @param entity
     * @return 返回带有ID值的对象
     * @throws ApiException
     */
    public T insertOrUpdate(T entity) throws ApiException {
        return insertOrUpdate(null, entity);
    }

    /**
     * 插入或更新
     *
     * @param sharding 分表规则
     * @param entity
     * @return 返回带有ID值的对象
     * @throws ApiException
     */
    public T insertOrUpdate(String sharding, T entity) throws ApiException {
        if (entity.getId() == null || entity.getId() == 0 || findById(entity.getId()) == null) {
            Long newId = insert(sharding, entity);
            entity.setId(newId);
        } else {
            update(sharding, entity);
        }
        return entity;
    }


    /**
     * 更新，默认不更新实体中的null字段
     *
     * @param entity
     * @return 影响行数
     * @throws ApiException
     */
    public int update(T entity) {
        return updateByQuery(entity, null);
    }

    /**
     * 更新，默认不更新实体中的null字段
     *
     * @param entityList
     * @return 影响行数
     * @throws ApiException
     */
    public int update(List<T> entityList) {
        return updateByQuery(null,entityList,null,false);
    }

    /**
     * 更新，默认不更新实体中的null字段
     *
     * @param sharding 分表规则
     * @param entity
     * @return 影响行数
     * @throws ApiException
     */
    public int update(String sharding, T entity) {
        return updateByQuery(sharding, entity, null);
    }

    /**
     * 更新，默认不更新实体中的null字段
     *
     * @param sharding 分表规则
     * @param entityList
     * @return 影响行数
     * @throws ApiException
     */
    public int update(String sharding, List<T> entityList) {
        return updateByQuery(sharding, entityList,null, false);
    }


    /**
     * 更新
     *
     * @param entity
     * @param includeNullProperties 当true，则更新entity中为空的字段
     * @return 影响行数
     * @throws ApiException
     */
    public int update(T entity, boolean includeNullProperties) {
        return updateByQuery(entity, null, includeNullProperties);
    }

    /**
     * 更新
     *
     * @param entityList
     * @param includeNullProperties 当true，则更新entity中为空的字段
     * @return 影响行数
     * @throws ApiException
     */
    public int update(List<T> entityList, boolean includeNullProperties) {
        return updateByQuery(null,entityList,null ,includeNullProperties);
    }

    /**
     * 更新
     *
     * @param sharding              分表规则
     * @param entity
     * @param includeNullProperties 当true，则更新entity中为空的字段
     * @return 影响行数
     * @throws ApiException
     */
    public int update(String sharding, T entity, boolean includeNullProperties) {
        return updateByQuery(sharding, entity, null, includeNullProperties);
    }

    /**
     * 更新
     *
     * @param sharding              分表规则
     * @param entityList
     * @param includeNullProperties 当true，则更新entity中为空的字段
     * @return 影响行数
     * @throws ApiException
     */
    public int update(String sharding, List<T> entityList, boolean includeNullProperties) {
        return updateByQuery(sharding, entityList, null, includeNullProperties);
    }

    /**
     * 根据查询更新记录，，默认不更新实体中的null字段
     *
     * @param entity
     * @param query
     * @return 影响行数
     * @throws ApiException
     */
    public int updateByQuery(T entity, Q query) {
        return updateByQuery(null, entity, query);
    }

    /**
     * 根据查询更新记录，，默认不更新实体中的null字段
     *
     * @param sharding 分表规则
     * @param entity
     * @param query
     * @return 影响行数
     * @throws ApiException
     */
    public int updateByQuery(String sharding, T entity, Q query) {
        return updateByQuery(sharding, entity, query, false);
    }

    /**
     * 根据查询更新记录
     *
     * @param entity
     * @param query
     * @param includeNullProperties 当true，则更新entity中为空的字段
     * @return
     * @throws ApiException
     */
    public int updateByQuery(T entity, Q query, boolean includeNullProperties) throws ApiException {
        return updateByQuery(null, entity, query, includeNullProperties);
    }

    /**
     * 根据查询更新记录
     *
     * @param sharding              分表规则
     * @param entity
     * @param query
     * @param includeNullProperties 当true，则更新entity中为空的字段
     * @return
     * @throws ApiException
     */
    public int updateByQuery(String sharding, T entity, Q query, boolean includeNullProperties) throws ApiException {
        return updateByQuery(sharding, Collections.singletonList(entity), query, includeNullProperties);
    }

    /**
     * 根据查询更新记录
     *
     * @param sharding              分表规则
     * @param entityList
     * @param query
     * @param includeNullProperties 当true，则更新entity中为空的字段
     * @return
     * @throws ApiException
     */
    private int updateByQuery(String sharding, List<T> entityList, Q query, boolean includeNullProperties) throws ApiException {
        if (entityList == null || entityList.size() == 0) { //如果传入实体为空，则返回0
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_ENTITY_IS_NULL, "传入的实体不能为null！");
        }
        //根据实体Bean，拼装select语句
        String sql = buildSqlUpdate(sharding, entityList.get(0), query, includeNullProperties).toString();
        logger.info("执行SQL：" + sql);
        //启用UMP监控
        String umpKey = new StringBuilder(this.getClass().getName()).append(".updateByQuery").append("_").append(sql.hashCode()).toString();
        if (!Config.SQL_LOG.containsKey(umpKey))
            Config.SQL_LOG.put(umpKey, sql);

        int rows;
        boolean entityIdFlag = true;
        boolean queryIdFlag = false;
        boolean queryIdInFlag = false;

        for(T entity : entityList){
            if (entity == null || entity.getId() == null || entity.getId() == 0L){
                entityIdFlag = false;
                break;
            }
        }
        if (query != null) {
            if (query.getId() != null && query.getId() != 0L)
                queryIdFlag = true;
            if (query.getId_IN() != null && query.getId_IN().length > 0)
                queryIdInFlag = true;
        }
        if (entityIdFlag || queryIdFlag || queryIdInFlag) { //三种ID值的限定条件，只要有一种成立，即可进行更新操作
            try {
                if(entityList.size() > 1){
                    rows = 0;
                    SqlMapClient writeSqlMapClient = getWriteSqlMapClient();
                    writeSqlMapClient.startBatch();
                    for(T entity : entityList){
                        //将entity内容放入参数列表
                        Map<String, Object> paramsMap = convertBeanToMap(entity, "");
                        //将query内容放入参数列表
                        if (query != null) {
                            paramsMap.putAll(convertBeanToMap(query, UPDATE_QUERY_PREFIX));
                        }
                        setMappedStatement(getWriteSqlMapClient(), SQL_MAP_TYPE_UPDATE_STATEMENT, sql, Map.class);
                        rows = rows + writeSqlMapClient.update(sql, paramsMap);
                    }
                    writeSqlMapClient.executeBatch();
                } else {
                    //将entity内容放入参数列表
                    Map<String, Object> paramsMap = convertBeanToMap(entityList.get(0), "");
                    //将query内容放入参数列表
                    if (query != null) {
                        paramsMap.putAll(convertBeanToMap(query, UPDATE_QUERY_PREFIX));
                    }
                    setMappedStatement(getWriteSqlMapClient(), SQL_MAP_TYPE_UPDATE_STATEMENT, sql, Map.class);
                    rows = getWriteSqlMapClient().update(sql, paramsMap);
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
                logger.error(sql);
                throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_SQL_EXCEPTION, e.getMessage() + "\n" + sql + "\n");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_UNKNOWN, e.getMessage() + "\n" + sql);
            }
        } else {
            //当entity和query中都没有Id限定时，不执行更新操作，避免造成数据误操作风险
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_UPDATE_NO_ID, "更新操作未限定数据ID范围！");
        }
        return rows;
    }

    /**
     * 删除
     *
     * @param id
     * @return 影响行数
     * @throws ApiException
     */
    public int delete(Long id) throws ApiException {
        return delete(null, id);
    }

    /**
     * 删除
     *
     * @param sharding 分表规则
     * @param id
     * @return 影响行数
     * @throws ApiException
     */
    public int delete(String sharding, Long id) {
        int rows;
        String sql = new StringBuilder("DELETE FROM ").append(getShardingTableName(sharding, id, null, null)).append(" WHERE ID = #value#").toString();
        logger.info("执行SQL：" + sql);
        //启用UMP监控
        String umpKey = new StringBuilder(this.getClass().getName()).append(".delete").append("_").append(sql.hashCode()).toString();
        if (!Config.SQL_LOG.containsKey(umpKey))
            Config.SQL_LOG.put(umpKey, sql);

        try {
            setMappedStatement(getWriteSqlMapClient(), SQL_MAP_TYPE_DELETE_STATEMENT, sql, Long.class);
            rows = getWriteSqlMapClient().delete(sql, id);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            logger.error(sql);
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_SQL_EXCEPTION, e.getMessage() + "\n" + sql);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_UNKNOWN, e.getMessage() + "\n" + sql);
        }
        return rows;
    }

    /**
     * 将一个 Map 对象转化为一个 JavaBean
     *
     * @param type 要转化的类型
     * @param map  包含属性值的 map
     * @return 转化出来的 JavaBean 对象
     */
    @SuppressWarnings("rawtypes")
    private Object convertMap(Class type, Map map) throws Exception {
        if (map == null)
            return null;
        BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
        Object obj = type.newInstance(); // 创建 JavaBean 对象
        // 给 JavaBean 对象的属性赋值
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = CamelCaseUtils.toUnderlineName(descriptor.getName(), true).toLowerCase();
            if (map.containsKey(propertyName)) {
                try {
                    Object value = convertType(map.get(propertyName));
                    Object[] args = new Object[1];
                    args[0] = value;
                    descriptor.getWriteMethod().invoke(obj, args);
                } catch (Exception e) {
                    logger.error("写入值发生错误。属性名：" + propertyName + ",属性类型：" + map.get(propertyName).getClass().getName() + ",属性值：" + map.get(propertyName).toString(), e);
                }
            }
        }
        return obj;
    }

    /**
     * 做返回的类型转换
     * BigInteger（有的时候数据库是bigint ibatis查询后是BigInteger）需要转换为long
     *
     * @param obj
     * @return
     */
    private Object convertType(Object obj) {
        if (null == obj) {
            return obj;
        }
        if (obj instanceof BigInteger) {
            return ((BigInteger) obj).longValue();
        }
        return obj;
    }

    /**
     * 将Bean转换为参数Map
     *
     * @param bean
     * @return
     */
    private Map<String, Object> convertBeanToMap(Object bean, String keyPrefix) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (bean == null)
            return paramMap;
        if (keyPrefix == null)
            keyPrefix = "";
        if (bean instanceof BaseQuery) {
            BaseQuery query = (BaseQuery) bean;
            paramMap.put("OFFSET", query.getOFFSET());
            paramMap.put("PAGE_SIZE", query.getPAGE_SIZE());
            if (query.getPARAMS() != null)
                paramMap.putAll(query.getPARAMS());
        }
        PropertyDescriptor[] beanGetters = ReflectUtils.getBeanProperties(bean.getClass());
        for (PropertyDescriptor propertyDescriptor : beanGetters) {
            if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                try {
                    Object object = propertyDescriptor.getReadMethod().invoke(bean);
                    if (object instanceof Boolean[]) {
                        int len = ((Boolean[]) object).length;
                        boolean[] array = new boolean[len];
                        for (int i = 0; i < len; i++) {
                            array[i] = ((Boolean[]) object)[i].booleanValue();
                        }
                        object = array;
                    } else if (object instanceof Double[]) {
                        int len = ((Double[]) object).length;
                        double[] array = new double[len];
                        for (int i = 0; i < len; i++) {
                            array[i] = ((Double[]) object)[i].doubleValue();
                        }
                        object = array;
                    } else if (object instanceof Float[]) {
                        int len = ((Float[]) object).length;
                        float[] array = new float[len];
                        for (int i = 0; i < len; i++) {
                            array[i] = ((Float[]) object)[i].floatValue();
                        }
                        object = array;
                    } else if (object instanceof Integer[]) {
                        int len = ((Integer[]) object).length;
                        int[] array = new int[len];
                        for (int i = 0; i < len; i++) {
                            array[i] = ((Integer[]) object)[i].intValue();
                        }
                        object = array;
                    } else if (object instanceof Long[]) {
                        int len = ((Long[]) object).length;
                        long[] array = new long[len];
                        for (int i = 0; i < len; i++) {
                            array[i] = ((Long[]) object)[i].longValue();
                        }
                        object = array;
                    } else if (object instanceof Short[]) {
                        int len = ((Double[]) object).length;
                        double[] array = new double[len];
                        for (int i = 0; i < len; i++) {
                            array[i] = ((Double[]) object)[i].doubleValue();
                        }
                        object = array;
                    }
                    paramMap.put(keyPrefix + propertyDescriptor.getName(), object);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return paramMap;
    }

    private static final InlineParameterMapParser PARAM_PARSER = new InlineParameterMapParser();

    /**
     * 设置MappedStatement
     *
     * @param sqlMapClient
     * @param sqlType
     * @param sqlStatement
     * @param parameterClass 参数类型
     */
    private void setMappedStatement(SqlMapClient sqlMapClient, String sqlType, String sqlStatement, Class parameterClass) {
        SqlMapExecutorDelegate delegate = ((SqlMapClientImpl) sqlMapClient).getDelegate();
        try {
            delegate.getMappedStatement(sqlStatement);
        } catch (SqlMapException e) {
            logger.info("SQL未读入：" + sqlStatement);
            logger.info("开始读入SQL至SqlMap...");
            MappedStatement statement = null;
            Class resultClass = null;
            if (SQL_MAP_TYPE_SELECT_STATEMENT.equals(sqlType)) {
                statement = new SelectStatement();
                resultClass = HashMap.class;
            } else if (SQL_MAP_TYPE_INSERT_STATEMENT.equals(sqlType)) {
                statement = new InsertStatement();
                //resultClass = java.util.HashMap.class;
            } else if (SQL_MAP_TYPE_UPDATE_STATEMENT.equals(sqlType)) {
                statement = new UpdateStatement();
                //resultClass = java.util.HashMap.class;
            } else if (SQL_MAP_TYPE_DELETE_STATEMENT.equals(sqlType)) {
                statement = new DeleteStatement();
                //resultClass = java.util.HashMap.class;
            }
            statement.setId(sqlStatement);
            statement.setResource(null);
            statement.setParameterClass(parameterClass);

            ParameterMap map = new ParameterMap(delegate);
            map.setId(statement.getId() + "-InlineParameterMap");
            map.setParameterClass(parameterClass);
            map.setResource(statement.getResource());
            statement.setParameterMap(map);

            SqlText sqlText = PARAM_PARSER.parseInlineParameterMap(delegate.getTypeHandlerFactory(), sqlStatement, statement.getParameterClass());
            String newSql = sqlText.getText();
            List mappingList = Arrays.asList(sqlText.getParameterMappings());
            map.setParameterMappingList(mappingList);
            Sql sql;
            if (SimpleDynamicSql.isSimpleDynamicSql(newSql)) {
                sql = new SimpleDynamicSql(delegate, newSql);
            } else {
                sql = new StaticSql(newSql);
            }
            statement.setParameterClass(parameterClass);
            statement.setSql(sql);
            ResultMap resultMap;
            resultMap = new AutoResultMap(delegate, true);
            resultMap.setId(statement.getId() + "-AutoResultMap");
            resultMap.setResultClass(resultClass);
            resultMap.setXmlName(null);
            resultMap.setResource(statement.getResource());
            statement.setResultMap(resultMap);
            statement.setTimeout(null);
            statement.setSqlMapClient(sqlMapClient);
            if (statement instanceof InsertStatement)
                setSelectKeyStatement((InsertStatement) statement, sqlMapClient);
            try {
                delegate.addMappedStatement(statement);
            } catch (Exception ee) {
                logger.info("该SQL在SQL Map中已存在！{}", ee.getMessage());
            }
        }
    }

    private void setSelectKeyStatement(InsertStatement insertStatement, SqlMapClient client) {
        SqlMapExecutorDelegate delegate = ((SqlMapClientImpl) client).getDelegate();
        SelectKeyStatement selectKeyStatement = new SelectKeyStatement();
        selectKeyStatement.setSqlMapClient(client);
        selectKeyStatement.setId(insertStatement.getId() + "-SelectKey");
        selectKeyStatement.setResource(insertStatement.getResource());
        selectKeyStatement.setKeyProperty("id");
        selectKeyStatement.setRunAfterSQL(true);

        String newSql = "SELECT @@IDENTITY as id";
        ParameterMap parameterMap = selectKeyStatement.getParameterMap();
        if (parameterMap == null) {
            ParameterMap map = new ParameterMap(delegate);
            map.setId(selectKeyStatement.getId() + "-InlineParameterMap");
            map.setParameterClass(selectKeyStatement.getParameterClass());
            map.setResource(selectKeyStatement.getResource());
            selectKeyStatement.setParameterMap(map);
            SqlText sqlText = PARAM_PARSER.parseInlineParameterMap(delegate.getTypeHandlerFactory(), newSql, selectKeyStatement.getParameterClass());
            newSql = sqlText.getText();
            List mappingList = Arrays.asList(sqlText.getParameterMappings());
            map.setParameterMappingList(mappingList);
        }
        Sql sql;
        if (SimpleDynamicSql.isSimpleDynamicSql(newSql)) {
            sql = new SimpleDynamicSql(delegate, newSql);
        } else {
            sql = new StaticSql(newSql);
        }
        selectKeyStatement.setSql(sql);
        ResultMap resultMap = new AutoResultMap(delegate, false);
        resultMap.setId(selectKeyStatement.getId() + "-AutoResultMap");
        resultMap.setResultClass(Long.class);
        resultMap.setResource(selectKeyStatement.getResource());
        selectKeyStatement.setResultMap(resultMap);

        insertStatement.setSelectKeyStatement(selectKeyStatement);
    }

    private String getSelectStatement() {
        StringBuilder selectCondition = new StringBuilder();
        PropertyDescriptor[] beanGetters = ReflectUtils.getBeanProperties(entityClass);
        for (PropertyDescriptor propertyDescriptor : beanGetters) {
            if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null)
                selectCondition.append(CamelCaseUtils.toUnderlineName(propertyDescriptor.getName(), true)).append(",");
        }
        selectCondition.deleteCharAt(selectCondition.length() - 1);
        return selectCondition.toString();
    }

    /**
     * 根据Query查询条件，拼装SQL
     *
     * @param query
     * @return
     */
    private StringBuilder buildSqlFindByQuery(String sharding, Q query) throws ApiException {
        if (query == null)
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_QUERY_IS_NULL, "查询参数不能为空！");
        StringBuilder sql = new StringBuilder();
        //根据实体Bean，拼装select语句
        StringBuilder selectCondition = new StringBuilder();
        if (query.getSELECT() == null || query.getSELECT().length == 0) {
            selectCondition.append(getSelectStatement());
        } else {
            for (String selectColumn : query.getSELECT()) {
                selectCondition.append(selectColumn.toLowerCase()).append(",");
            }
            selectCondition.deleteCharAt(selectCondition.length() - 1);
        }
        sql.append("SELECT ").append(selectCondition).append(" FROM ").append(getShardingTableName(sharding, null, null, query)).append(" WHERE").append(buildWhereByQuery(query));
        //Group By 语句
        if (query.getGroupBy() != null && query.getGroupBy().length > 0) {
            sql.append(" GROUP BY ");
            for (String groupByColumn : query.getGroupBy()) {
                sql.append(groupByColumn.toLowerCase()).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
        }
        //Order By 语句
        if (query.getOrderBy() != null && query.getOrderBy().length > 0) {
            sql.append(" ORDER BY ");
            for (String orderByColumn : query.getOrderBy()) {
                sql.append(orderByColumn.toLowerCase()).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
        }
        //分页语句
        if (query.getPAGE_SIZE() > 0 && query.getOFFSET() >= 0) {
            sql.append(" LIMIT #OFFSET#,#PAGE_SIZE# ");
        }
        return sql;
    }

    /**
     * 根据Query查询条件，拼装Count SQL
     *
     * @param query
     * @return
     */
    private StringBuilder buildSqlCountByQuery(String sharding, Q query) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(1) FROM ").append(getShardingTableName(sharding, null, null, query)).append(" WHERE").append(buildWhereByQuery(query));
        return sql;
    }

    //构造Where查询语句
    private String buildWhereByQuery(Q query) {
        return buildWhereByQuery(query, "", "");
    }

    //构造Where查询语句
    private String buildWhereByQuery(Q query, String queryPrefix, String querySuffix) {
        //构造where语句
        StringBuilder whereCondition = new StringBuilder();
        PropertyDescriptor[] beanGetters = ReflectUtils.getBeanProperties(query.getClass());
        for (PropertyDescriptor propertyDescriptor : beanGetters) {
            if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                try {
                    Object value = propertyDescriptor.getReadMethod().invoke(query);
                    if (value != null) {
                        String name = propertyDescriptor.getName();
                        if (name.contains("_")) {
                            String[] nameArray = name.split("_");
                            whereCondition.append(" AND ").append(CamelCaseUtils.toUnderlineName(nameArray[0], true));
                            if (QUERY_SUFFIX_IN.equals(nameArray[1])) {
                                whereCondition.append(" IN (");
                                Object[] valueArray = (Object[]) value;
                                for (int i = 0; i < valueArray.length; i++) {
                                    whereCondition.append("#").append(queryPrefix).append(name).append("[").append(i).append("]").append("#").append(",");
                                }
                                whereCondition.deleteCharAt(whereCondition.length() - 1);
                                whereCondition.append(")");
                            } else if (QUERY_SUFFIX_NE.equals(nameArray[1])) {
                                whereCondition.append(" <> ").append("#").append(queryPrefix).append(name).append(querySuffix).append("#");
                            } else if (QUERY_SUFFIX_LT.equals(nameArray[1])) {
                                whereCondition.append(" < ").append("#").append(queryPrefix).append(name).append(querySuffix).append("#");
                            } else if (QUERY_SUFFIX_LE.equals(nameArray[1])) {
                                whereCondition.append(" <= ").append("#").append(queryPrefix).append(name).append(querySuffix).append("#");
                            } else if (QUERY_SUFFIX_GT.equals(nameArray[1])) {
                                whereCondition.append(" > ").append("#").append(queryPrefix).append(name).append(querySuffix).append("#");
                            } else if (QUERY_SUFFIX_GE.equals(nameArray[1])) {
                                whereCondition.append(" >= ").append("#").append(queryPrefix).append(name).append(querySuffix).append("#");
                            } else if (QUERY_SUFFIX_LK.equals(nameArray[1])) {
                                whereCondition.append(" LIKE ").append("#").append(queryPrefix).append(name).append(querySuffix).append("#");
                            }
                        } else {
                            whereCondition.append(" AND ").append(CamelCaseUtils.toUnderlineName(name, true)).append(" = #").append(queryPrefix).append(name).append(querySuffix).append("#");
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        //拼自定义Where的And逻辑
        for (Object whereAnd : query.getWhereAndList()) {
            whereCondition.append(" AND ").append(whereAnd);
        }
        //拼自定义Where的Or逻辑
        for (Object whereOr : query.getWhereOrList()) {
            whereCondition.append(" OR ").append(whereOr);
        }
        if (whereCondition.length() > 0) {
            return whereCondition.toString().replaceAll("^ (AND|OR)", "");
        } else {
            whereCondition.append(" 1 = 1");
            return whereCondition.toString();
        }
    }

    //构造Insert语句
    private StringBuilder buildSqlInsert(String sharding, T entity) throws ApiException {
        StringBuilder intoStatement = new StringBuilder();
        StringBuilder valueStatement = new StringBuilder();
        PropertyDescriptor[] beanGetters = ReflectUtils.getBeanProperties(entityClass);
        for (PropertyDescriptor propertyDescriptor : beanGetters) {
            if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                try {
                    if (propertyDescriptor.getReadMethod().invoke(entity) != null) {
                        valueStatement.append("#").append(propertyDescriptor.getName()).append("#,");
                        intoStatement.append(CamelCaseUtils.toUnderlineName(propertyDescriptor.getName(), true)).append(",");
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        if (intoStatement.length() == 0) {
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_ENTITY_IS_EMPTY, "传入的实体中不包含任何内容！");
        }
        valueStatement.deleteCharAt(valueStatement.length() - 1);
        intoStatement.deleteCharAt(intoStatement.length() - 1);
        return (new StringBuilder("INSERT INTO ").append(getShardingTableName(sharding, null, entity, null)).append(" (").append(intoStatement).append(") VALUES (").append(valueStatement).append(")"));
    }

    /**
     * 构造更新语句
     */
    private StringBuilder buildSqlUpdate(String sharding, T entity, Q query, boolean includeNullProperties) {
        StringBuilder setStatement = new StringBuilder();
        PropertyDescriptor[] beanGetters = ReflectUtils.getBeanProperties(entityClass);
        for (PropertyDescriptor propertyDescriptor : beanGetters) {
            if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                try {
                    Object value = propertyDescriptor.getReadMethod().invoke(entity);
                    if (!includeNullProperties && value == null) {
                        //当includeNullProperties为true，并且值为空时，不拼接set语句
                    } else {
                        String fieldName = propertyDescriptor.getName();
                        if (!"id".equals(fieldName.toLowerCase())) {    //update 语句中不set  ID字段
                            setStatement.append(CamelCaseUtils.toUnderlineName(fieldName, true)).append(" = ").append("#").append(fieldName).append("#,");
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        if (setStatement.length() == 0) {
            throw new ApiException(ApiExceptionErrorCodeEnum.ERROR_ENTITY_IS_EMPTY, "传入的实体中不包含任何内容！");
        }
        setStatement.deleteCharAt(setStatement.length() - 1);

        StringBuilder sql = new StringBuilder("UPDATE ").append(getShardingTableName(sharding, null, entity, query)).append(" SET ").append(setStatement).append(" WHERE");
        if (entity.getId() != null && entity.getId() != 0L) {
            sql.append(" id = #id#");
        } else {
            sql.append(" 1 = 1");
        }
        if (query != null) {
            sql.append(" AND");
            sql.append(buildWhereByQuery(query, UPDATE_QUERY_PREFIX, ""));
        }
        return sql;
    }
}
