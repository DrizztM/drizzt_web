package project.logic.dao.sys;

// Generated 2015-7-20 11:46:39 by SourceMaker

import org.springframework.stereotype.Repository;

import project.entities.sys.SysDept;
import drizzt.common.base.BaseDao;
import drizzt.common.handler.PageHandler;
import drizzt.common.handler.QueryHandler;

@Repository
public class SysDeptDao extends BaseDao<SysDept> {
    public PageHandler getPage(Integer parentId, Integer userId, String name, Integer pageIndex, Integer pageSize) {
        QueryHandler queryHandler = getQueryHandler("from SysDept bean");
        if (notEmpty(parentId)) {
            queryHandler.condition("(bean.parentId = :parentId)").setParameter("parentId", parentId);
        } else {
            queryHandler.condition("bean.parentId is null");
        }
        if (notEmpty(userId)) {
            queryHandler.condition("(bean.userId = :userId)").setParameter("userId", userId);
        }
        if (notEmpty(name)) {
            queryHandler.condition("(bean.name like :name)").setParameter("name", like(name));
        }
        queryHandler.order("bean.id desc");
        return getPage(queryHandler, pageIndex, pageSize);
    }

    @Override
    protected SysDept init(SysDept entity) {
        return entity;
    }

    @Override
    protected Class<SysDept> getEntityClass() {
        return SysDept.class;
    }

}