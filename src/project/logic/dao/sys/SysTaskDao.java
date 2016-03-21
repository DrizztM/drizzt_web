package project.logic.dao.sys;

// Generated 2015-7-3 16:18:22 by SourceMaker

import org.springframework.stereotype.Repository;

import project.entities.sys.SysTask;
import drizzt.common.base.BaseDao;
import drizzt.common.handler.PageHandler;
import drizzt.common.handler.QueryHandler;

@Repository
public class SysTaskDao extends BaseDao<SysTask> {
    public PageHandler getPage(Integer status, Integer pageIndex, Integer pageSize) {
        QueryHandler queryHandler = getQueryHandler("from SysTask bean");
        if (notEmpty(status)) {
            queryHandler.condition("bean.status = :status").setParameter("status", status);
        }
        queryHandler.order("bean.id desc");
        return getPage(queryHandler, pageIndex, pageSize);
    }

    @Override
    protected SysTask init(SysTask entity) {
        return entity;
    }

    @Override
    protected Class<SysTask> getEntityClass() {
        return SysTask.class;
    }

}