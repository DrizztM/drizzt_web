package project.views.directive.log;

// Generated 2015-5-12 12:57:43 by SourceMaker

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import project.logic.service.log.LogLoginService;
import drizzt.common.base.BaseTemplateDirective;
import drizzt.common.handler.PageHandler;
import drizzt.common.handler.RenderHandler;

@Component
public class LogLoginListDirective extends BaseTemplateDirective {

    @Override
    public void execute(RenderHandler handler) throws IOException, Exception {
        PageHandler page = service.getPage(handler.getBoolean("result"), handler.getInteger("userId"), 
                handler.getString("name"), handler.getDate("startCreateDate"), handler.getDate("endCreateDate"), handler.getString("ip"), 
                handler.getString("orderField"), handler.getString("orderType"), handler.getInteger("pageIndex",1), handler.getInteger("count",20));
        handler.put("page", page).render();
    }

    @Autowired
    private LogLoginService service;

}