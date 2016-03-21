package project.logic.service.sys;

// Generated 2015-7-20 11:46:39 by SourceMaker

import static org.apache.commons.lang3.ArrayUtils.contains;
import static org.apache.commons.lang3.ArrayUtils.removeElement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import project.entities.sys.SysRoleUser;
import project.logic.dao.sys.SysRoleUserDao;
import drizzt.common.base.BaseService;
import drizzt.common.handler.PageHandler;

@Service
@Transactional
public class SysRoleUserService extends BaseService<SysRoleUser> {

    @Autowired
    private SysRoleUserDao dao;

    @Transactional(readOnly = true)
    public PageHandler getPage(Integer roleId, Integer userId, Integer pageIndex, Integer pageSize) {
        return dao.getPage(roleId, userId, pageIndex, pageSize);
    }

    public void dealRoleUsers(Integer userId, Integer[] roleIds) {
        @SuppressWarnings("unchecked")
        List<SysRoleUser> list = (List<SysRoleUser>) getPage(null, userId, null, null).getList();
        if (null != roleIds) {
            for (SysRoleUser roleUser : list) {
                if (!contains(roleIds, roleUser.getRoleId())) {
                    delete(roleUser.getId());
                    roleIds = removeElement(roleIds, roleUser.getRoleId());
                }
            }
            for (Integer roleId : roleIds) {
                save(new SysRoleUser(roleId, userId));
            }
        } else {
            deleteByUserId(userId);
        }
    }

    public int deleteByUserId(Integer userId) {
        return dao.deleteByUserId(userId);
    }

    public int deleteByRole(Integer roleId) {
        return dao.deleteByRole(roleId);
    }
}