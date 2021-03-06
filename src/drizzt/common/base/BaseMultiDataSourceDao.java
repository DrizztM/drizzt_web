package drizzt.common.base;

import org.hibernate.Session;

import drizzt.common.datasource.MultiDataSource;

/**
 * 
 * BaseMultiDataSourceDao 多数据库操作DAO基类
 *
 */
public abstract class BaseMultiDataSourceDao<E> extends BaseDao<E> {
    public abstract String getDataSource();

    @Override
    protected Session getSession() {
        MultiDataSource.setDataSourceName(getDataSource());
        return sessionFactory.getCurrentSession();
    }
}
