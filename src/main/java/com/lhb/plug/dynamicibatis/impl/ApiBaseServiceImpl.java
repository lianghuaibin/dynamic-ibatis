/*
 * Copyright (c) 2016, lhb All Rights Reserved.
 */
package com.lhb.plug.dynamicibatis.impl;

import com.lhb.plug.dynamicibatis.ApiBaseDao;
import com.lhb.plug.dynamicibatis.ApiBaseService;
import com.lhb.plug.dynamicibatis.ApiException;
import com.lhb.plug.dynamicibatis.domain.BasePojo;
import com.lhb.plug.dynamicibatis.domain.BaseQuery;
import com.lhb.plug.dynamicibatis.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Map;

/**
 * ApiBaseServiceImpl
 * 提供了数据库增删改查基础服务
 *
 * @param <T> 数据库映射对象实体类
 * @param <Q> 数据库查询对象类
 * @author lianghuaibin
 * @since 2016/03/07
 * @see BasePojo
 * @see BaseQuery
 * @see ApiBaseService
 * @since 1.00
 */
public abstract class ApiBaseServiceImpl<T extends BasePojo, Q extends BaseQuery> implements ApiBaseService<T, Q> {

    public final static Logger logger = LoggerFactory.getLogger(ApiBaseServiceImpl.class);

    /**
     * 获取ApiBaseDao具体表的实现类，子类必须实现此方法
     *
     * @return
     */
    public abstract ApiBaseDao<T, Q> getApiBaseDao();

    @Autowired
    private Config config;

    private PlatformTransactionManager getTransactionManager() {
        return config.getTransactionManager();
    }

    /**
     * 根据ID查询
     *
     * @param id 记录ID
     * @return 查找到的实体，未找到则返回null
     * @throws ApiException
     */
    public T findById(Long id) throws ApiException {
        return getApiBaseDao().findById(id);
    }

    /**
     * 根据ID查询，附分表逻辑
     *
     * @param sharding 分表规则
     * @param id       记录ID
     * @return 查找到的实体，未找到则返回null
     * @throws ApiException
     */
    public T findByIdWithSharding(String sharding, Long id) throws ApiException {
        return getApiBaseDao().findById(sharding, id);
    }

    /**
     * 根据查询条件进行查询
     *
     * @param query
     * @return 查找结果值List<Map<String, Object>>
     * @throws ApiException
     */
    public List<Map<String, Object>> findForMap(Q query) throws ApiException {
        return getApiBaseDao().findForMap(query);
    }

    /**
     * 根据查询条件进行查询
     *
     * @param sharding 分表规则
     * @param query
     * @return 查找结果值List<Map<String, Object>>
     * @throws ApiException
     */
    public List<Map<String, Object>> findForMapWithSharding(String sharding, Q query) throws ApiException {
        return getApiBaseDao().findForMap(sharding, query);
    }

    /**
     * 根据查询条件进行查询
     *
     * @param query
     * @return 查找结果值List<T>
     * @throws ApiException
     */
    public List<T> find(Q query) throws ApiException {
        return getApiBaseDao().find(query);
    }

    /**
     * 根据查询条件进行查询
     *
     * @param sharding 分表规则
     * @param query
     * @return 查找结果值List<T>
     * @throws ApiException
     */
    public List<T> findWithSharding(String sharding, Q query) throws ApiException {
        return getApiBaseDao().find(sharding, query);
    }

    /**
     * 查找总数
     *
     * @param query
     * @return 数据条数
     * @throws ApiException
     */
    public int count(Q query) throws ApiException {
        return getApiBaseDao().count(query);
    }

    /**
     * 查找总数
     *
     * @param sharding 分表规则
     * @param query
     * @return 数据条数
     * @throws ApiException
     */
    public int countWithSharding(String sharding, Q query) throws ApiException {
        return getApiBaseDao().count(sharding, query);
    }


    /**
     * 插入
     *
     * @param entity
     * @return 新插入数据ID
     * @throws ApiException
     */
    public Long insert(T entity) throws ApiException {
        return getApiBaseDao().insert(entity);
    }

    /**
     * 插入
     *
     * @param sharding 分表规则
     * @param entity
     * @return 新插入数据ID
     * @throws ApiException
     */
    public Long insertWithSharding(String sharding, T entity) throws ApiException {
        return getApiBaseDao().insert(sharding, entity);
    }

    /**
     * 插入或更新
     *
     * @param entity
     * @return 返回带有ID值的对象
     * @throws ApiException
     */
    public T insertOrUpdate(T entity) throws ApiException {
        return getApiBaseDao().insertOrUpdate(entity);
    }

