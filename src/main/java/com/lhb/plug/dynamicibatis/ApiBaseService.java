/*
 * Copyright (c) 2016, lhb All Rights Reserved.
 */
package com.lhb.plug.dynamicibatis;



import com.lhb.plug.dynamicibatis.domain.BasePojo;
import com.lhb.plug.dynamicibatis.domain.BaseQuery;

import java.util.List;
import java.util.Map;

/**
 * ApiBaseService接口说明
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
public interface ApiBaseService<T extends BasePojo, Q extends BaseQuery> {

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
    T findByIdWithSharding(String sharding, Long id) throws ApiException;

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
    List<Map<String, Object>> findForMapWithSharding(String sharding, Q query) throws ApiException;


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
    List<T> findWithSharding(String sharding, Q query) throws ApiException;

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
    int countWithSharding(String sharding, Q query) throws ApiException;


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
     * @param sharding 分表规则
     * @param entity
     * @return 新插入数据ID
     * @throws ApiException
     */
    Long insertWithSharding(String sharding, T entity) throws ApiException;

    /**
     * 插入或更新，默认不更新实体中为null的字段
     *
     * @param entity
     * @return 返回带有ID值的对象
     * @throws ApiException
     */
    T insertOrUpdate(T entity) throws ApiException;

    /**
     * 插入或更新，默认不更新实体中为null的字段
     *
     * @param sharding 分表规则
     * @param entity
     * @return 返回带有ID值的对象
     * @throws ApiException
     */
    T insertOrUpdateWithSharding(String sharding, T entity) throws ApiException;

    /**
     * 更新，默认不更新实体中为null的字段
     *
     * @param entity
     * @return 影响行数
     * @throws ApiException
     */
    int update(T entity) throws ApiException;

    /**
     * 更新，默认不更新实体中为null的字段
     *
     * @param sharding 分表规则
     * @param entity
     * @return 影响行数
     * @throws ApiException
     */
    int updateWithSharding(String sharding, T entity) throws ApiException;

    /**
     * 更新,并更新实体中为空的字段
     *
     * @param entity
     * @return 影响行数
     * @throws ApiException
     */
    int updateIncludeNullProperties(T entity) throws ApiException;

    /**
     * 更新,并更新实体中为空的字段
     *
     * @param sharding 分表规则
     * @param entity
     * @return 影响行数
     * @throws ApiException
     */
    int updateIncludeNullPropertiesWithSharding(String sharding, T entity) throws ApiException;

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
    int updateByQueryWithSharding(String sharding, T entity, Q query) throws ApiException;

    /**
     * 根据查询更新记录，并更新实体中为空的字段
     *
     * @param entity
     * @param query
     * @return 影响行数
     * @throws ApiException
     */
    int updateByQueryIncludeNullProperties(T entity, Q query) throws ApiException;

    /**
     * 根据查询更新记录，并更新实体中为空的字段
     *
     * @param sharding 分表规则
     * @param entity
     * @param query
     * @return 影响行数
     * @throws ApiException
     */
    int updateByQueryIncludeNullPropertiesWithSharding(String sharding, T entity, Q query) throws ApiException;

    /**
     * 删除
     *
     * @param id
     * @return 影响行数
     * @throws ApiException
     * @deprecated 不建议直接对数据进行物理删除操作
     */
    @Deprecated
    int delete(Long id) throws ApiException;

    /**
     * 删除（根据分表规则）
     *
     * @param sharding 分表规则
     * @param id
     * @return 影响行数
     * @throws ApiException
     * @deprecated 不建议直接对数据进行物理删除操作
     */
    @Deprecated
    int deleteWithSharding(String sharding, Long id) throws ApiException;

}
