package project.logic.service.sys;

import static org.apache.commons.lang3.ArrayUtils.removeElement;
import static org.springframework.util.StringUtils.arrayToCommaDelimitedString;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import project.entities.log.LogLogin;
import project.entities.sys.SysUser;
import project.logic.dao.log.LogLoginDao;
import project.logic.dao.sys.SysUserDao;
import drizzt.common.base.BaseService;
import drizzt.common.handler.PageHandler;

@Service
@Transactional
public class SysUserService extends BaseService<SysUser> {
    @Autowired
    private SysUserDao dao;
    @Autowired
    private LogLoginDao logLoginDao;

    @Transactional(readOnly = true)
    public PageHandler getPage(Date startDateRegistered, Date endDateRegistered, Date startLastLoginDate, Date endLastLoginDate,
            Boolean superuserAccess, Boolean emailChecked, Integer deptId, String name, Boolean disabled, String orderField,
            String orderType, Integer pageIndex, Integer pageSize) {
        return dao.getPage(startDateRegistered, endDateRegistered, startLastLoginDate, endLastLoginDate, superuserAccess,
                emailChecked, deptId, name, disabled, orderField, orderType, pageIndex, pageSize);
    }

    public SysUser findByName(String name) {
        return dao.getEntity(name, "name");
    }

    public SysUser findByNickName(String nickName) {
        return dao.getEntity(nickName, "nickName");
    }

    public SysUser findByEmail(String email) {
        return dao.findByEmail(email);
    }

    public SysUser findByAuthToken(String authToken) {
        return dao.getEntity(authToken, "authToken");
    }

    public void updatePassword(Integer id, String password) {
        SysUser entity = dao.getEntity(id);
        if (notEmpty(entity)) {
            entity.setPassword(password);
        }
    }

    public SysUser updateLoginStatus(Integer id, String username, String authToken, String ip) {
        SysUser entity = dao.getEntity(id);
        if (notEmpty(entity)) {
            LogLogin log = new LogLogin();
            log.setName(username);
            log.setUserId(id);
            log.setResult(true);
            log.setIp(ip);
            log.setErrorPassword(null);
            logLoginDao.save(log);
            entity.setAuthToken(authToken);
            entity.setLastLoginDate(getDate());
            entity.setLastLoginIp(ip);
            entity.setLoginCount(entity.getLoginCount() + 1);
        }
        return entity;
    }

    public void deleteRoleIds(Integer userId, Integer roleId) {
        SysUser entity = dao.getEntity(userId);
        if (notEmpty(entity)) {
            String roles = entity.getRoles();
            String[] roleArray = split(roles, ',');
            removeElement(roleArray, roleId.toString());
            roles = arrayToCommaDelimitedString(roleArray);
            entity.setRoles(roles);
        }
    }

    public void checked(Integer userId, String email) {
        SysUser entity = dao.getEntity(userId);
        if (notEmpty(entity)) {
            entity.setEmail(email);
            entity.setEmailChecked(true);
        }
    }

    public SysUser updateStatus(Integer id, boolean status) {
        SysUser entity = dao.getEntity(id);
        if (notEmpty(entity)) {
            entity.setDisabled(status);
            entity.setAuthToken(null);
        }
        return entity;
    }
}
