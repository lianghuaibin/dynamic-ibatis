/*
 * Copyright (c) 2016, lhb All Rights Reserved.
 */
package com.lhb.plug.dynamicibatis;


import com.lhb.plug.dynamicibatis.domain.BasePojo;
import com.lhb.plug.dynamicibatis.domain.BaseQuery;

import java.util.List;
import java.util.Map;

/**
 * ApiBaseDao接口说明
 * 提供了基本的数据库增删改查操作方法
 *
 * @param <T> 数据库映射对象实体类
 * @param <Q> 数据库查询对象类
 * @author lianghuaibin
 * @since 2016/03/07
 * @see BasePojo
 * @see BaseQuery
 * @since 1.00
 */
public interface ApiBaseDao<T extends BasePojo, Q extends BaseQuery> {

    /**
     * Order By 语句，降序
     */
    String ORDER_BY_DESC = " DESC";
    /**
     * Order By 语句，升序
     */
    String ORDER_BY_ASC = " ASC";

    /**
     * BaseQuery实体后缀：IN条件
     */
    String QUERY_SUFFIX_IN = "IN";
    /**
     * BaseQuery实体后缀：不等于
     */
    String QUERY_SUFFIX_NE = "NE";
    /**
     * BaseQuery实体后缀：小于
     */
    String QUERY_SUFFIX_LT = "LT";
    /**
     * BaseQuery实体后缀：小于等于
     */
    String QUERY_SUFFIX_LE = "LE";
    /**
     * BaseQuery实体后缀：大于
     */
    String QUERY_SUFFIX_GT = "GT";
    /**
     * BaseQuery实体后缀：大于等于
     */
    String QUERY_SUFFIX_GE = "GE";
    /**
     * BaseQuery实体后缀：SQL LIKE
     */
    String QUERY_SUFFIX_LK = "LK";

    /**
     * 根据ID查询
     *
     * @param id 记录ID
     * @return 查找到的实体，未找到则返回null
     * @throws ApiException
     */
    T findById(Long id) throws ApiException;

    /**
     * 根据ID查询，附分表逻辑
     *
     * @param sharding 分表规则
     * @param id       记录ID
     * @return 查找到的实体，未找到则返回null
     * @throws ApiException
     */
    T findById(String sharding, Long id) throws ApiException;

    /**
     * 根据ID查询
     *
     * @param id
     * @param useWriteDataSource 是否使用写数据源
     * @return 查找到的实体，未找到则返回null
     * @throws ApiException
     */
    T findById(Long id, boolean useWriteDataSource) throws ApiException;

    /**
     * 根据ID查询
     *
     * @param sharding           分表规则
     * @param id
     * @param useWriteDataSource 是否使用写数据源
     * @return 查找到的实体，未找到则返回null
     * @throws ApiException
     */
    T findById(String sharding, Long id, boolean useWriteDataSource) throws ApiException;

    /**
     * 根据查询条件进行查询
     *
     * @param query
     * @return 查找结果值List<Map<String, Object>>
     * @throws ApiException
     */
    List<Map<String, Object>> findForMap(Q query) throws ApiException;

    /**
     * 根据查询条件进行查询
     *
     * @param sharding 分表规则
     * @param query
     * @return 查找结果值List<Map<String, Object>>
     * @throws ApiException
     */
    List<Map<String, Object>> findForMap(String sharding, Q query) throws ApiException;

    /**
     * 根据查询条件进行查询
     *
     * @param query
     * @param useWriteDataSource 是否使用写数据源
     * @return 查找结果值List<Map<String, Object>>
     * @throws ApiException
     */
    List<Map<String, Object>> findForMap(Q query, boolean useWriteDataSource) throws ApiException;

    /**
     * 根据查询条件进行查询
     *
     * @param sharding           分表规则
     * @param query
     * @param useWriteDataSource 是否使用写数据源
     * @return 查找结果值List<Map<String, Object>>
     * @throws ApiException
     */
    List<Map<String, Object>> findForMap(String sharding, Q query, boolean useWriteDataSource) throws ApiException;

    /**
     * 根据查询条件进行查询
     *
     * @param query
     * @return 查找结果值List<T>
     * @throws ApiException
     */
    List<T> find(Q query) throws ApiException;

    /**
     * 根据查询条件进行查询
     *
     * @param sharding 分表规则
     * @param query
     * @return 查找结果值List<T>
     * @throws ApiException
     */
    List<T> find(String sharding, Q query) throws ApiException;

    /**
     * 根据查询条件进行查询
     *
     * @param query
     * @param useWriteDataSource 是否使用写数据源
     * @return 查找结果值List<T>
     * @throws ApiException
     */
    List<T> find(Q query, boolean useWriteDataSource) throws ApiException;

    /**
     * 根据查询条件进行查询
     *
     * @param sharding           分表规则
     * @param query
     * @param useWriteDataSource 是否使用写数据源
     * @return 查找结果值List<T>
     * @throws ApiException
     */
    List<T> find(String sharding, Q query, boolean useWriteDataSource) throws ApiException;

    /**
     * 查找总数
     *
     * @param query
     * @return 数据条数
     * @throws ApiException
     */
    int count(Q query) throws ApiException;