    /**
     * 插入或更新
     *
     * @param sharding 分表规则
     * @param entity
     * @return 返回带有ID值的对象
     * @throws ApiException
     */
    public T insertOrUpdateWithSharding(String sharding, T entity) throws ApiException {
        return getApiBaseDao().insertOrUpdate(sharding, entity);
    }

    /**
     * 更新，默认不更新实体中为null的字段
     *
     * @param entity
     * @return 影响行数
     * @throws ApiException
     */
    public int update(T entity) throws ApiException {
        return getApiBaseDao().update(entity);
    }

    /**
     * 更新，默认不更新实体中为null的字段
     *
     * @param sharding 分表规则
     * @param entity
     * @return 影响行数
     * @throws ApiException
     */
    public int updateWithSharding(String sharding, T entity) throws ApiException {
        return getApiBaseDao().update(sharding, entity);
    }

    /**
     * 更新,并更新实体中为空的字段
     *
     * @param entity
     * @return 影响行数
     * @throws ApiException
     */
    public int updateIncludeNullProperties(T entity) throws ApiException {
        return getApiBaseDao().update(entity, true);
    }

    /**
     * 更新,并更新实体中为空的字段
     *
     * @param sharding 分表规则
     * @param entity
     * @return 影响行数
     * @throws ApiException
     */
    public int updateIncludeNullPropertiesWithSharding(String sharding, T entity) throws ApiException {
        return getApiBaseDao().update(sharding, entity, true);
    }

    /**
     * 根据查询更新记录
     *
     * @param entity
     * @param query
     * @return 影响行数
     * @throws ApiException
     */
    public int updateByQuery(T entity, Q query) throws ApiException {
        return getApiBaseDao().updateByQuery(entity, query);
    }

    /**
     * 根据查询更新记录
     *
     * @param sharding 分表规则
     * @param entity
     * @param query
     * @return 影响行数
     * @throws ApiException
     */
    public int updateByQueryWithSharding(String sharding, T entity, Q query) throws ApiException {
        return getApiBaseDao().updateByQuery(sharding, entity, query);
    }

    /**
     * 根据查询更新记录，并更新实体中为空的字段
     *
     * @param entity
     * @param query
     * @return 影响行数
     * @throws ApiException
     */
    public int updateByQueryIncludeNullProperties(T entity, Q query) throws ApiException {
        return getApiBaseDao().updateByQuery(entity, query, true);
    }

    /**
     * 根据查询更新记录，并更新实体中为空的字段
     *
     * @param sharding 分表规则
     * @param entity
     * @param query
     * @return 影响行数
     * @throws ApiException
     */
    public int updateByQueryIncludeNullPropertiesWithSharding(String sharding, T entity, Q query) throws ApiException {
        return getApiBaseDao().updateByQuery(sharding, entity, query, true);
    }

    /**
     * 删除
     *
     * @param id
     * @return 影响行数
     * @throws ApiException
     * @deprecated 不建议直接对数据进行物理删除操作
     */
    @Deprecated
    public int delete(Long id) throws ApiException {
        return getApiBaseDao().delete(id);
    }

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
    public int deleteWithSharding(String sharding, Long id) throws ApiException {
        return getApiBaseDao().delete(sharding, id);
    }

    /**
     * 定义事物
     */
    protected TransactionStatus initTansactionStatus(
            PlatformTransactionManager transactionManager, int propagetion) {
        logger.debug("init the getTransactionManager() start!");

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();// 事务定义类
        def.setPropagationBehavior(propagetion);
        return transactionManager.getTransaction(def);
    }

    // 开始事务
    protected TransactionStatus startTansactionManager() {
        logger.debug("start the getTransactionManager() start!");

        TransactionStatus status = this
                .initTansactionStatus(getTransactionManager(), TransactionDefinition.PROPAGATION_REQUIRED);

        logger.debug("start the getTransactionManager() end!");

        return status;
    }

    // 提交事务
    protected void commitTansactionManager(TransactionStatus status) {
        logger.debug("commit the getTransactionManager() start!");

        getTransactionManager().commit(status);

        logger.debug("commit the getTransactionManager() success!");

    }

    // 回滚事务
    protected void rollbackTansactionManager(TransactionStatus status) {
        logger.debug("rollback the getTransactionManager() start!");

        if (status != null)
            getTransactionManager().rollback(status);

        logger.debug("rollback the getTransactionManager() success!");

    }

}
