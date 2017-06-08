/*
 * Copyright (c) 2016, lhb All Rights Reserved.
 */
package com.lhb.plug.dynamicibatis.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础查询类
 *
 * @author lianghuaibin
 * @since 2016/03/07
 */
public class BaseQuery<T extends BaseQuery> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String[] mSelectArray;
    private String[] mGroupByArray;
    private String[] mOrderByArray;
    private List<String> mWhereOrList = new ArrayList<String>();
    private List<String> mWhereAndList = new ArrayList<String>();
    private int mPageSize;
    private int mOffset;
    private Map<String, Object> mParams = null;

    private Long id;    //自增主键
    private Long[] id_IN;  //id in
    private Long id_NE; //不等于:not equal
    private Long id_LT; //小于:less than
    private Long id_LE; //小于或等于:equal or less than
    private Long id_GT; //大于:greater than
    private Long id_GE; //大于或等于:equal or greater than

    /**
     * 自增主键
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 自增主键
     *
     * @return id
     */
    public Long getId() {
        return this.id;
    }

    public Long[] getId_IN() {
        return id_IN;
    }

    public void setId_IN(Long[] id_IN) {
        this.id_IN = id_IN;
    }

    public Long getId_NE() {
        return id_NE;
    }

    public void setId_NE(Long id_NE) {
        this.id_NE = id_NE;
    }

    public Long getId_LT() {
        return id_LT;
    }

    public void setId_LT(Long id_LT) {
        this.id_LT = id_LT;
    }

    public Long getId_LE() {
        return id_LE;
    }

    public void setId_LE(Long id_LE) {
        this.id_LE = id_LE;
    }

    public Long getId_GT() {
        return id_GT;
    }

    public void setId_GT(Long id_GT) {
        this.id_GT = id_GT;
    }

    public Long getId_GE() {
        return id_GE;
    }

    public void setId_GE(Long id_GE) {
        this.id_GE = id_GE;
    }

    public String[] getSELECT() {
        return mSelectArray;
    }

    /**
     * 自定义select语句
     *
     * @param SELECT
     * @return
     */
    public T select(String[] SELECT) {
        this.mSelectArray = SELECT;
        return (T) this;
    }

    public String[] getGroupBy() {
        return mGroupByArray;
    }

    /**
     * 自定义group by语句
     *
     * @param GROUP_BY
     * @return
     */
    public T groupBy(String[] GROUP_BY) {
        this.mGroupByArray = GROUP_BY;
        return (T) this;
    }

    public String[] getOrderBy() {
        return mOrderByArray;
    }

    /**
     * 自定义order by语句
     *
     * @param ORDER_BY
     * @return
     */
    public T orderBy(String[] ORDER_BY) {
        this.mOrderByArray = ORDER_BY;
        return (T) this;
    }

    /**
     * 增加PreparedStatement绑定参数
     *
     * @param key   参数key值，在SQL表达式中，使用#key#表示
     * @param value 值
     * @return
     */
    public T addParam(String key, Object value) {
        if (mParams == null)
            mParams = new HashMap<String, Object>();
        mParams.put(key, value);
        return (T) this;
    }

    /**
     * 分页查询
     *
     * @param pageNo   当前页数
     * @param pageSize 分页条数
     * @return
     */
    public T page(int pageNo, int pageSize) {
        this.mPageSize = pageSize;
        this.mOffset = (pageNo - 1) * pageSize;
        return (T) this;
    }

    public int getPAGE_SIZE() {
        return mPageSize;
    }

    public int getOFFSET() {
        return mOffset;
    }

    public Map<String, Object> getPARAMS() {
        return mParams;
    }

    public List<String> getWhereOrList() {
        return mWhereOrList;
    }

    /**
     * 自定义where中的or语句
     *
     * @param whereOr
     * @return
     */
    public T whereOr(String whereOr) {
        mWhereOrList.add(whereOr);
        return (T) this;
    }

    public List<String> getWhereAndList() {
        return mWhereAndList;
    }

    public T whereAnd(String whereAnd) {
        mWhereAndList.add(whereAnd);
        return (T) this;
    }
}
