package project.logic.dao.sys;

// Generated 2015-7-20 11:46:39 by SourceMaker

import org.springframework.stereotype.Repository;

import project.entities.sys.SysRole;
import drizzt.common.base.BaseDao;
import drizzt.common.handler.PageHandler;
import drizzt.common.handler.QueryHandler;

@Repository
public class SysRoleDao extends BaseDao<SysRole> {
    public PageHandler getPage(Integer deptId, Integer pageIndex, Integer pageSize) {
        QueryHandler queryHandler = getQueryHandler("from SysRole bean");
        if (notEmpty(deptId)) {
            queryHandler.condition("(bean.deptId = :deptId)").setParameter("deptId", deptId);
        }
        queryHandler.order("bean.id desc");
        return getPage(queryHandler, pageIndex, pageSize);
    }

    @Override
    protected SysRole init(SysRole entity) {
        return entity;
    }

    @Override
    protected Class<SysRole> getEntityClass() {
        return SysRole.class;
    }

}