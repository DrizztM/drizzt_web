package project.logic.service.sys;

// Generated 2015-7-22 13:48:39 by SourceMaker


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import project.entities.sys.SysMoudle;
import project.logic.dao.sys.SysMoudleDao;
import drizzt.common.base.BaseService;
import drizzt.common.handler.PageHandler;

@Service
@Transactional
public class SysMoudleService extends BaseService<SysMoudle> {

    @Autowired
    private SysMoudleDao dao;

    @Transactional(readOnly = true)
    public PageHandler getPage(Integer parentId, String url, 
                Integer pageIndex, Integer pageSize) {
        return dao.getPage(parentId, url, 
                pageIndex, pageSize);
    }
}