    /**
     * 查找总数
     *
     * @param sharding 分表规则
     * @param query
     * @return 数据条数
     * @throws ApiException
     */
    int count(String sharding, Q query) throws ApiException;

    /**
     * 查找总数
     *
     * @param query
     * @param useWriteDataSource 是否使用写数据源
     * @return 数据条数
     * @throws ApiException
     */
    int count(Q query, boolean useWriteDataSource) throws ApiException;

    /**
     * 查找总数
     *
     * @param sharding           分表规则
     * @param query
     * @param useWriteDataSource 是否使用写数据源
     * @return 数据条数
     * @throws ApiException
     */
    int count(String sharding, Q query, boolean useWriteDataSource) throws ApiException;

    /**
     * 插入
     *
     * @param entity
     * @return 新插入数据ID
     * @throws ApiException
     */
    Long insert(T entity) throws ApiException;

    /**
     * 插入
     *
     * @param entityList
     * @return 插入数据总数
     * @throws ApiException
     */
    int insert(List<T> entityList) throws ApiException;

    /**
     * 插入
     *
     * @param sharding 分表规则
     * @param entity
     * @return 新插入数据ID
     * @throws ApiException
     */
    Long insert(String sharding, T entity) throws ApiException;

    /**
     * 插入
     *
     * @param sharding 分表规则
     * @param entity
     * @return 插入数据总数
     * @throws ApiException
     */
    int insert(String sharding, List<T> entity) throws ApiException;

    /**
     * 插入或更新
     *
     * @param entity
     * @return 返回带有ID值的对象
     * @throws ApiException
     */
    T insertOrUpdate(T entity) throws ApiException;

    /**
     * 插入或更新
     *
     * @param sharding 分表规则
     * @param entity
     * @return 返回带有ID值的对象
     * @throws ApiException
     */
    T insertOrUpdate(String sharding, T entity) throws ApiException;

    /**
     * 更新，默认不更新实体中的null字段
     *
     * @param entity
     * @return 影响行数
     * @throws ApiException
     */
    int update(T entity) throws ApiException;

    /**
     * 更新，默认不更新实体中的null字段
     *
     * @param entityList
     * @return 影响行数
     * @throws ApiException
     */
    int update(List<T> entityList) throws ApiException;

    /**
     * 更新，默认不更新实体中的null字段
     *
     * @param sharding 分表规则
     * @param entity
     * @return 影响行数
     * @throws ApiException
     */
    int update(String sharding, T entity) throws ApiException;

    /**
     * 更新，默认不更新实体中的null字段
     *
     * @param sharding 分表规则
     * @param entityList
     * @return 影响行数
     * @throws ApiException
     */
    int update(String sharding, List<T> entityList) throws ApiException;

    /**
     * 更新
     *
     * @param entity
     * @param includeNullProperties 当true，则更新entity中为空的字段
     * @return 影响行数
     * @throws ApiException
     */
    int update(T entity, boolean includeNullProperties) throws ApiException;

    /**
     * 更新
     *
     * @param entityList
     * @param includeNullProperties 当true，则更新entity中为空的字段
     * @return 影响行数
     * @throws ApiException
     */
    int update(List<T> entityList, boolean includeNullProperties) throws ApiException;

    /**
     * 更新
     *
     * @param sharding             分表规则
     * @param entity
     * @param includeNullProperties 当true，则更新entity中为空的字段
     * @return 影响行数
     * @throws ApiException
     */
    int update(String sharding, T entity, boolean includeNullProperties) throws ApiException;

    /**
     * 更新
     *
     * @param sharding             分表规则
     * @param entityList
     * @param includeNullProperties 当true，则更新entity中为空的字段
     * @return 影响行数
     * @throws ApiException
     */
    int update(String sharding, List<T> entityList, boolean includeNullProperties) throws ApiException;

    /**
     * 根据查询更新记录
     *
     * @param entity
     * @param query
     * @return 影响行数
     * @throws ApiException
     */
    int updateByQuery(T entity, Q query) throws ApiException;

    /**
     * 根据查询更新记录
     *
     * @param sharding 分表规则
     * @param entity
     * @param query
     * @return 影响行数
     * @throws ApiException
     */
    int updateByQuery(String sharding, T entity, Q query) throws ApiException;

    /**
     * 根据查询更新记录
     *
     * @param entity
     * @param query
     * @param includeNullProperties 当true，则更新entity中为空的字段
     * @return
     * @throws ApiException
     */
    int updateByQuery(T entity, Q query, boolean includeNullProperties) throws ApiException;

    /**
     * 根据查询更新记录
     *
     * @param sharding             分表规则
     * @param entity
     * @param query
     * @param includeNullProperties 当true，则更新entity中为空的字段
     * @return
     * @throws ApiException
     */
    int updateByQuery(String sharding, T entity, Q query, boolean includeNullProperties) throws ApiException;

    /**
     * 删除
     *
     * @param id
     * @return 影响行数
     * @throws ApiException
     */
    int delete(Long id) throws ApiException;

    /**
     * 删除
     *
     * @param sharding 分表规则
     * @param id
     * @return 影响行数
     * @throws ApiException
     */
    int delete(String sharding, Long id) throws ApiException;

}
