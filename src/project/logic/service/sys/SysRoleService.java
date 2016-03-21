package project.logic.service.sys;

// Generated 2015-7-20 11:46:39 by SourceMaker

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import project.entities.sys.SysRole;
import project.logic.dao.sys.SysRoleDao;
import drizzt.common.base.BaseService;
import drizzt.common.handler.PageHandler;

@Service
@Transactional
public class SysRoleService extends BaseService<SysRole> {

    @Autowired
    private SysRoleDao dao;

    @Transactional(readOnly = true)
    public PageHandler getPage(Integer deptId, Integer pageIndex, Integer pageSize) {
        return dao.getPage(deptId, pageIndex, pageSize);
    }

    @Transactional(readOnly = true)
    public boolean ownsAllRight(Integer[] roleIds) {
        List<SysRole> list = getEntitys(roleIds);
        for (SysRole role : list) {
            if (role.isOwnsAllRight()) {
                return true;
            }
        }
        return false;
    }
}