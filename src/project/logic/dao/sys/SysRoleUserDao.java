package project.logic.dao.sys;

// Generated 2015-7-20 11:46:39 by SourceMaker

import org.springframework.stereotype.Repository;

import project.entities.sys.SysRoleUser;
import drizzt.common.base.BaseDao;
import drizzt.common.handler.PageHandler;
import drizzt.common.handler.QueryHandler;

@Repository
public class SysRoleUserDao extends BaseDao<SysRoleUser> {
    public PageHandler getPage(Integer roleId, Integer userId, Integer pageIndex, Integer pageSize) {
        QueryHandler queryHandler = getQueryHandler("from SysRoleUser bean");
        if (notEmpty(roleId)) {
            queryHandler.condition("bean.roleId = :roleId").setParameter("roleId", roleId);
        }
        if (notEmpty(userId)) {
            queryHandler.condition("bean.userId = :userId").setParameter("userId", userId);
        }
        queryHandler.order("bean.id desc");
        return getPage(queryHandler, pageIndex, pageSize);
    }

    public int deleteByRole(Integer roleId) {
        if (notEmpty(roleId)) {
            QueryHandler queryHandler = getDeleteQueryHandler("from SysRoleUser bean where bean.roleId = :roleId");
            queryHandler.setParameter("roleId", roleId);
            return delete(queryHandler);
        } else {
            return 0;
        }
    }

    public int deleteByUserId(Integer userId) {
        if (notEmpty(userId)) {
            QueryHandler queryHandler = getDeleteQueryHandler("from SysRoleUser bean where bean.userId = :userId");
            queryHandler.setParameter("userId", userId);
            return delete(queryHandler);
        } else {
            return 0;
        }
    }
    
    @Override
    protected SysRoleUser init(SysRoleUser entity) {
        return entity;
    }

    @Override
    protected Class<SysRoleUser> getEntityClass() {
        return SysRoleUser.class;
    }

}