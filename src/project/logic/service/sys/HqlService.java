package project.logic.service.sys;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import project.logic.dao.sys.HqlDao;
import drizzt.common.base.BaseService;
import drizzt.common.handler.PageHandler;

@Service
@Transactional
public class HqlService extends BaseService<Object> {
    @Autowired
    private HqlDao dao;

    @Transactional(readOnly = true)
    public PageHandler getPage(String hql, Map<String, Object> paramters, Integer pageIndex, Integer pageSize) {
        return dao.getPage(hql, paramters, pageIndex, pageSize);
    }
}
